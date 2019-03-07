package com.sedmelluq.discord.jdaction;

import org.gradle.api.logging.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class NoActionClassVisitor extends ClassVisitor {
  private final Logger logger;
  private final boolean ignoreFailures;
  private String className;
  private int issueCount;

  public NoActionClassVisitor(Logger logger, boolean ignoreFailures) {
    super(Opcodes.ASM6);
    this.logger = logger;
    this.ignoreFailures = ignoreFailures;
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    this.className = name;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    return new UnusedReturnMethodVisitor();
  }

  public int getIssueCount() {
    return issueCount;
  }

  private class UnusedReturnMethodVisitor extends MethodVisitor {
    private boolean checkForImmediatePop;
    private int lineNumber;

    public UnusedReturnMethodVisitor() {
      super(Opcodes.ASM6);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
      lineNumber = line;
    }

    @Override
    public void visitInsn(int opcode) {
      if (checkForImmediatePop && (opcode == Opcodes.POP || opcode == Opcodes.POP2)) {
        String message = className + ".java:" + lineNumber + ": Return value is unused. This action is not performed.";

        if (ignoreFailures) {
          logger.warn(message);
        } else {
          logger.error(message);
        }

        issueCount++;
      }

      checkForImmediatePop = false;
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
      checkForImmediatePop = false;
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
      checkForImmediatePop = false;
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
      checkForImmediatePop = false;
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
      checkForImmediatePop = false;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
      if (NoActionTargetDetector.isRestActionDescriptor(desc)) {
        checkForImmediatePop = true;
      } else {
        checkForImmediatePop = false;
      }
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
      checkForImmediatePop = false;
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
      checkForImmediatePop = false;
    }

    @Override
    public void visitLdcInsn(Object cst) {
      checkForImmediatePop = false;
    }

    @Override
    public void visitIincInsn(int var, int increment) {
      checkForImmediatePop = false;
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
      checkForImmediatePop = false;
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
      checkForImmediatePop = false;
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
      checkForImmediatePop = false;
    }
  }
}
