package net.ptnkjke.jbeditor.logic.asm.bytecode;

import org.objectweb.asm.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceAnnotationVisitor;

import java.util.List;

/**
 * ASM TO BCEL - WTF?
 * Basement on TraceMethodVisitor
 */
public class BCMethodVisitor extends MethodVisitor {

    public final Printer p = new Textifier();

    public BCMethodVisitor() {
        super(Opcodes.ASM5);
    }

    @Override
    public void visitParameter(String name, int access) {
        p.visitParameter(name, access);
        super.visitParameter(name, access);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc,
                                             final boolean visible) {
        Printer p = this.p.visitMethodAnnotation(desc, visible);
        AnnotationVisitor av = mv == null ? null : mv.visitAnnotation(desc,
                visible);
        return new TraceAnnotationVisitor(av, p);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef,
                                                 TypePath typePath, String desc, boolean visible) {
        Printer p = this.p.visitMethodTypeAnnotation(typeRef, typePath, desc,
                visible);
        AnnotationVisitor av = mv == null ? null : mv.visitTypeAnnotation(
                typeRef, typePath, desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    @Override
    public void visitAttribute(final Attribute attr) {
        p.visitMethodAttribute(attr);
        super.visitAttribute(attr);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        Printer p = this.p.visitAnnotationDefault();
        AnnotationVisitor av = mv == null ? null : mv.visitAnnotationDefault();
        return new TraceAnnotationVisitor(av, p);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter,
                                                      final String desc, final boolean visible) {
        Printer p = this.p.visitParameterAnnotation(parameter, desc, visible);
        AnnotationVisitor av = mv == null ? null : mv.visitParameterAnnotation(
                parameter, desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    @Override
    public void visitCode() {
        p.visitCode();
        super.visitCode();
    }

    @Override
    public void visitFrame(final int type, final int nLocal,
                           final Object[] local, final int nStack, final Object[] stack) {
        p.visitFrame(type, nLocal, local, nStack, stack);
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void visitInsn(final int opcode) {
        p.visitInsn(opcode);
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        p.visitIntInsn(opcode, operand);
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(final int opcode, final int var) {
        p.visitVarInsn(opcode, var);
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        p.visitTypeInsn(opcode, type);
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner,
                               final String name, final String desc) {
        p.visitFieldInsn(opcode, owner, name, desc);
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Deprecated
    @Override
    public void visitMethodInsn(int opcode, String owner, String name,
                                String desc) {
        if (api >= Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        p.visitMethodInsn(opcode, owner, name, desc);
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name,
                                String desc, boolean itf) {
        if (api < Opcodes.ASM5) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        p.visitMethodInsn(opcode, owner, name, desc, itf);
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm,
                                       Object... bsmArgs) {
        p.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        p.visitJumpInsn(opcode, label);
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(final Label label) {
        p.visitLabel(label);
        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(final Object cst) {
        p.visitLdcInsn(cst);
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitIincInsn(final int var, final int increment) {
        p.visitIincInsn(var, increment);
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(final int min, final int max,
                                     final Label dflt, final Label... labels) {
        p.visitTableSwitchInsn(min, max, dflt, labels);
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
                                      final Label[] labels) {
        p.visitLookupSwitchInsn(dflt, keys, labels);
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        p.visitMultiANewArrayInsn(desc, dims);
        super.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef,
                                                 TypePath typePath, String desc, boolean visible) {
        Printer p = this.p
                .visitInsnAnnotation(typeRef, typePath, desc, visible);
        AnnotationVisitor av = mv == null ? null : mv.visitInsnAnnotation(
                typeRef, typePath, desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    @Override
    public void visitTryCatchBlock(final Label start, final Label end,
                                   final Label handler, final String type) {
        p.visitTryCatchBlock(start, end, handler, type);
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef,
                                                     TypePath typePath, String desc, boolean visible) {
        Printer p = this.p.visitTryCatchAnnotation(typeRef, typePath, desc,
                visible);
        AnnotationVisitor av = mv == null ? null : mv.visitTryCatchAnnotation(
                typeRef, typePath, desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    @Override
    public void visitLocalVariable(final String name, final String desc,
                                   final String signature, final Label start, final Label end,
                                   final int index) {
        p.visitLocalVariable(name, desc, signature, start, end, index);
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef,
                                                          TypePath typePath, Label[] start, Label[] end, int[] index,
                                                          String desc, boolean visible) {
        Printer p = this.p.visitLocalVariableAnnotation(typeRef, typePath,
                start, end, index, desc, visible);
        AnnotationVisitor av = mv == null ? null : mv
                .visitLocalVariableAnnotation(typeRef, typePath, start, end,
                        index, desc, visible);
        return new TraceAnnotationVisitor(av, p);
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
        p.visitLineNumber(line, start);
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        p.visitMaxs(maxStack, maxLocals);
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        p.visitMethodEnd();
        super.visitEnd();
    }

    public List<Object> getTextCode(){
        return  p.getText();
    }
}
