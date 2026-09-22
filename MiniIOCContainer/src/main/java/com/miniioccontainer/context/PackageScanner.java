package com.miniioccontainer.context;

import com.miniioccontainer.annotation.MyComponent;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageScanner {

    /**
     * 扫描指定包下所有带 @MyComponent 的类（支持 filesystem 目录与依赖 JAR）。
     */
    public static List<Class<?>> scan(String basePackage) {
        List<Class<?>> result = new ArrayList<>();
        String path = basePackage.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            if (!resources.hasMoreElements()) {
                throw new RuntimeException("包不存在: " + basePackage);
            }
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    scanDirectory(toFile(url), basePackage, classLoader, result);
                } else if ("jar".equals(protocol)) {
                    scanJar(url, path, classLoader, result);
                } else {
                    System.err.println("跳过不支持的资源协议: " + protocol + " -> " + url);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("扫描包失败: " + basePackage, e);
        }

        return result;
    }

    private static File toFile(URL url) {
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            return new File(url.getFile());
        }
    }

    private static void scanJar(URL url, String pathPrefix, ClassLoader classLoader, List<Class<?>> result)
            throws IOException {
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        String prefix = pathPrefix.endsWith("/") ? pathPrefix : pathPrefix + "/";
        // Also accept exact package path entries under pathPrefix without trailing slash match for nested
        try (JarFile jarFile = connection.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName();
                if (!name.startsWith(prefix) || !name.endsWith(".class")) {
                    continue;
                }
                String fullClassName = name.substring(0, name.length() - 6).replace('/', '.');
                tryLoadComponent(fullClassName, classLoader, result);
            }
        }
    }

    /**
     * 递归遍历目录，收集 .class 文件对应的 Class 对象
     */
    private static void scanDirectory(File dir,
                                      String packageName,
                                      ClassLoader classLoader,
                                      List<Class<?>> result) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classLoader, result);
            } else {
                String fileName = file.getName();
                if (!fileName.endsWith(".class")) {
                    continue;
                }
                String className = fileName.substring(0, fileName.length() - 6);
                String fullClassName = packageName + "." + className;
                tryLoadComponent(fullClassName, classLoader, result);
            }
        }
    }

    private static void tryLoadComponent(String fullClassName, ClassLoader classLoader, List<Class<?>> result) {
        try {
            Class<?> clazz = Class.forName(fullClassName, false, classLoader);
            if (clazz.isAnnotationPresent(MyComponent.class)) {
                result.add(clazz);
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            System.err.println("无法加载类: " + fullClassName);
        }
    }
}
