package xyz.sorridi.are.ijvm.data.impl;

import lombok.Getter;
import xyz.sorridi.are.ijvm.data.DataIJVM;

@Getter
public class ConstantIJVM extends DataIJVM
{
    private final int value;

    public ConstantIJVM(String name, int instructionNumber, int value)
    {
        super(name, instructionNumber);
        this.value = value;
    }
}
