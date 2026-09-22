# MiniTomcat

A hand-written mini HTTP server for learning the core Tomcat request path:
listen on a port, parse an HTTP request line, match Filters by URL, dispatch through
`FilterChain` to a `Servlet`, then write the response back to the client.

It is **not** a full Servlet container — no `web.xml`, Session, or JSP.
The goal is to walk the path: accept → parse → filter → route → respond.

Chinese version: [README(CN).md](README(CN).md)

## Shared Servlet API

`Servlet`, `Filter`, `FilterChain`, `HttpRequest`, and `HttpResponse` live in a
separate module so BIO / NIO Tomcat and MiniMVC can share one contract:

| Item | Value |
|------|--------|
| Module | `MiniServletApi` (`mini-servlet-api`) |
| Package | `com.web` |
| Repository | [https://github.com/HooYeecea/MiniServletAPI](https://github.com/HooYeecea/MiniServletAPI) |

This project **implements** that API (`BioHttpRequest` / `BioHttpResponse`, filter chain, demos).
Application code should import types from `com.web`, not from a container-specific package.

Sibling NIO server: [MiniTomcatNIO](https://github.com/HooYeecea/MiniTomcatNIO)

Also a module of the parent [MiniSpring](../README.md) reactor. MiniMVC registers
`DispatcherServlet` via `HandleRequest.registerServlet(...)`.

## What it can do

- Listen on port `8080` for browser / `curl` requests
- Fixed-size thread pool; the main thread only `accept`s
- Parse request line, headers, query string, and request body (`Content-Length`)
- Route URLs to a `Servlet` via a simple map (`registerServlet` / `resetMappings`)
- Custom Servlets with a `service(req, resp)` style API
- `Filter` / `FilterChain` with URL patterns; call `chain.doFilter()` to continue
- Unmatched paths return `404 Not Found`

## Stack

- Java 21
- Maven
- JDK: `ServerSocket` / `Socket` / `ExecutorService`
- Depends on **`mini-servlet-api`** (shared contracts)

## Request flow

```
Browser / curl
    │
    ▼
HttpServer          listen 8080, accept → thread pool
    │
    ▼
HandleRequest       parse line/headers/body → BioHttpRequest → match Filters → route table
    │
    ▼
ApplicationFilterChain
    ├─ Filter1.doFilter → Filter2.doFilter → ...
    ├─ hit Servlet  → servlet.service(req, resp)
    └─ miss         → set 404
    │
    ▼
BioHttpResponse.write  build HTTP response, write socket, close
```

Typical steps for one request:

1. `HttpServer` blocks on `serverSocket.accept()`, then submits work to a 10-thread pool.
2. `HandleRequest` reads the request line, headers, and optional body.
3. Fills `BioHttpRequest` with method / url / version.
4. Matches Filters from `FILTER_MAPPINGS`, looks up Servlet in `SERVLET_MAP`.
5. Builds an `ApplicationFilterChain` and starts `chain.doFilter()`.
6. Each Filter may run before/after; skipping `chain.doFilter()` blocks the Servlet.
7. At the end of the chain: call `service()`, or write a 404 body.
8. `BioHttpResponse.write()` sends status, headers, and body.

## Core types

| Type | Role |
|------|------|
| `HttpServer` | Entry: `ServerSocket`, thread pool, accept loop |
| `HandleRequest` | Parse request, route table, Filter mappings, build chain |
| `BioHttpRequest` | Concrete request; implements `com.web.HttpRequest` |
| `BioHttpResponse` | Concrete response; implements `com.web.HttpResponse` |
| `Servlet` / `Filter` / `FilterChain` | From **mini-servlet-api** (`com.web`) |
| `ApplicationFilterChain` | FilterChain impl; tracks Filter position with `pos` |
| `FilterMapping` | Filter ↔ URL pattern (`/*`, `/xxx/*`, exact, `*.ext`) |
| `HelloServlet` / `TimeServlet` | Sample Servlets |
| `LogFilter` / `TimerFilter` / `HelloFilter` | Sample Filters |

Routes are registered in `HandleRequest`'s static block:

```java
SERVLET_MAP.put("/", (req, resp) -> resp.setBody("<h1>Welcome to MiniTomcat Home Page</h1>"));
SERVLET_MAP.put("/hello", new HelloServlet());
SERVLET_MAP.put("/time", new TimeServlet());

FILTER_MAPPINGS.add(new FilterMapping("/*", new LogFilter()));
FILTER_MAPPINGS.add(new FilterMapping("/*", new TimerFilter()));
FILTER_MAPPINGS.add(new FilterMapping("/hello", new HelloFilter()));
```

## Project layout

```
MiniTomcat
├── pom.xml
└── src/main/java/com/minitomcat
    ├── HttpServer.java
    ├── HandleRequest.java
    ├── BioHttpRequest.java
    ├── BioHttpResponse.java
    ├── ApplicationFilterChain.java
    ├── FilterMapping.java
    ├── HelloServlet.java
    ├── TimeServlet.java
    ├── LogFilter.java
    ├── TimerFilter.java
    └── HelloFilter.java
```

Shared API types are **not** in this tree; they come from [MiniServletAPI](https://github.com/HooYeecea/MiniServletAPI).

## Build & run

Requires **JDK 21** and **Maven**. Install `mini-servlet-api` first if you build this module alone
(`mvn install` from that repo or from the parent `mini-spring` reactor).

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.minitomcat.HttpServer"
```

Or run `com.minitomcat.HttpServer#main` from an IDE.

When you see:

```
Server is running on port 8080
```

the server is up.

## Built-in routes

| Path | Description |
|------|-------------|
| http://localhost:8080/ | Home welcome text |
| http://localhost:8080/hello | `Hello, MiniTomcat!` |
| http://localhost:8080/time | Server current time |
| other paths | `404 Not Found` |

```bash
curl http://localhost:8080/hello
curl http://localhost:8080/time
```

## Add your own Servlet

1. Implement `com.web.Servlet`:

```java
import com.web.HttpRequest;
import com.web.HttpResponse;
import com.web.Servlet;

public class PingServlet implements Servlet {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.setBody("<h1>pong</h1>");
    }
}
```

2. Register in `HandleRequest`:

```java
SERVLET_MAP.put("/ping", new PingServlet());
```

3. Restart and open `http://localhost:8080/ping`.

Lambdas work too; a separate class is optional.

## Add your own Filter

1. Implement `com.web.Filter`. Call `chain.doFilter()` to continue:

```java
import com.web.*;

public class AuthFilter implements Filter {
    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception {
        // before: auth, logging, ...
        chain.doFilter(request, response);
        // after: timing, tweak response, ...
    }
}
```

2. Register URL patterns in order in `HandleRequest`:

```java
FILTER_MAPPINGS.add(new FilterMapping("/*", new AuthFilter()));
```

| Pattern | Meaning |
|---------|---------|
| `/*` | All paths |
| `/hello` | Exact match |
| `/api/*` | `/api` and `/api/...` |
| `*.do` | Extension match |

Registration order is execution order (onion model).

## Gaps vs real Tomcat

Intentionally small for now:

- Cookie parsing is minimal / incomplete vs real Servlet containers
- No `web.xml` / annotation scan; routes and Filters are registered in code
- No Session, Listener, JSP
- No static resource root; responses are primarily HTML text
- Connection closed after each response (no Keep-Alive)
- Status codes are basic (200 / 404 / whatever apps set)

Those gaps are natural next steps for practice.

## License

Personal practice project for learning Tomcat-style request handling.
