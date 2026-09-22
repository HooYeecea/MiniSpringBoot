# MiniMVC

基于 `mini-servlet-api` + `MiniIOCContainer` 的迷你 SpringMVC 风格层。

英文版：[README.md](README.md)

属于父工程 [MiniSpring](../README(CN).md) 多模块中的一环。

## 做什么

把一个 `DispatcherServlet`（前端控制器）挂到 Tomcat 上：

1. 从 IoC 扫描 `@Controller` / `@RestController` / `@ControllerAdvice`
2. 把 HTTP 请求映射到处理方法
3. 解析方法参数、写出返回值
4. 跑拦截器与统一异常处理

## 处理管道

```text
DispatcherServlet
  → HandlerMapping（@RequestMapping）
  → 拦截器（preHandle / postHandle / afterCompletion）
  → HandlerAdapter
       → 参数解析器链
            （@RequestParam / @PathVariable / HttpRequest|HttpResponse /
             @RequestBody + @Valid）
       → 调用 Controller 方法
       → 返回值处理器链
            （@ResponseBody / @RestController → Jackson JSON；
             String / Object → 文本）
  → 异常时：@ControllerAdvice + @ExceptionHandler
```

## 注解与校验

| 能力 | 说明 |
|------|------|
| `@Controller` / `@RestController` / `@RequestMapping` | 映射 |
| `@RequestParam` / `@PathVariable` / `@RequestBody` | 参数 |
| `@ResponseBody` | JSON 返回 |
| `@Valid` + `@NotNull` / `@NotBlank` / `@Min` / `@Max` | 迷你校验（非 JSR-380） |
| `@ControllerAdvice` / `@ExceptionHandler` | 全局异常 |

## Demo 路由

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/mvc/hello` | 纯文本 |
| GET | `/mvc/echo?name=...` | `@RequestParam` |
| GET | `/mvc/add?a=1&b=2` | 类型转换 |
| GET | `/api/ping` | JSON |
| GET | `/api/user?id=7` | JSON |
| GET | `/api/users/{id}` | `@PathVariable` |
| POST | `/api/users` | `@RequestBody` + `@Valid` |

非法 body / JSON → `GlobalExceptionHandler` 返回 `400` JSON。

## 运行

父工程（推荐）：

```bash
mvn -pl MiniMVC -am install -DskipTests

# BIO（默认 mainClass）
mvn -f MiniMVC/pom.xml exec:java

# NIO
mvn -f MiniMVC/pom.xml exec:java -Dexec.mainClass=com.mvc.demo.MvcNioApplication
```

IDE：`com.mvc.demo.MvcApplication` 或 `com.mvc.demo.MvcNioApplication`。

## 试一下

```bash
curl http://localhost:8080/mvc/hello
curl "http://localhost:8080/mvc/echo?name=MiniSpring"
curl http://localhost:8080/api/ping
curl http://localhost:8080/api/users/7
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"name\":\"Tom\",\"age\":20}"
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"name\":\"\",\"age\":0}"
```

## 目录结构

```text
com.mvc
├── annotation/     # @Controller、@RequestMapping、@RequestBody、@Valid …
├── servlet/        # DispatcherServlet
├── handler/        # 映射、适配器、参数/返回值链
├── interceptor/    # HandlerInterceptor、MappedInterceptor
├── exception/      # HandlerExceptionResolver、@ExceptionHandler 支持
├── validation/     # MiniValidator + 约束注解
└── demo/           # MvcApplication、Controller、GlobalExceptionHandler
```

## 依赖

- `mini-servlet-api`
- `MiniIOCContainer`
- `MiniTomcat` / `MiniTomcatNIO`（演示运行时）
- Jackson `jackson-databind`

## License

个人练习项目。
