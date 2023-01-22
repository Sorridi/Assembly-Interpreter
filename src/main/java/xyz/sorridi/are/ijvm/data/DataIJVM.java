package xyz.sorridi.are.ijvm.data;

import lombok.Getter;

@Getter
public abstract class DataIJVM
{
    private final String name;
    private final int instructionNumber;

    public DataIJVM(String name, int instructionNumber)
    {
        this.name               = name;
        this.instructionNumber  = instructionNumber;
    }
}
