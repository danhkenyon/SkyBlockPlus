package uk.ac.bsfc.sbp.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Provides utility methods for performing reflection-based operations, such as discovering classes
 * in a specified package that inherit from or implement a given superclass or interface.
 */
public final class SBReflectionUtils {
    private SBReflectionUtils() {}

    public static List<Class<?>> find(String basePackage, Class<?> superClass) {
        List<Class<?>> classes = new ArrayList<>();
        String path = basePackage.replace('.', '/');

        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            boolean foundAny = false;
            while (resources.hasMoreElements()) {
                foundAny = true;
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();

                if ("file".equals(protocol)) {
                    File directory = new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
                    if (directory.exists()) {
                        SBReflectionUtils.findClasses(basePackage, directory, superClass, classes);
                    }
                } else if ("jar".equals(protocol)) {
                    String resourcePath = resource.getPath();
                    String jarPath = resourcePath.substring(5, resourcePath.indexOf("!"));
                    try (JarFile jarFile = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
                        SBReflectionUtils.scanJar(jarFile, path, superClass, classes);
                    }
                }
            }
            if (!foundAny) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();

                List<URL> urls = new ArrayList<>();
                try {
                    Object u = cl.getClass().getMethod("getURLs").invoke(cl);
                    if (u instanceof URL[] arr) {
                        Collections.addAll(urls, arr);
                    }
                } catch (Throwable ignored) { }

                try {
                    ClassLoader sys = ClassLoader.getSystemClassLoader();
                    Object u = sys.getClass().getMethod("getURLs").invoke(sys);
                    if (u instanceof URL[] arr) {
                        Collections.addAll(urls, arr);
                    }
                } catch (Throwable ignored) { }

                try {
                    URL codeSource = SBReflectionUtils.class.getProtectionDomain().getCodeSource().getLocation();
                    if (codeSource != null) urls.add(codeSource);
                } catch (Throwable ignored) {}

                for (URL url : urls) {
                    String urlStr = url.toExternalForm();
                    if (urlStr.endsWith(".jar")) {
                        try (JarFile jarFile = new JarFile(URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8))) {
                            SBReflectionUtils.scanJar(jarFile, path, superClass, classes);
                        } catch (IOException ignored) {}
                    } else {
                        try {
                            File root = new File(URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8));
                            if (root.exists()) {
                                SBReflectionUtils.scanDirectoryRoot(root, basePackage, path, superClass, classes);
                            }
                        } catch (Throwable ignored) {}
                    }
                }
            }

        } catch (IOException e) {
            SBLogger.err(e.getMessage());
        }

        return classes;
    }

    private static void scanJar(JarFile jarFile, String path, Class<?> superClass, List<Class<?>> classes) {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.startsWith(path) && name.endsWith(".class")) {
                className(superClass, classes, name);
            }
        }
    }
    private static void scanDirectoryRoot(File root, String basePackage, String path, Class<?> superClass, List<Class<?>> classes) {
        File pkgDir = new File(root, path);
        if (pkgDir.exists() && pkgDir.isDirectory()) {
            SBReflectionUtils.findClasses(basePackage, pkgDir, superClass, classes);
        } else {
            SBReflectionUtils.findClasses(root, basePackage, superClass, classes);
        }
    }

    private static void findClasses(File directory, String basePackage, Class<?> superClass, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                SBReflectionUtils.findClasses(f, basePackage, superClass, classes);
            } else if (f.getName().endsWith(".class")) {
                String abs = f.getAbsolutePath().replace(File.separatorChar, '/');
                int idx = abs.indexOf(basePackage.replace('.', '/'));
                if (idx >= 0) {
                    String rel = abs.substring(idx);
                    SBReflectionUtils.className(superClass, classes, rel);
                }
            }
        }
    }
    private static void findClasses(String packageName, File directory, Class<?> superClass, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                findClasses(packageName + "." + file.getName(), file, superClass, classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> cls = Class.forName(className);
                    if (superClass.isAssignableFrom(cls) && !cls.equals(superClass)) {
                        classes.add(cls);
                    }
                } catch (Throwable ignored) {}
            }
        }
    }

    private static void className(Class<?> superClass, List<Class<?>> classes, String rel) {
        String className = rel.replace('/', '.').substring(0, rel.length() - 6);
        try {
            Class<?> cls = Class.forName(className);
            if (superClass.isAssignableFrom(cls) && !cls.equals(superClass)) {
                classes.add(cls);
            }
        } catch (Throwable ignored) {}
    }
}