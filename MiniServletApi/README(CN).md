# MiniServletApi

MiniSpring 学习栈共用的 Servlet / HTTP 契约层。

英文版：[README.md](README.md)

**仓库：** [https://github.com/HooYeecea/MiniServletAPI](https://github.com/HooYeecea/MiniServletAPI)

同时是父工程 [MiniSpring](../README(CN).md) 中的一个模块。

## 为什么要单独抽模块

`MiniTomcat`（BIO）和 `MiniTomcatNIO` 是两套不同的连接器 / 容器实现。
`MiniMVC` 不该关心运行时具体用哪一个。

本模块只放**面向应用的 API**：

- `Servlet`、`Filter`、`FilterChain`
- `HttpRequest`、`HttpResponse`
- `ServletConfig`、`RequestDispatcher`、`HttpSession`、`DispatcherType`

包名：`com.web`  
Maven 坐标：`com.minitomcat:mini-servlet-api`

具体实现留在各自服务器工程里。MVC 和应用代码只依赖本 API。

## 谁在用

| 项目 | 角色 |
|------|------|
| [MiniTomcat](../MiniTomcat/) | BIO 迷你 HTTP 服务器，实现本 API |
| [MiniTomcatNIO](../MiniTomcatNIO/) | NIO 迷你 Tomcat，实现本 API |
| [MiniMVC](../MiniMVC/) | SpringMVC 风格层；只依赖本 API，不绑死某个 Tomcat |

## 构建

父工程：

```bash
mvn -pl MiniServletApi clean install
```

或在本模块目录：

```bash
mvn clean install
```

其它模块依赖：

```xml
<dependency>
    <groupId>com.minitomcat</groupId>
    <artifactId>mini-servlet-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 目录结构

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

个人练习项目，用于学习 Spring / Tomcat 风格分层。
