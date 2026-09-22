package com.minitomcat;

import com.web.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class BioHttpResponse implements HttpResponse {
    private final Socket socket;
    private int statusCode = 200;
    private String reason = "OK";
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body = "";

    public BioHttpResponse(Socket socket) {
        this.socket = socket;
        headers.put("Content-Type", "text/html; charset=UTF-8");
    }

    @Override
    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
        this.reason = defaultReason(statusCode);
    }

    @Override
    public void setStatus(int statusCode, String reason) {
        this.statusCode = statusCode;
        this.reason = reason == null || reason.isEmpty() ? defaultReason(statusCode) : reason;
    }

    @Override
    public int getStatus() {
        return statusCode;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public void setBody(String body) {
        this.body = body == null ? "" : body;
    }

    @Override
    public byte[] getBody() {
        return body.getBytes(StandardCharsets.UTF_8);
    }

    /** Write the response back to the client socket. */
    public void write() throws IOException {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        headers.put("Content-Length", String.valueOf(bodyBytes.length));

        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append("HTTP/1.1 ").append(statusCode).append(' ').append(reason).append("\r\n");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            headerBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        headerBuilder.append("\r\n");

        OutputStream out = socket.getOutputStream();
        out.write(headerBuilder.toString().getBytes(StandardCharsets.ISO_8859_1));
        out.write(bodyBytes);
        out.flush();
    }

    private static String defaultReason(int status) {
        return switch (status) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Status";
        };
    }
}
