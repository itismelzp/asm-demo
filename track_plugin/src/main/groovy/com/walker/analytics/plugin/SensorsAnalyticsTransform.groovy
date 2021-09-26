package com.walker.analytics.plugin

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project


class SensorsAnalyticsTransform extends Transform {

    private static Project project

    public SensorsAnalyticsTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "sensorsAnalytics"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        _transform(transformInvocation.context, transformInvocation.inputs,
                transformInvocation.outputProvider, transformInvocation.incremental)
    }

//    @Override
//    void transform(Context context, Collection<TransformInput> inputs,
//                   Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider,
//                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
//
//    }

    /** 即使该方法什么都是不做也需所所有的输入文件拷贝到目标目录下，否则下一个Task就没有TransformInput了 **/
    void _transform(Context context, Collection<TransformInput> inputs, TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {

        printCopyRight()

        if (!incremental) {
            outputProvider.deleteAll()
        }

        /** Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历 **/
        inputs.each { TransformInput input ->
            /** 遍历目录 **/
            input.directoryInputs.each { DirectoryInput directoryInput ->
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                File dir = directoryInput.file

                if (dir) {
                    HashMap<String, File> modifyMap = new HashMap<>()

                    /** 遍历以某一扩展名结尾的文件 **/
                    dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) { File classFile ->
                        if (SensorsAnalyticsClassModifier.isShouldModify(classFile.name)) {
                            File modified = SensorsAnalyticsClassModifier
                                    .modifyClassFile(dir, classFile, context.getTemporaryDir())
//                            println("modified: $modified")
                            if (modified != null) {
                                /**
                                 * key为包名+类名，如：/com/walker/autotrack/android/app/MainActivity.class*
                                 */
                                String key = classFile.absolutePath.replace(dir.absolutePath, "")
                                modifyMap.put(key, modified)
                            }
                        }
                    }
                    FileUtils.copyDirectory(dir, dest)
                    modifyMap.entrySet().each { Map.Entry<String, File> en ->
                        File target = new File(dest.absolutePath + en.getKey())
                        if (target.exists()) {
                            target.delete()
                        }
                        FileUtils.copyFile(en.getValue(), target)
                        en.getValue().delete()
                    }
                }
            }

            /** 遍历jar **/
            input.jarInputs.each { JarInput jarInput ->
                def destName = jarInput.file.name

                /** 截取文件路径的md5值重命名输出文件，因为可能同名，会覆盖 **/
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8)
                /** 获得输出文件 **/
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }

                /** 获得输出文件 **/
                File dest = outputProvider.getContentLocation(destName + "_" + hexName,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                def modifiedJar = SensorsAnalyticsClassModifier.modifyJar(jarInput.file,
                        context.getTemporaryDir(), true)
                if (modifiedJar == null) {
                    modifiedJar = jarInput.file
                }
//                println("jarInputs modified: $modifiedJar")
//                println("jarInputs dest: $dest")
                FileUtils.copyFile(modifiedJar, dest)
            }
        }

    }

    static void printCopyRight() {
        println()
        println("##########################################")
        println("###                                    ###")
        println("### welcome to use walker's asm plugin ###")
        println("###                                    ###")
        println("##########################################")
        println()
    }
}
