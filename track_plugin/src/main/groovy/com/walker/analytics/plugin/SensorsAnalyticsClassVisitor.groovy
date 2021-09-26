package com.walker.analytics.plugin

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class SensorsAnalyticsClassVisitor extends ClassVisitor implements Opcodes {

    private final static String SDK_API_CLASS = "com/walker/analytics/sdk/SensorsDataAutoTrackHelper"
    private String[] mInterfaces
    private ClassVisitor classVisitor

    SensorsAnalyticsClassVisitor(final ClassVisitor classVisitor) {
        super(Opcodes.ASM6, classVisitor)
        this.classVisitor = classVisitor
    }

    /**
     * 可以拿到类的详细信息，然后对满足条件的类进行过滤
     */
    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        mInterfaces = interfaces
    }

    /**
     * 拿到需要修改的方法，然后进行修改过滤
     */
    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions)

        String nameDesc = name + desc

        methodVisitor = new SensorsAnalyticsDefaultMethodVisitor(methodVisitor, access, name, desc) {
            @Override
            protected void onMethodExit(int opcode) {
                super.onMethodExit(opcode)

                if (mInterfaces != null && mInterfaces.length > 0) {
                    if ((mInterfaces.contains('android/view/View$OnClickListener')
                            && nameDesc == 'onClick(Landroid/view/View;)V')
//                            || desc == '(Landroid/view/View;)V'
                    ) {

                        println("visitMethod onMethodExit name: ${name}")
                        println("visitMethod onMethodExit nameDesc: ${nameDesc}")
                        println("visitMethod onMethodExit desc: ${desc}")

                        methodVisitor.visitVarInsn(ALOAD, 1)
                        methodVisitor.visitMethodInsn(INVOKESTATIC, SDK_API_CLASS,
                                "trackViewOnClick", "(Landroid/view/View;)V", false)
                    }
                }
            }

            @Override
            AnnotationVisitor visitAnnotation(String s, boolean b) {
                return super.visitAnnotation(s, b)
            }
        }
        return methodVisitor
    }

    /**
     * 访问内部类信息
     */
    @Override
    void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access)
    }

    /**
     * 遍历类中成员信息结束
     */
    @Override
    void visitEnd() {
        super.visitEnd()
    }


}