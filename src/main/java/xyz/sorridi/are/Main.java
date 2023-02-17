package xyz.sorridi.are;

import xyz.sorridi.are.ijvm.impl.StackIJVM;
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
                    "BIPUSH 0x3",       // 3
                    "testing:",
                        "BIPUSH 0x5",   // 5
                        "ISTORE a",     // a = 5
                        "BIPUSH 0x5",   // 5
                        "ISTORE b",     // b = 5
                        "GOTO label2",
                        "ILOAD a",
                        "ILOAD b",
                        "IADD",
                        "ISTORE c",
                    "label2:",
                        "ILOAD a",      // 5
                        "ILOAD b",      // 5
                        "ILOAD c",      // 0
                        "INVOKEVIRTUAL test",   // 5, 5, 0
                        "IADD",         // 3 + 10
                        "HALT",         // stop
                        "ISTORE c",
                        "BIPUSH 0x10",
                        "ILOAD c",
                        "ISUB",
                ".end-main",
                "",
                ".method test(a, b, c)",
                    "ILOAD a",  // 5
                    "ILOAD b",  // 5
                    "ILOAD c",  // 0
                    "IADD",     // 5
                    "IADD",     // 10
                    "IRETURN",  // 10
                ".end-method");

        InterpreterIJVM interpreterIjvm = new InterpreterIJVM();
        interpreterIjvm.scan(input);
        interpreterIjvm.execute(input, new StackIJVM());
    }

}