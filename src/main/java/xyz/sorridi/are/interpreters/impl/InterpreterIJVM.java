package xyz.sorridi.are.interpreters.impl;

import lombok.Getter;
import lombok.SneakyThrows;
import xyz.sorridi.are.ijvm.PointerIJVM;
import xyz.sorridi.are.ijvm.impl.StackIJVM;
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
public class InterpreterIJVM extends Interpreter
{
    private boolean wide, halt;

    private MethodIJVM main;
    private final HashMap<String, MethodIJVM>   methods;
    private final HashMap<String, ConstantIJVM> constants;
    private final HashMap<String, VariableIJVM> variables;
    private final HashMap<String, LabelIJVM>    labels;

    public InterpreterIJVM()
    {
        super("IJVM");

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
    public void execute(String input, Object stack)
    {
        execute(transform(input, "\n"), stack);
    }

    @SneakyThrows
    @Override
    public <L extends List<String>> void execute(L instructions, Object stack)
    {
        StackIJVM stackIJVM = (StackIJVM) stack;
        boolean started     = false;
        int size            = instructions.size();

        System.out.println("size of execute is " + size);

        for (int programCounter = 0; programCounter < size; programCounter++)
        {
            if (halt)
            {
                break;
            }

            String instruction = instructions.get(programCounter);

            if (!instruction.isEmpty())
            {
                Thread.sleep(1000);
            }

            if (instruction.startsWith(".main"))
            {
                started = true;
                continue;
            }

            if (instruction.isEmpty() || instruction.startsWith("//"))
            {
                continue;
            }

            warning(" ");
            warning("(" + programCounter + ") Instruction: " + instruction);
            warning("Stack PRE: " + stackIJVM);

            String[] split = instruction.split(" ");
            String opcode  = split[0];
            String arg     = split.length > 1 ? split[1] : null;

            switch (opcode)
            {
                case "BIPUSH":
                {
                    stackIJVM.bipush(arg);
                    break;
                }
                case "DUP":
                {
                    stackIJVM.dup();
                    break;
                }
                case "ERR":
                {
                    stackIJVM.err();
                    break;
                }
                case "GOTO":
                {
                    programCounter = stackIJVM._goto(labels, arg);
                    break;
                }
                case "HALT":
                {
                    halt = stackIJVM.halt();
                    break;
                }
                case "IADD":
                {
                    stackIJVM.iadd();
                    break;
                }
                case "IAND":
                {
                    stackIJVM.iand();
                    break;
                }
                case "IFEQ":
                {
                    int gotoResult = stackIJVM.ifeq(labels, arg);

                    if (gotoResult != -1)
                    {
                        programCounter = gotoResult;
                    }
                    break;
                }
                case "IFLT":
                {
                    int gotoResult = stackIJVM.iflt(labels, arg);

                    if (gotoResult != -1)
                    {
                        programCounter = gotoResult;
                    }
                    break;
                }
                case "IF_ICMPEQ":
                {
                    int gotoResult = stackIJVM.if_icmpeq(labels, arg);

                    if (gotoResult != -1)
                    {
                        programCounter = gotoResult;
                    }
                    break;
                }
                case "IINC":
                {
                    stackIJVM.iinc(variables, arg, split[2]);
                    break;
                }
                case "ILOAD":
                {
                    stackIJVM.iload(variables, arg);
                    break;
                }
                case "INPUT":
                {
                    stackIJVM.input("Test");
                    break;
                }
                case "INVOKEVIRTUAL":
                {
                    stackIJVM.invokevirtual(this, methods, arg);
                    break;
                }
                case "IOR":
                {
                    stackIJVM.ior();
                    break;
                }
                case "IRETURN":
                {
                    stackIJVM.ireturn();
                    break;
                }
                case "ISTORE":
                {
                    stackIJVM.istore(variables, arg);
                    break;
                }
                case "ISUB":
                {
                    stackIJVM.isub();
                    break;
                }
                case "LDC_W":
                {
                    stackIJVM.ldc_w(constants, arg);
                    break;
                }
                case "NOP":
                {
                    stackIJVM.nop();
                    break;
                }
                case "OUT":
                {
                    stackIJVM.output();
                    break;
                }
                case "POP":
                {
                    stackIJVM.pop();
                    break;
                }
                case "SWAP":
                {
                    stackIJVM.swap();
                    break;
                }
                case "WIDE":
                {
                    wide = stackIJVM.wide();
                    break;
                }
            }
            warning("Stack POST: " + stackIJVM);
        }

        warning("QUIT Stack: " + stackIJVM);
    }

}
