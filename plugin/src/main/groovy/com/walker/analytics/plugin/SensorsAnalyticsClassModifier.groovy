package com.walker.analytics.plugin

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.regex.Matcher
import java.util.zip.ZipEntry

class SensorsAnalyticsClassModifier {
    private static HashSet<String> exclude = new HashSet<>()

    static {
        exclude = new HashSet<>()
        exclude.add('android.support')
        exclude.add('com.walker.analytics.sdk')
    }

    static File modifyJar(File jarFile, File tempDir, boolean nameHex) {
        // 读取原jar
        def file = new JarFile(jarFile, false)
        // /Users/walker/.gradle/caches/modules-2/files-2.1/androidx.arch.core/core-common/2.1.0/b3152fc64428c9354344bd89848ecddc09b6f07e/core-common-2.1.0.jar
//        println("modifyJar JarFile: ${file.name}")
        // 设置输出到的jar
        def hexName = ""
        if (nameHex) {
            hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8)
        }
        def outputJar = new File(tempDir, hexName + jarFile.name)
        // bbd120aecore-common-2.1.0.jar
//        println("modifyJar outputJar: " + outputJar.name)
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar))
        Enumeration enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement()
            InputStream inputStream
            try {
                inputStream = file.getInputStream(jarEntry)
            } catch (Exception e) {
                println(e.getMessage())
                e.printStackTrace()
                return null
            }

            String entryName = jarEntry.getName()
            if (entryName.endsWith(".DSA") || entryName.endsWith(".SF")) {
                // ignore
            } else {
                String className
                ZipEntry zipEntry = new ZipEntry(entryName)
                jarOutputStream.putNextEntry(zipEntry)

                byte[] modifiedClassBytes = null
                byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)
                if (entryName.endsWith('.class')) {
                    // androidx/constraintlayout/solver/ArrayRow.class
//                    println("modifyJar entryName: ${entryName}")
                    className = entryName.replace(Matcher.quoteReplacement(File.separator), ".").replace(".class", "")
                    // androidx.constraintlayout.solver.ArrayRow
//                    println("modifyJar className: ${className}")
                    if (isShouldModify(className)) {
                        modifiedClassBytes = modifyClass(sourceClassBytes)
                    }
                }
                if (modifiedClassBytes == null) {
                    modifiedClassBytes = sourceClassBytes
                }
                jarOutputStream.write(modifiedClassBytes)
                jarOutputStream.closeEntry()
            }
        }
        jarOutputStream.close()
        file.close()
        return outputJar
    }

    // ASM
    private static byte[] modifyClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new SensorsAnalyticsClassVisitor(classWriter)
        ClassReader classReader = new ClassReader(srcClass)
        classReader.accept(classVisitor, ClassReader.SKIP_FRAMES)
        return classWriter.toByteArray()
    }

    protected static boolean isShouldModify(String className) {
        Iterator<String> iterator = exclude.iterator()
        while (iterator.hasNext()) {
            String packageName = iterator.next()
            if (className.startsWith(packageName)) {
                return false
            }
        }

        if (className.contains('R$')
                || className.contains('R2$')
                || className.contains('R.class')
                || className.contains('R2.class')
                || className.contains('BuildConfig.class')) {
            return false
        }
        return true
    }

    static File modifyClassFile(File dir, File classFile, File tempDir) {
        File modified = null
        try {
            String tmpDir = tempDir.absolutePath
            // /Users/walker/android_demo/gradle_demo/AutoTrackTransformProject/app/build/tmp/transformClassesWithSensorsAnalyticsForDebug
            println("modifyClassFile tmpDir: " + tmpDir)
            String absolutePath = classFile.absolutePath
            // /Users/walker/android_demo/gradle_demo/AutoTrackTransformProject/app/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes/com/example/autotracktransformproject/MainActivity.class
            println("modifyClassFile absolutePath: " + absolutePath)
            String dirPath = dir.absolutePath + File.separator
            // /Users/walker/android_demo/gradle_demo/AutoTrackTransformProject/app/build/intermediates/javac/debug/compileDebugJavaWithJavac/classes/
            println("modifyClassFile dirPath: " + dirPath)
            String pathName = absolutePath.replace(dirPath, "")
            // com/example/autotracktransformproject/MainActivity.class
            println("modifyClassFile pathName: " + pathName)
            String className = path2ClassName(pathName)
            // com.example.autotracktransformproject.MainActivity
            println("modifyClassFile className: " + className)
            byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(classFile))
            byte[] modifiedClassBytes = modifyClass(sourceClassBytes)
            if (modifiedClassBytes) {
                modified = new File(tempDir, className.replace('.', '') + '.class')
                println("modifyClassFile modified className: " + className)
                if (modified.exists()) {
                    modified.delete()
                }
                modified.createNewFile()
                new FileOutputStream(modified).write(modifiedClassBytes)
            }
        } catch (Exception e) {
            e.printStackTrace()
            modified = classFile
        }
        return modified
    }

    // com/walker/demo/HelloWorld.class -> com.walker.demo.HelloWorld
    static String path2ClassName(String pathName) {
        pathName.replace(File.separator, ".").replace(".class", "")
    }
}