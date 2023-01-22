package xyz.sorridi.are.interpreters.impl;

import lombok.Getter;
import xyz.sorridi.are.ijvm.IOperationIJVM;
import xyz.sorridi.are.ijvm.PointerIJVM;
import xyz.sorridi.are.ijvm.data.impl.ConstantIJVM;
import xyz.sorridi.are.ijvm.data.impl.LabelIJVM;
import xyz.sorridi.are.ijvm.data.impl.MethodIJVM;
import xyz.sorridi.are.ijvm.data.impl.VariableIJVM;
import xyz.sorridi.are.interpreters.Interpreter;
import xyz.sorridi.are.utils.Decode;
import xyz.sorridi.are.utils.Parser;

import java.util.*;

import static xyz.sorridi.are.ijvm.PointerIJVM.*;

@Getter
public class InterpreterIJVM extends Interpreter implements IOperationIJVM
{
    private final Stack<Integer> stackPointer;
    private int programCounter;
    private boolean wide, halt;

    private MethodIJVM main;
    private final HashMap<String, MethodIJVM>   methods;
    private final HashMap<String, ConstantIJVM> constants;
    private final HashMap<String, VariableIJVM> variables;
    private final HashMap<String, LabelIJVM>    labels;

    public InterpreterIJVM()
    {
        super("IJVM");

        stackPointer    = new Stack<>();
        methods         = new HashMap<>();
        constants       = new HashMap<>();
        variables       = new HashMap<>();
        labels          = new HashMap<>();
    }

    @Override
    public void scan(String input)
    {
        scan(transform(input, "\n"));
    }

    @Override
    public void scan(List<String> instructions)
    {
        PointerIJVM pointerIJVM = NONE, prev = NONE;

        String currentMethod = null;
        int size = instructions.size();

        for (int i = 0; i < size; i++)
        {
            String instruction = instructions.get(i);

            if (instruction.isEmpty())
            {
                continue;
            }

            info("Instruction: " + instruction);

            if (instruction.startsWith(".method"))
            {
                pointerIJVM = METHODS;

                String actualMethod = instruction.substring(8);

                String[] parsed = Parser.methodIJVM(actualMethod);
                int arguments   = parsed.length - 1;

                MethodIJVM method = new MethodIJVM(parsed[0], i, arguments);
                info("Found method: " + method.getName() + " with " + method.getArguments() + " arguments.");

                methods.put(method.getName(), method);
                currentMethod = method.getName();

                continue;
            }

            if (instruction.startsWith(".end-method"))
            {
                pointerIJVM = NONE;
                info("Found end of method!");
                continue;
            }

            if (instruction.startsWith(".var"))
            {
                pointerIJVM = VARIABLES;
                info("Found variables section...");
                continue;
            }

            if (instruction.startsWith(".end-var"))
            {
                pointerIJVM = NONE;
                info("Found a total of " + variables.size() + " variables!");
                continue;
            }

            if (instruction.startsWith(".constant"))
            {
                pointerIJVM = CONSTANTS;
                info("Found constant section...");
                continue;
            }

            if (instruction.startsWith(".end-constant"))
            {
                pointerIJVM = NONE;
                info("Found a total of " + constants.size() + " constants!");
                continue;
            }

            if (instruction.endsWith(":"))
            {
                String label = instruction.substring(0, instruction.length() - 1);

                labels.put(label, new LabelIJVM(label, i));
                info("Found label: " + label);
                continue;
            }

            if (instruction.startsWith(".main"))
            {
                pointerIJVM = MAIN;
                prev        = MAIN;

                main = new MethodIJVM("main", i, 0);
                info("Found main method!");
                continue;
            }

            if (instruction.startsWith(".end-main"))
            {
                pointerIJVM = NONE;
                prev        = NONE;

                info("Found end of main method!");
                continue;
            }

            if (prev == MAIN)
            {
                if (pointerIJVM != NONE)
                {
                    if (pointerIJVM == VARIABLES)
                    {
                        variables.put(instruction, new VariableIJVM(instruction, i, 0));
                        info("Saved: '" + instruction + "' with value '0'.");
                    }

                    continue;
                }

                main.addInstruction(instruction);
                info("Saved: '" + instruction + "' to main method.");
                continue;
            }

            switch (pointerIJVM)
            {
                case CONSTANTS:
                {
                    String[] split = instruction.split(" ");

                    String name = split[0];
                    int value   = Decode.num(split[1]);

                    constants.put(name, new ConstantIJVM(name, i, value));
                    info("Saved: '" + name + "' with value '" + value + "'.");
                    break;
                }
                case METHODS:
                {
                    methods.get(currentMethod).addInstruction(instruction);
                    info("Saved: '" + instruction + "' to method '" + currentMethod + "'.");
                    break;
                }
            }
        }
    }

    @Override
    public void execute(String input)
    {
        execute(transform(input, "\n"));
    }

