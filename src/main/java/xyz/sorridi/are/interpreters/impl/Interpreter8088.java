package xyz.sorridi.are.interpreters.impl;

import xyz.sorridi.are.interpreters.Interpreter;

import java.util.List;
import java.util.Stack;

public class Interpreter8088 extends Interpreter
{

    public Interpreter8088(String loggerName)
    {
        super("8088");
    }

    @Override
    public void execute(String instructions, Object stack) {

    }

    @Override
    public <L extends List<String>> void execute(L instructions, Object stack) {

    }

    @Override
    public void scan(String instructions)
    {

    }

    @Override
    public void scan(List<String> instructions)
    {

    }


}
