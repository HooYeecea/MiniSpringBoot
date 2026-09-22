# MiniMVC

A minimal SpringMVC-style layer on top of `mini-servlet-api` + `MiniIOCContainer`.

Chinese version: [README(CN).md](README(CN).md)

Part of the parent [MiniSpring](../README.md) reactor.

## What it does

One `DispatcherServlet` (front controller) registered on Tomcat:

1. Scan IoC beans for `@Controller` / `@RestController` / `@ControllerAdvice`
2. Map HTTP requests to handler methods
3. Resolve method arguments and write return values
4. Run interceptors and unified exception handlers

## Pipeline

```text
DispatcherServlet
  → HandlerMapping (@RequestMapping)
  → Interceptors (preHandle / postHandle / afterCompletion)
  → HandlerAdapter
       → ArgumentResolver chain
            (@RequestParam / @PathVariable / HttpRequest|HttpResponse /
             @RequestBody + @Valid)
       → invoke controller method
       → ReturnValueHandler chain
            (@ResponseBody / @RestController → JSON via Jackson;
             String / Object → text)
  → on error: @ControllerAdvice + @ExceptionHandler
```

## Annotations & validation

| Piece | Notes |
|-------|--------|
| `@Controller` / `@RestController` / `@RequestMapping` | Mapping |
| `@RequestParam` / `@PathVariable` / `@RequestBody` | Arguments |
| `@ResponseBody` | JSON return |
| `@Valid` + `@NotNull` / `@NotBlank` / `@Min` / `@Max` | Mini validator (not JSR-380) |
| `@ControllerAdvice` / `@ExceptionHandler` | Global errors |

## Demo routes

| Method | Path | Notes |
|--------|------|--------|
| GET | `/mvc/hello` | plain text |
| GET | `/mvc/echo?name=...` | `@RequestParam` |
| GET | `/mvc/add?a=1&b=2` | type conversion |
| GET | `/api/ping` | JSON |
| GET | `/api/user?id=7` | JSON |
| GET | `/api/users/{id}` | `@PathVariable` |
| POST | `/api/users` | `@RequestBody` + `@Valid` |

Invalid body / JSON → `400` JSON from `GlobalExceptionHandler`.

## Run

Parent reactor (recommended):

```bash
mvn -pl MiniMVC -am install -DskipTests

# BIO (default mainClass)
mvn -f MiniMVC/pom.xml exec:java

# NIO
mvn -f MiniMVC/pom.xml exec:java -Dexec.mainClass=com.mvc.demo.MvcNioApplication
```

IDE: `com.mvc.demo.MvcApplication` or `com.mvc.demo.MvcNioApplication`.

## Try

```bash
curl http://localhost:8080/mvc/hello
curl "http://localhost:8080/mvc/echo?name=MiniSpring"
curl http://localhost:8080/api/ping
curl http://localhost:8080/api/users/7
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"name\":\"Tom\",\"age\":20}"
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"name\":\"\",\"age\":0}"
```

## Layout

```text
com.mvc
├── annotation/     # @Controller, @RequestMapping, @RequestBody, @Valid, ...
├── servlet/        # DispatcherServlet
├── handler/        # mapping, adapter, argument / return-value chains
├── interceptor/    # HandlerInterceptor, MappedInterceptor
├── exception/      # HandlerExceptionResolver, @ExceptionHandler support
├── validation/     # MiniValidator + constraint annotations
└── demo/           # MvcApplication, controllers, GlobalExceptionHandler
```

## Dependencies

- `mini-servlet-api`
- `MiniIOCContainer`
- `MiniTomcat` / `MiniTomcatNIO` (demo runtimes)
- Jackson `jackson-databind`

## License

Personal practice project.
