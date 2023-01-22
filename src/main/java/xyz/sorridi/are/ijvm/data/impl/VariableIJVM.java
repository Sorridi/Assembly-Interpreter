package xyz.sorridi.are.ijvm.data.impl;

import lombok.Getter;
import lombok.Setter;
import xyz.sorridi.are.ijvm.data.DataIJVM;

@Getter
@Setter
public class VariableIJVM extends DataIJVM
{
    private int value;

    public VariableIJVM(String name, int instructionNumber, int value)
    {
        super(name, instructionNumber);
        this.value = value;
    }
}
