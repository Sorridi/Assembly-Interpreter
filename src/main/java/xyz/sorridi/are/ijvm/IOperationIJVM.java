package xyz.sorridi.are.ijvm;

import xyz.sorridi.are.ijvm.data.impl.ConstantIJVM;
import xyz.sorridi.are.ijvm.data.impl.LabelIJVM;
import xyz.sorridi.are.ijvm.data.impl.MethodIJVM;
import xyz.sorridi.are.ijvm.data.impl.VariableIJVM;
import xyz.sorridi.are.interpreters.Interpreter;

import java.util.HashMap;
import java.util.Stack;

public interface IOperationIJVM
{
    void bipush(String value);
    void bipush(int value);

    void dup();

    void err();

    int _goto(HashMap<String, LabelIJVM> labels, String label);

    boolean halt();

    void iadd();

    void iand();

    int ifeq(HashMap<String, LabelIJVM> labels, String label);

    int iflt(HashMap<String, LabelIJVM> labels, String label);

    int if_icmpeq(HashMap<String, LabelIJVM> labels, String label);

    void iinc(HashMap<String, VariableIJVM> variables, String varName, String value);
    void iinc(HashMap<String, VariableIJVM> variables, String varName, int value);

    void iload(HashMap<String, VariableIJVM> variables, String varName);

    void input(String character);

    void invokevirtual(Interpreter interpreter, HashMap<String, MethodIJVM> methods, String methodName);

    void ior();

    void ireturn();

    void istore(HashMap<String, VariableIJVM> variables, String varName);

    void isub();

    void ldc_w(HashMap<String, ConstantIJVM> constants, String constantName);

    void nop();

    void output();

    void swap();

    boolean wide();

}
