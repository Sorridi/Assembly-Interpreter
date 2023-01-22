package xyz.sorridi.are.ijvm.data.impl;

import lombok.Getter;
import xyz.sorridi.are.ijvm.data.DataIJVM;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MethodIJVM extends DataIJVM
{
    private final List<String>  instructions;
    private final List<Integer> arguments;

    private final int argumentsNumber;

    public MethodIJVM(String name, int instructionNumber, int argumentsNumber)
    {
        super(name, instructionNumber);

        this.instructions   = new ArrayList<>();
        this.arguments      = new ArrayList<>();

        this.argumentsNumber = argumentsNumber;
    }

    public void addInstruction(String instruction)
    {
        this.instructions.add(instruction);
    }
}
