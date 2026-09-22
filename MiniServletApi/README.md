# MiniServletApi

Shared Servlet / HTTP contracts for the MiniSpring learning stack.

Chinese version: [README(CN).md](README(CN).md)

**Repository:** [https://github.com/HooYeecea/MiniServletAPI](https://github.com/HooYeecea/MiniServletAPI)

Also a module of the parent [MiniSpring](../README.md) reactor.

## Why this module exists

`MiniTomcat` (BIO) and `MiniTomcatNIO` are two different connectors / containers.
`MiniMVC` should not care which one is used at runtime.

This module holds only the **application-facing API**:

- `Servlet`, `Filter`, `FilterChain`
- `HttpRequest`, `HttpResponse`
- `ServletConfig`, `RequestDispatcher`, `HttpSession`, `DispatcherType`

Package: `com.web`  
Maven artifact: `com.minitomcat:mini-servlet-api`

Implementations stay in each server project. MVC and app code depend on this API only.

## Consumers

| Project | Role |
|---------|------|
| [MiniTomcat](../MiniTomcat/) | BIO mini HTTP server; implements the API |
| [MiniTomcatNIO](../MiniTomcatNIO/) | NIO mini Tomcat; implements the API |
| [MiniMVC](../MiniMVC/) | SpringMVC-style layer; depends on the API, not a specific Tomcat |

## Build

From the parent reactor:

```bash
mvn -pl MiniServletApi clean install
```

Or inside this module:

```bash
mvn clean install
```

Other modules depend on:

```xml
<dependency>
    <groupId>com.minitomcat</groupId>
    <artifactId>mini-servlet-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Layout

```text
com.web
├── Servlet
├── Filter
├── FilterChain
├── HttpRequest
├── HttpResponse
├── ServletConfig
├── RequestDispatcher
├── HttpSession
└── DispatcherType
```

## License

Personal practice project for learning Spring / Tomcat-style layering.
