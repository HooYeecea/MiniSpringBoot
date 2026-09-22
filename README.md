# MiniSpring

A multi-module learning stack that mirrors the core Spring / Tomcat layering:

```text
Browser
  → MiniTomcat (BIO) / MiniTomcatNIO
      → Servlet API (com.web)
          → MiniMVC DispatcherServlet
              → MiniIOC beans (@Controller / @ControllerAdvice / ...)
```

Chinese version: [README(CN).md](README(CN).md)

## Modules

| Module | Artifact | Role |
|--------|----------|------|
| [MiniServletApi](MiniServletApi/) | `mini-servlet-api` | Shared `Servlet` / `HttpRequest` / `HttpResponse` contracts (`com.web`) |
| [MiniTomcat](MiniTomcat/) | `MiniTomcat` | BIO HTTP server implementing the API |
| [MiniTomcatNIO](MiniTomcatNIO/) | `MiniTomcatNIO` | NIO mini Tomcat implementing the API |
| [MiniIOCContainer](MiniIOCContainer/) | `MiniIOCContainer` | Annotation + XML IoC, scopes, lifecycle, `@MyValue`, simple AOP |
| [MiniMVC](MiniMVC/) | `mini-mvc` | SpringMVC-style front controller on top of IoC + Servlet API |

Parent POM (`packaging=pom`) only aggregates modules — **no Main in the root**.

## Requirements

- JDK 21
- Maven 3.6+

## Build everything

```bash
mvn clean install -DskipTests
```

## Run the full stack (recommended)

MiniMVC wires IoC + DispatcherServlet onto a Tomcat:

```bash
# BIO (default)
mvn -pl MiniMVC -am install -DskipTests
mvn -f MiniMVC/pom.xml exec:java

# NIO
mvn -f MiniMVC/pom.xml exec:java -Dexec.mainClass=com.mvc.demo.MvcNioApplication
```

Or run from the IDE:

- `com.mvc.demo.MvcApplication`
- `com.mvc.demo.MvcNioApplication`

Try:

```bash
curl http://localhost:8080/mvc/hello
curl http://localhost:8080/api/ping
curl http://localhost:8080/api/users/7
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"name\":\"Tom\",\"age\":20}"
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"name\":\"\",\"age\":0}"
```

## Run modules alone

```bash
# BIO Tomcat demos
mvn -pl MiniTomcat -am compile exec:java -Dexec.mainClass=com.minitomcat.HttpServer

# IoC demos
mvn -pl MiniIOCContainer -am compile exec:java -Dexec.mainClass=com.miniioccontainer.Main

# NIO Tomcat (run from MiniTomcatNIO so webapps/ is visible)
cd MiniTomcatNIO && mvn -q compile && java -cp target/classes cn.minitomcatnio.NioServer
```

## Mental model

| Layer | Responsibility |
|-------|----------------|
| Tomcat | Accept TCP, parse HTTP, call `Servlet.service` |
| Servlet API | Stable contracts shared by BIO / NIO / MVC |
| IoC | Create beans, inject dependencies, AOP proxies |
| MVC | One `DispatcherServlet` maps URLs to `@Controller` methods |

## Status

This practice edition covers the main learning path (container → IoC → MVC). It is **not** a Spring replacement (no Boot auto-config, no full Servlet 4, no AspectJ, etc.).

## License

Personal practice project.
