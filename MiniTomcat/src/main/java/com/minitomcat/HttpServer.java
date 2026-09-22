package com.minitomcat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server is running on port 8080");
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        // 等待连接
        while (true) {
            // 等待连接，阻塞等待，直到有连接进来
            Socket socket = serverSocket.accept();
            System.out.println("连接进来了！");
            // 提交任务到线程池
            // 线程池会自动创建线程，处理任务,不会阻塞主线程
            threadPool.submit(() -> HandleRequest.handleRequest(socket));
        }
    }
}
