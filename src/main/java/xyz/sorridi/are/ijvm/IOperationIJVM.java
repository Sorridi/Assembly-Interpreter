package xyz.sorridi.are.ijvm;

public interface IOperationIJVM
{
    void bipush(String value);
    void bipush(int value);

    void dup();

    void err();

    void _goto(String label);

    void halt();

    void iadd();

    void iand();

    void ifeq(String label);

    void iflt(String label);

    void if_icmpeq(String label);

    void iinc(String varName, String value);
    void iinc(String varName, int value);

    void iload(String varName);

    void input(String character);

    void invokevirtual(String methodName);

    void ior();

    int ireturn();

    void istore(String varName);

    void isub();

    void ldc_w(String constantName);

    void nop();

    void output();

    int pop();

    void swap();

    void wide();

}
