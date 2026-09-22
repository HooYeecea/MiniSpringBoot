# MiniSpring / MiniSpringBoot

A multi-module learning stack: mini Tomcat → IoC → MVC → **mini Spring Boot**, bottom-up.

```text
Browser
  → MiniBoot (Environment / auto-config / EmbeddedServer)
      → MiniTomcat (BIO) or MiniTomcatNIO
          → Servlet API (com.web)
              → MiniMVC DispatcherServlet
                  → MiniIOC beans (@Controller / @ControllerAdvice / ...)
```

Chinese version: [README(CN).md](README(CN).md)

## Modules

| Module | Artifact | Role |
|--------|----------|------|
| [MiniServletApi](MiniServletApi/) | `mini-servlet-api` | Shared `Servlet` / `HttpRequest` / `HttpResponse` contracts (`com.web`) |
| [MiniTomcat](MiniTomcat/) | `MiniTomcat` | BIO HTTP server |
| [MiniTomcatNIO](MiniTomcatNIO/) | `MiniTomcatNIO` | NIO mini Tomcat |
| [MiniIOCContainer](MiniIOCContainer/) | `MiniIOCContainer` | Annotation + XML IoC, scopes, lifecycle, `@MyValue`, simple AOP |
| [MiniMVC](MiniMVC/) | `mini-mvc` | SpringMVC-style `DispatcherServlet` |
| [MiniBoot](MiniBoot/) | `mini-boot` | Boot core: `MiniSpringApplication.run`, auto-config, Environment, EmbeddedServer |
| [MiniBootStarterWeb](MiniBootStarterWeb/) | `mini-boot-starter-web` | Web starter — one dependency to get started |
| [MiniBootDemo](MiniBootDemo/) | `mini-boot-demo` | Sample app (depends only on starter-web) |

Parent POM (`packaging=pom`) only aggregates modules — **no Main in the root**.

## Requirements

- JDK 21
- Maven 3.6+

## Build everything

```bash
mvn clean install -DskipTests
```

## Run mini Boot (recommended)

The sample app depends only on `mini-boot-starter-web` and starts with one line:

```bash
mvn -pl MiniBootDemo -am install -DskipTests

# BIO (default)
mvn -f MiniBootDemo/pom.xml exec:java

# NIO
mvn -f MiniBootDemo/pom.xml exec:java "-Dexec.mainClass=com.miniboot.demo.MiniBootNioApplication"

# Override port from the CLI
mvn -f MiniBootDemo/pom.xml exec:java "-Dexec.args=--server.port=18080"
```

Or run from the IDE:

- `com.miniboot.demo.MiniBootApplication`
- `com.miniboot.demo.MiniBootNioApplication`

Try:

```bash
curl http://localhost:8080/mvc/hello
curl http://localhost:8080/api/ping
curl http://localhost:8080/api/users/7
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"name\":\"Tom\",\"age\":20}"
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"name\":\"\",\"age\":0}"
```

### MiniBoot feature snapshot

| Feature | Notes |
|---------|--------|
| `@MiniSpringBootApplication` | Entry annotation (includes `@EnableAutoConfiguration`) |
| `MiniSpringApplication.run` | Scan → auto-config → start embedded server |
| `EmbeddedServer` | Switch `BIO` / `NIO` (annotation or `server.type`) |
| `META-INF/miniboot.factories` | `spring.factories`-style auto-config list |
| `@ConditionalOnClass` | Enable auto-config when classes are on the classpath |
| `Environment` | `application.properties`, profiles, `--key=value` |
| `@ConfigurationProperties` | Prefix binding (e.g. `server.port`, `app.name`) |

See [MiniBootDemo/src/main/resources/application.properties](MiniBootDemo/src/main/resources/application.properties).

## Run MVC manual wiring (for comparison)

Wire Tomcat by hand in the MiniMVC demo (no Boot layer):

```bash
mvn -pl MiniMVC -am install -DskipTests
mvn -f MiniMVC/pom.xml exec:java
# NIO: mvn -f MiniMVC/pom.xml exec:java -Dexec.mainClass=com.mvc.demo.MvcNioApplication
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
| Boot | Orchestrate startup: Environment, auto-config, embedded server |
| Tomcat | Accept TCP, parse HTTP, call `Servlet.service` |
| Servlet API | Stable contracts shared by BIO / NIO / MVC |
| IoC | Create beans, inject dependencies, AOP proxies |
| MVC | One `DispatcherServlet` maps URLs to `@Controller` methods |

## Status

This practice edition covers: container → IoC → MVC → **mini Boot (run / auto-config / Environment / starter-web)**.  
It is still a teaching project, **not** a Spring replacement (no full condition system, no Actuator, no fat-jar plugin, no AspectJ, etc.).

## License

Personal practice project.
