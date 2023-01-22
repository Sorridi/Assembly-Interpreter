package xyz.sorridi.are.utils;

public class Decode
{

    /**
     * Decodes the value into a decimal number.
     * @param value The instruction to decode.
     * @return The decoded instruction.
     */
    public static Integer num(String value)
    {
        if (value.startsWith("0x"))
        {
            return Integer.parseInt(value.substring(2), 16);
        }

        if (value.startsWith("0") && value.length() > 1)
        {
            return Integer.parseInt(value.substring(1), 8);
        }

        return Integer.parseInt(value);
    }

}
