# MiniSpring / MiniSpringBoot

多模块学习栈：自底向上实现迷你 Tomcat → IoC → MVC → **迷你 Spring Boot**。

```text
浏览器
  → MiniBoot（Environment / 自动配置 / EmbeddedServer）
      → MiniTomcat（BIO）或 MiniTomcatNIO
          → Servlet API（com.web）
              → MiniMVC DispatcherServlet
                  → MiniIOC Bean（@Controller / @ControllerAdvice / ...）
```

英文版：[README.md](README.md)

## 模块

| 模块 | 坐标 | 职责 |
|------|------|------|
| [MiniServletApi](MiniServletApi/) | `mini-servlet-api` | 共享 `Servlet` / `HttpRequest` / `HttpResponse` 契约（`com.web`） |
| [MiniTomcat](MiniTomcat/) | `MiniTomcat` | BIO HTTP 服务器 |
| [MiniTomcatNIO](MiniTomcatNIO/) | `MiniTomcatNIO` | NIO 迷你 Tomcat |
| [MiniIOCContainer](MiniIOCContainer/) | `MiniIOCContainer` | 注解 + XML IoC、作用域、生命周期、`@MyValue`、简单 AOP |
| [MiniMVC](MiniMVC/) | `mini-mvc` | SpringMVC 风格 `DispatcherServlet` |
| [MiniBoot](MiniBoot/) | `mini-boot` | Boot 核心：`MiniSpringApplication.run`、自动配置、Environment、EmbeddedServer |
| [MiniBootStarterWeb](MiniBootStarterWeb/) | `mini-boot-starter-web` | Web Starter：只拉这一条依赖即可上手 |
| [MiniBootDemo](MiniBootDemo/) | `mini-boot-demo` | 示例应用（只依赖 starter-web） |

父 POM（`packaging=pom`）只做聚合，**根目录没有 Main**。

## 环境

- JDK 21
- Maven 3.6+

## 整体构建

```bash
mvn clean install -DskipTests
```

## 跑迷你 Boot（推荐）

应用只依赖 `mini-boot-starter-web`，一行启动：

```bash
mvn -pl MiniBootDemo -am install -DskipTests

# BIO（默认）
mvn -f MiniBootDemo/pom.xml exec:java

# NIO
mvn -f MiniBootDemo/pom.xml exec:java "-Dexec.mainClass=com.miniboot.demo.MiniBootNioApplication"

# 命令行覆盖端口
mvn -f MiniBootDemo/pom.xml exec:java "-Dexec.args=--server.port=18080"
```

IDE 运行：

- `com.miniboot.demo.MiniBootApplication`
- `com.miniboot.demo.MiniBootNioApplication`

试一下：

```bash
curl http://localhost:8080/mvc/hello
curl http://localhost:8080/api/ping
curl http://localhost:8080/api/users/7
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"name\":\"Tom\",\"age\":20}"
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"name\":\"\",\"age\":0}"
```

### MiniBoot 能力速览

| 能力 | 说明 |
|------|------|
| `@MiniSpringBootApplication` | 入口注解（含 `@EnableAutoConfiguration`） |
| `MiniSpringApplication.run` | 扫包 → 自动配置 → 启动嵌入式服务器 |
| `EmbeddedServer` | 可切换 `BIO` / `NIO`（注解或 `server.type`） |
| `META-INF/miniboot.factories` | 类似 `spring.factories` 的自动配置列表 |
| `@ConditionalOnClass` | 按 classpath 条件启用自动配置 |
| `Environment` | `application.properties`、profile、`--key=value` |
| `@ConfigurationProperties` | 前缀绑定（如 `server.port`、`app.name`） |

配置示例见 [MiniBootDemo/src/main/resources/application.properties](MiniBootDemo/src/main/resources/application.properties)。

## 跑 MVC 手工装配（对照学习）

不经 Boot，直接在 MiniMVC demo 里手工挂 Tomcat：

```bash
mvn -pl MiniMVC -am install -DskipTests
mvn -f MiniMVC/pom.xml exec:java
# NIO: mvn -f MiniMVC/pom.xml exec:java -Dexec.mainClass=com.mvc.demo.MvcNioApplication
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
| Boot | 编排启动：Environment、自动配置、嵌入式服务器 |
| Tomcat | 接 TCP、解析 HTTP、调用 `Servlet.service` |
| Servlet API | BIO / NIO / MVC 共用的稳定契约 |
| IoC | 创建 Bean、注入依赖、AOP 代理 |
| MVC | 一个 `DispatcherServlet` 把 URL 分发给 `@Controller` 方法 |

## 状态

练习版已覆盖：容器 → IoC → MVC → **迷你 Boot（run / 自动配置 / Environment / starter-web）**。  
仍是教学项目，**不是** Spring 替代品（无完整条件注解体系、无 Actuator、无 fat-jar 插件、无 AspectJ 等）。

## License

个人练习项目。