    @Override
    public <L extends List<String>> void execute(L instructions)
    {
        boolean started = false;
        int size = instructions.size();

        System.out.println("size is " + size);

        for (programCounter = 0; programCounter < size; programCounter++)
        {
            if (halt)
            {
                break;
            }

            String instruction = instructions.get(programCounter);

            if (instruction.startsWith(".main"))
            {
                started = true;
                continue;
            }

            if (instruction.isEmpty())
            {
                continue;
            }

            warning(" ");
            warning("Instruction: " + instruction);
            warning("Stack: " + stackPointer);
            warning("Program counter: " + programCounter);

            String[] split = instruction.split(" ");
            String opcode  = split[0];
            String arg     = split.length > 1 ? split[1] : null;

            switch (opcode)
            {
                case "BIPUSH":
                {
                    bipush(arg);
                    break;
                }
                case "DUP":
                {
                    dup();
                    break;
                }
                case "ERR":
                {
                    err();
                    break;
                }
                case "GOTO":
                {
                    error("goto from line " + programCounter + " to " + arg);
                    _goto(arg);
                    error("goto is now at line " + programCounter + " to " + arg);
                    break;
                }
                case "HALT":
                {
                    halt();
                    break;
                }
                case "IADD":
                {
                    iadd();
                    break;
                }
                case "IAND":
                {
                    iand();
                    break;
                }
                case "IFEQ":
                {
                    ifeq(arg);
                    break;
                }
                case "IFLT":
                {
                    iflt(arg);
                    break;
                }
                case "IF_ICMPEQ":
                {
                    if_icmpeq(arg);
                    break;
                }
                case "IINC":
                {
                    iinc(arg, split[2]);
                    break;
                }
                case "ILOAD":
                {
                    iload(arg);
                    break;
                }
                case "INPUT":
                {
                    input("Test");
                    break;
                }
                case "INVOKEVIRTUAL":
                {
                    invokevirtual(arg);
                    break;
                }
                case "IOR":
                {
                    ior();
                    break;
                }
                case "IRETURN":
                {
                    ireturn();
                    break;
                }
                case "ISTORE":
                {
                    istore(arg);
                    break;
                }
                case "ISUB":
                {
                    isub();
                    break;
                }
                case "LDC_W":
                {
                    ldc_w(arg);
                    break;
                }
                case "NOP":
                {
                    nop();
                    break;
                }
                case "OUT":
                {
                    output();
                    break;
                }
                case "POP":
                {
                    pop();
                    break;
                }
                case "SWAP":
                {
                    swap();
                    break;
                }
                case "WIDE":
                {
                    wide();
                    break;
                }
            }
        }

        warning("Stack finale: " + stackPointer);
    }

    @Override
    public void bipush(String value)
    {
        bipush(Decode.num(value));
    }

    @Override
    public void bipush(int value)
    {
        stackPointer.push(value);
    }

    @Override
    public void dup()
    {
        stackPointer.push(stackPointer.peek());
    }

    @Override
    public void err()
    {
        throw new RuntimeException("Error!");
    }

    @Override
    public void _goto(String label)
    {
        LabelIJVM labelIJVM = labels.get(label);

        if (labelIJVM == null)
        {
            throw new RuntimeException("Label '" + label + "' not found!");
        }

        programCounter = labelIJVM.getInstructionNumber();
    }

    @Override
    public void halt()
    {
        halt = true;
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
    public void ifeq(String label)
    {
        int value = pop();

        if (value == 0)
        {
            _goto(label);
        }
    }

    @Override
    public void iflt(String label)
    {
        int value = pop();

        if (value < 0)
        {
            _goto(label);
        }
    }

    @Override
    public void if_icmpeq(String label)
    {
        int value1 = pop();
        int value2 = pop();

        if (value1 == value2)
        {
            _goto(label);
        }
    }

    @Override
    public void iinc(String varName, String value)
    {
        iinc(varName, Decode.num(value));
    }

    @Override
    public void iinc(String varName, int value)
    {
        VariableIJVM variable = variables.get(varName);

        if (variable == null)
        {
            throw new RuntimeException("Variable '" + varName + "' not found!");
        }

        variable.setValue(variable.getValue() + value);
    }

    @Override
    public void iload(String varName)
    {
        VariableIJVM variable = variables.get(varName);

        if (variable == null)
        {
            throw new RuntimeException("Variable '" + varName + "' not found!");
        }

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
    public void invokevirtual(String method)
    {
        MethodIJVM methodIJVM = methods.get(method);

        if (methodIJVM == null)
        {
            throw new RuntimeException("Method '" + method + "' not found!");
        }

        int argNum      = methodIJVM.getArgumentsNumber();
        int[] arguments = new int[argNum];

        for (int i = 0; i < argNum; i++)
        {
            arguments[i] = pop();
        }

        execute(methodIJVM.getInstructions());

        for (int i = 0; i < argNum; i++)
        {
            bipush(arguments[i]);
        }
    }

    @Override
    public void ior()
    {
        bipush(pop() | pop());
    }

    @Override
    public int ireturn()
    {
        return pop();
    }

    @Override
    public void istore(String varName)
    {
        VariableIJVM variable = variables.get(varName);

        if (variable == null)
        {
            throw new RuntimeException("Variable '" + varName + "' not found!");
        }

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
    public void ldc_w(String constantName)
    {
        ConstantIJVM constant = constants.get(constantName);

        if (constant == null)
        {
            throw new RuntimeException("Constant '" + constantName + "' not found!");
        }

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
    public int pop()
    {
        return stackPointer.pop();
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
    public void wide()
    {
        wide = true;
    }

}
