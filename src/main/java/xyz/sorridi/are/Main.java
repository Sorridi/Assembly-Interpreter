package xyz.sorridi.are;

import xyz.sorridi.are.interpreters.impl.InterpreterIJVM;

import java.util.List;

public class Main
{

    public static void main(String[] args)
    {
        List<String> input = List.of(
                ".constant",
                    "CONST_1 0x10",
                ".end-constant",
                "",
                ".main",
                    ".var",
                        "a",
                        "b",
                        "c",
                    ".end-var",
                    "BIPUSH 0x3",
                    "testing:",
                        "BIPUSH 0x5",
                        "ISTORE a",
                        "BIPUSH 0x5",
                        "ISTORE b",
                        "GOTO label2",
                        "ILOAD a",
                        "ILOAD b",
                        "IADD",
                        "ISTORE c",
                    "label2:",
                        "ILOAD a",
                        "ILOAD b",
                        "ILOAD c",
                        "IADD",
                        "IADD",
                        "HALT",
                        "ISTORE c",
                        "BIPUSH 0x10",
                        "ILOAD c",
                        "ISUB",
                ".end-main",
                "",
                ".method test(a, b, c)",
                    "ILOAD a",
                    "ILOAD b",
                    "ILOAD c",
                ".end-method");

        InterpreterIJVM interpreterIjvm = new InterpreterIJVM();
        interpreterIjvm.scan(input);
        interpreterIjvm.execute(input);
    }

}