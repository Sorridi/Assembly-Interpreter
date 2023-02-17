package xyz.sorridi.are.ijvm.impl;

import xyz.sorridi.are.ijvm.IOperationIJVM;
import xyz.sorridi.are.ijvm.data.impl.ConstantIJVM;
import xyz.sorridi.are.ijvm.data.impl.LabelIJVM;
import xyz.sorridi.are.ijvm.data.impl.MethodIJVM;
import xyz.sorridi.are.ijvm.data.impl.VariableIJVM;
import xyz.sorridi.are.interpreters.Interpreter;
import xyz.sorridi.are.utils.Decode;

import java.util.HashMap;
import java.util.Stack;

import static com.google.common.base.Preconditions.checkNotNull;

public class StackIJVM extends Stack<Integer> implements IOperationIJVM
{
    private final StackIJVM from;

    public StackIJVM()
    {
        super();

        this.from = null;
    }

    public StackIJVM(StackIJVM from)
    {
        super();

        this.from = from;
    }
    @Override
    public void bipush(String value)
    {
        bipush(Decode.num(value));
    }

    @Override
    public void bipush(int value)
    {
        push(value);
    }

    @Override
    public void dup()
    {
        push(peek());
    }

    @Override
    public void err()
    {
        throw new RuntimeException("Error!");
    }

    @Override
    public int _goto(HashMap<String, LabelIJVM> labels, String label)
    {
        LabelIJVM labelIJVM = get(labels, label);

        return labelIJVM.getInstructionNumber();
    }

    @Override
    public boolean halt()
    {
        return true;
    }

    @Override
    public void iadd()
    {
        bipush(pop() + pop());
    }

    @Override
    public void iand()
    {
        bipush(pop() & pop());
    }

    @Override
    public int ifeq(HashMap<String, LabelIJVM> labels, String label)
    {
        int value = pop();

        if (value == 0)
        {
            return _goto(labels, label);
        }

        return -1;
    }

    @Override
    public int iflt(HashMap<String, LabelIJVM> labels, String label)
    {
        int value = pop();

        if (value < 0)
        {
            return _goto(labels, label);
        }

        return -1;
    }

    @Override
    public int if_icmpeq(HashMap<String, LabelIJVM> labels, String label)
    {
        int value1 = pop();
        int value2 = pop();

        if (value1 == value2)
        {
            return _goto(labels, label);
        }

        return -1;
    }

    @Override
    public void iinc(HashMap<String, VariableIJVM> variables, String varName, String value)
    {
        iinc(variables, varName, Decode.num(value));
    }

    @Override
    public void iinc(HashMap<String, VariableIJVM> variables, String varName, int value)
    {
        VariableIJVM variable = get(variables, varName);

        variable.setValue(variable.getValue() + value);
    }

    @Override
    public void iload(HashMap<String, VariableIJVM> variables, String varName)
    {
        VariableIJVM variable = get(variables, varName);

        bipush(variable.getValue());
    }

    @Override
    public void input(String character)
    {
        if (character == null)
        {
            bipush(0);
        }
        else
        {
            bipush(character.charAt(0));
        }
    }

    @Override
    public void invokevirtual(Interpreter interpreter, HashMap<String, MethodIJVM> methods, String method)
    {
        MethodIJVM methodIJVM = get(methods, method);

        int argNum      = methodIJVM.getArgumentsNumber();
        StackIJVM stack = new StackIJVM(this);

        for (int i = argNum; i > 0; i--)
        {
            stack.bipush(pop());
        }

        interpreter.execute(methodIJVM.getInstructions(), stack);
    }

    @Override
    public void ior()
    {
        bipush(pop() | pop());
    }

    @Override
    public void ireturn()
    {
        if (from == null)
        {
            throw new RuntimeException("IRETURN outside a method!");
        }

        from.bipush(pop());
    }

    @Override
    public void istore(HashMap<String, VariableIJVM> variables, String varName)
    {
        VariableIJVM variable = get(variables, varName);

        variable.setValue(pop());
    }

    @Override
    public void isub()
    {
        int value1 = pop();
        int value2 = pop();

        bipush(value2 - value1);
    }

    @Override
    public void ldc_w(HashMap<String, ConstantIJVM> constants, String constantName)
    {
        ConstantIJVM constant = get(constants, constantName);

        bipush(constant.getValue());
    }

    @Override
    public void nop()
    {

    }

    @Override
    public void output()
    {
        System.out.println(pop());
    }

    @Override
    public void swap()
    {
        int value1 = pop();
        int value2 = pop();

        bipush(value1);
        bipush(value2);
    }

    @Override
    public boolean wide()
    {
        return true;
    }

    private <T> T get(HashMap<String, T> from, String name)
    {
        return checkNotNull(from.get(name));
    }

}
