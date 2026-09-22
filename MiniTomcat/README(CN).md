# MiniTomcat

一个手写的迷你 HTTP 服务器，用来理解 Tomcat 在处理请求时最核心的那几步：监听端口、解析 HTTP 报文、按路径匹配 Filter、经 FilterChain 分发到 Servlet、再把响应写回客户端。

它不是完整的 Servlet 容器，没有 `web.xml`、Session、JSP。目标是把「请求是怎么被接住、拆开、过滤、路由、响应」这条链路走通。

英文版：[README.md](README.md)

## 共享 Servlet API

`Servlet`、`Filter`、`FilterChain`、`HttpRequest`、`HttpResponse` 等契约已抽到独立模块，
方便 BIO / NIO 两套 Tomcat 以及后续 MiniMVC 共用同一套接口：

| 项 | 说明 |
|----|------|
| 模块 | `MiniServletApi`（`mini-servlet-api`） |
| 包名 | `com.web` |
| 仓库 | [https://github.com/HooYeecea/MiniServletAPI](https://github.com/HooYeecea/MiniServletAPI) |

本工程**实现**该 API（`BioHttpRequest` / `BioHttpResponse`、过滤链、示例 Servlet/Filter）。
写业务时请从 `com.web` 引入类型，而不是依赖某个容器私有包。

NIO 兄弟项目：[MiniTomcatNIO](https://github.com/HooYeecea/MiniTomcatNIO)

同时是父工程 [MiniSpring](../README(CN).md) 中的模块。MiniMVC 通过
`HandleRequest.registerServlet(...)` 注册 `DispatcherServlet`。

## 它能做什么

- 监听 `8080` 端口，接收浏览器或 `curl` 发来的 HTTP 请求
- 用固定大小线程池并发处理连接，主线程只负责 `accept`
- 解析请求行、Header、Query、以及按 `Content-Length` 读取请求体
- 用一张路由表把 URL 映射到 `Servlet`（`registerServlet` / `resetMappings`）
- 支持自定义 Servlet，写法接近 Java Servlet 的 `service(req, resp)`
- 支持 `Filter` / `FilterChain`：按 URL 模式匹配，递归调用 `chain.doFilter()`，链尾才执行 Servlet
- 未匹配路径返回 `404 Not Found`

## 技术栈

- Java 21
- Maven
- 标准库：`ServerSocket` / `Socket` / `ExecutorService`
- 依赖 **`mini-servlet-api`**（共享契约）

## 请求处理流程

```
浏览器 / curl
    │
    ▼
HttpServer          监听 8080，accept 后把 Socket 丢进线程池
    │
    ▼
HandleRequest       读请求行/头/体 → 封装 BioHttpRequest → 匹配 Filter → 查路由表
    │
    ▼
ApplicationFilterChain
    ├─ Filter1.doFilter → Filter2.doFilter → ...
    ├─ 命中 Servlet  → servlet.service(req, resp)
    └─ 未命中        → 设置 404
    │
    ▼
BioHttpResponse.write  拼 HTTP 响应头 + 正文，写回 Socket 并关闭连接
```

一次请求的大致步骤：

1. `HttpServer` 阻塞在 `serverSocket.accept()`，有连接进来后提交给 10 线程的线程池。
2. `HandleRequest` 读取请求行、Header，以及可选的请求体。
3. 拆成方法、路径、协议版本，填进 `BioHttpRequest`。
4. 用 URL 去 `FILTER_MAPPINGS` 里找出所有匹配的 Filter，再去 `SERVLET_MAP` 里找 Servlet。
5. 为这次请求新建一条 `ApplicationFilterChain`，从头调用 `chain.doFilter()`。
6. 每个 Filter 可以在前后插入逻辑；不调用 `chain.doFilter()` 就会拦截，Servlet 不会执行。
7. 链走到末尾：找到 Servlet 就调用 `service()`，找不到就写 404 页面。
8. `BioHttpResponse.write()` 按 HTTP/1.1 格式写出状态行、响应头和正文。

## 核心类

| 类 | 职责 |
| --- | --- |
| `HttpServer` | 入口。创建 `ServerSocket`、线程池，循环接受连接 |
| `HandleRequest` | 解析请求、维护路由表和 Filter 映射、组装 FilterChain |
| `BioHttpRequest` | 具体请求对象，实现 `com.web.HttpRequest` |
| `BioHttpResponse` | 具体响应对象，实现 `com.web.HttpResponse` |
| `Servlet` / `Filter` / `FilterChain` | 来自 **mini-servlet-api**（`com.web`） |
| `ApplicationFilterChain` | FilterChain 实现，用 `pos` 记录当前走到第几个 Filter |
| `FilterMapping` | Filter 与 URL 模式的绑定，支持 `/*`、`/xxx/*`、精确路径、扩展名 |
| `HelloServlet` | `/hello` 示例 |
| `TimeServlet` | `/time` 示例，返回当前时间 |
| `LogFilter` | 打印请求进入 / 离开，映射 `/*` |
| `TimerFilter` | 统计整条链耗时，映射 `/*` |
| `HelloFilter` | 只拦截 `/hello`，用来演示 URL 匹配 |

路由注册在 `HandleRequest` 的静态块里：

```java
SERVLET_MAP.put("/", (req, resp) -> resp.setBody("<h1>Welcome to MiniTomcat Home Page</h1>"));
SERVLET_MAP.put("/hello", new HelloServlet());
SERVLET_MAP.put("/time", new TimeServlet());

FILTER_MAPPINGS.add(new FilterMapping("/*", new LogFilter()));
FILTER_MAPPINGS.add(new FilterMapping("/*", new TimerFilter()));
FILTER_MAPPINGS.add(new FilterMapping("/hello", new HelloFilter()));
```

## 项目结构

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

共享 API 类型不在本仓库中，见 [MiniServletAPI](https://github.com/HooYeecea/MiniServletAPI)。

## 如何运行

需要本机已安装 **JDK 21** 和 **Maven**。若单独编译本模块，请先安装 `mini-servlet-api`
（在该仓库或父工程 `mini-spring` 下执行 `mvn install`）。

```bash
# 在项目根目录编译
mvn compile

# 启动服务器
mvn exec:java -Dexec.mainClass="com.minitomcat.HttpServer"
```

也可以在 IDE 里直接运行 `com.minitomcat.HttpServer` 的 `main` 方法。

控制台出现下面这行就说明已经起来了：

```
Server is running on port 8080
```

## 内置路由

启动后访问：

| 路径 | 说明 |
| --- | --- |
| http://localhost:8080/ | 首页欢迎语 |
| http://localhost:8080/hello | `Hello, MiniTomcat!` |
| http://localhost:8080/time | 服务器当前时间 |
| 其他路径 | `404 Not Found` |

用 curl 也可以：

```bash
curl http://localhost:8080/hello
curl http://localhost:8080/time
```

## 如何加一个自己的 Servlet

1. 实现 `com.web.Servlet` 接口：

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

2. 在 `HandleRequest` 的静态块里注册路径：

```java
SERVLET_MAP.put("/ping", new PingServlet());
```

3. 重启服务器，访问 `http://localhost:8080/ping`。

也可以直接用 lambda 注册，不必单独建类。

## 如何加一个自己的 Filter

1. 实现 `com.web.Filter` 接口。想继续往下走就必须调用 `chain.doFilter()`：

```java
import com.web.*;

public class AuthFilter implements Filter {
    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception {
        // 前置逻辑：鉴权、记日志、改请求……
        chain.doFilter(request, response);
        // 后置逻辑：统计耗时、改响应……
    }
}
```

如果这里不调用 `chain.doFilter()`，后面的 Filter 和 Servlet 都不会执行，相当于拦截。

2. 在 `HandleRequest` 的静态块里按顺序注册 URL 模式：

```java
FILTER_MAPPINGS.add(new FilterMapping("/*", new AuthFilter()));
```

支持的 URL 模式：

| 模式 | 含义 |
| --- | --- |
| `/*` | 匹配所有路径 |
| `/hello` | 精确匹配 |
| `/api/*` | 匹配 `/api` 以及 `/api/...` |
| `*.do` | 按扩展名匹配 |

3. 重启服务器。访问 `/hello` 时控制台大致会看到：

```
[LogFilter] 进入: GET /hello
[HelloFilter] 命中 /hello，准备进入 HelloServlet
[TimerFilter] /hello 耗时 1 ms
[LogFilter] 离开: /hello
```

注册顺序就是执行顺序：先注册的 Filter 先进入、后离开（洋葱模型）。

## 和真实 Tomcat 差在哪

当前版本刻意做小，下面这些都还没有：

- Cookie 解析相对真实容器仍不完整
- 没有 `web.xml` / 注解扫描，路由和 Filter 靠代码里手动注册
- 没有 Session、Listener、JSP
- 没有静态资源目录，响应以 HTML 文本为主
- 连接处理完就关闭，没有 Keep-Alive
- 状态码由应用自行设置，容器侧主要保证基本响应能力

这些限制正好对应下一步可以动手扩展的方向。

## License

个人练习项目，用于学习 Tomcat 风格的请求处理。
