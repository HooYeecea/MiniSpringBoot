# MiniSpring

多模块学习栈，对应 Spring / Tomcat 的核心分层：

```text
浏览器
  → MiniTomcat（BIO）/ MiniTomcatNIO
      → Servlet API（com.web）
          → MiniMVC DispatcherServlet
              → MiniIOC Bean（@Controller / @ControllerAdvice / ...）
```

英文版：[README.md](README.md)

## 模块

| 模块 | 坐标 | 职责 |
|------|------|------|
| [MiniServletApi](MiniServletApi/) | `mini-servlet-api` | 共享 `Servlet` / `HttpRequest` / `HttpResponse` 契约（`com.web`） |
| [MiniTomcat](MiniTomcat/) | `MiniTomcat` | BIO HTTP 服务器，实现上述 API |
| [MiniTomcatNIO](MiniTomcatNIO/) | `MiniTomcatNIO` | NIO 迷你 Tomcat，实现上述 API |
| [MiniIOCContainer](MiniIOCContainer/) | `MiniIOCContainer` | 注解 + XML IoC、作用域、生命周期、`@MyValue`、简单 AOP |
| [MiniMVC](MiniMVC/) | `mini-mvc` | 基于 IoC + Servlet API 的 SpringMVC 风格前端控制器 |

父 POM（`packaging=pom`）只做聚合，**根目录没有 Main**。

## 环境

- JDK 21
- Maven 3.6+

## 整体构建

```bash
mvn clean install -DskipTests
```

## 跑完整栈（推荐）

MiniMVC 把 IoC + DispatcherServlet 挂到 Tomcat 上：

```bash
# BIO（默认）
mvn -pl MiniMVC -am install -DskipTests
mvn -f MiniMVC/pom.xml exec:java

# NIO
mvn -f MiniMVC/pom.xml exec:java -Dexec.mainClass=com.mvc.demo.MvcNioApplication
```

或在 IDE 运行：

- `com.mvc.demo.MvcApplication`
- `com.mvc.demo.MvcNioApplication`

试一下：

```bash
curl http://localhost:8080/mvc/hello
curl http://localhost:8080/api/ping
curl http://localhost:8080/api/users/7
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"name\":\"Tom\",\"age\":20}"
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"name\":\"\",\"age\":0}"
```

## 单独跑各模块

```bash
# BIO Tomcat 演示
mvn -pl MiniTomcat -am compile exec:java -Dexec.mainClass=com.minitomcat.HttpServer

# IoC 演示
mvn -pl MiniIOCContainer -am compile exec:java -Dexec.mainClass=com.miniioccontainer.Main

# NIO Tomcat（在 MiniTomcatNIO 目录执行，以便看到 webapps/）
cd MiniTomcatNIO && mvn -q compile && java -cp target/classes cn.minitomcatnio.NioServer
```

## 怎么理解分层

| 层 | 职责 |
|----|------|
| Tomcat | 接 TCP、解析 HTTP、调用 `Servlet.service` |
| Servlet API | BIO / NIO / MVC 共用的稳定契约 |
| IoC | 创建 Bean、注入依赖、AOP 代理 |
| MVC | 一个 `DispatcherServlet` 把 URL 分发给 `@Controller` 方法 |

## 状态

练习版已覆盖主学习路径（容器 → IoC → MVC）。**不是** Spring 替代品（无 Boot 自动配置、无完整 Servlet 4、无 AspectJ 等）。

## License

个人练习项目。
