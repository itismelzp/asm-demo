package com.walker.analytics.plugin

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Attribute
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class SensorsAnalyticsDefaultMethodVisitor extends AdviceAdapter {

    SensorsAnalyticsDefaultMethodVisitor(MethodVisitor mv, int access, String name, String desc) {
        super(Opcodes.ASM6, mv, access, name, desc)
    }

    /**
     * 表示ASM开始扫描这个方法
     */
    @Override
    void visitCode() {
        super.visitCode()
    }

    @Override
    void visitMethodInsn(int opcode, String owner, String name, String desc) {
        super.visitMethodInsn(opcode, owner, name, desc)
    }

    @Override
    void visitAttribute(Attribute attr) {
        super.visitAttribute(attr)
    }

    /**
     * 表示方法输出完毕
     */
    @Override
    void visitEnd() {
        super.visitEnd()
    }

    @Override
    void visitFieldInsn(int opcode, String owner, String name, String desc) {
        super.visitFieldInsn(opcode, owner, name, desc)
    }

    @Override
    void visitIincInsn(int var, int increment) {
        super.visitIincInsn(var, increment)
    }

    @Override
    void visitIntInsn(int opcode, int operand) {
        super.visitIntInsn(opcode, operand)
    }

    /**
     * 该方法是visitEnd之前调用，可能反复调用。用以确定类方法在执行时堆栈的大小
     * @param maxStack
     * @param maxLocals
     */
    @Override
    void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack, maxLocals)
    }

    @Override
    void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, var)
    }

    @Override
    void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label)
    }

    @Override
    void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        super.visitLookupSwitchInsn(dflt, keys, labels)
    }

    @Override
    void visitMultiANewArrayInsn(String desc, int dims) {
        super.visitMultiANewArrayInsn(desc, dims)
    }

    @Override
    void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        super.visitTableSwitchInsn(min, max, dflt, labels)
    }

    @Override
    void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        super.visitTryCatchBlock(start, end, handler, type)
    }

    @Override
    void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, desc, signature, start, end, index)
    }

    @Override
    void visitInsn(int opcode) {
        super.visitInsn(opcode)
    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible)
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter()
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode)
    }

}