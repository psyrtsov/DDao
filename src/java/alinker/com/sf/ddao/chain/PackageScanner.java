package com.sf.ddao.chain;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * derived from Morphia's ReflectionUtils @see http://code.google.com/p/morphia/source/browse/trunk/morphia/src/main/java/com/google/code/morphia/utils/ReflectionUtils.java
 */
public class PackageScanner {
    private static String stripFilenameExtension(final String filename) {
        if (filename.indexOf('.') != -1) {
            return filename.substring(0, filename.lastIndexOf('.'));
        } else {
            return filename;
        }
    }

    public static void getFromDirectory(final File directory, final String packageName, final Set<Class<?>> classes)
            throws ClassNotFoundException {
        if (directory.exists()) {
            for (String file : directory.list()) {
                if (file.endsWith(".class")) {
                    String name = packageName + '.' + stripFilenameExtension(file);
                    Class<?> clazz = Class.forName(name);
                    classes.add(clazz);
                }
            }
        }
    }

    public static void getFromJARFile(final String jar, final String packageName, final Set<Class<?>> classes) throws IOException,
            ClassNotFoundException {
        JarInputStream jarFile = new JarInputStream(new FileInputStream(jar));
        JarEntry jarEntry;
        do {
            jarEntry = jarFile.getNextJarEntry();
            if (jarEntry != null) {
                String className = jarEntry.getName();
                if (className.endsWith(".class")) {
                    className = stripFilenameExtension(className);
                    if (className.startsWith(packageName)) {
                        classes.add(Class.forName(className.replace('/', '.')));
                    }
                }
            }
        } while (jarEntry != null);
    }

    public static void getClasses(final ClassLoader loader, final String packageName, final Set<Class<?>> classes) throws IOException,
            ClassNotFoundException {
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = loader.getResources(path);
        if (resources != null) {
            while (resources.hasMoreElements()) {
                String filePath = resources.nextElement().getFile();
                // WINDOWS HACK
                if (filePath.indexOf("%20") > 0)
                    filePath = filePath.replaceAll("%20", " ");
                // # in the jar name
                if (filePath.indexOf("%23") > 0)
                    filePath = filePath.replaceAll("%23", "#");

                if (filePath != null) {
                    if ((filePath.indexOf("!") > 0) & (filePath.indexOf(".jar") > 0)) {
                        String jarPath = filePath.substring(0, filePath.indexOf("!")).substring(
                                filePath.indexOf(":") + 1);
                        // WINDOWS HACK
                        if (jarPath.contains(":")) {
                            jarPath = jarPath.substring(1);
                        }
                        getFromJARFile(jarPath, path, classes);
                    } else {
                        getFromDirectory(new File(filePath), packageName, classes);
                    }
                }
            }
        }
    }

    public static Set<Class<?>> getClasses(final Class<?>[] sampleClassList) throws ClassNotFoundException, IOException {
        Map<String, ClassLoader> packageClassLoaders = new HashMap<String, ClassLoader>();
        for (Class<?> aClass : sampleClassList) {
            final String name = aClass.getPackage().getName();
            packageClassLoaders.put(name, aClass.getClassLoader());
        }
        Set<Class<?>> classes = new HashSet<Class<?>>();
        for (Map.Entry<String, ClassLoader> entry : packageClassLoaders.entrySet()) {
            getClasses(entry.getValue(),entry.getKey(),classes);
        }
        return classes;
    }
}
