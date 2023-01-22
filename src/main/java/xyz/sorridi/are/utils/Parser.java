package xyz.sorridi.are.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser
{

    /**
     * Parses a method with the arguments and returns the name and each single argument.
     * @param method The method to be parsed. Eg. <code>method_name(a, b, c)</code> -> <code>[method_name, a, b, c]</code>
     * @return The name of the method and each single argument.
     */
    public static String[] methodIJVM(String method)
    {
        Pattern pattern = Pattern.compile("(\\w+)\\((.*)\\)");
        Matcher matcher = pattern.matcher(method);

        if (!matcher.matches())
        {
            throw new RuntimeException("Invalid method syntax: " + method);
        }

        String name = matcher.group(1);
        String args = matcher.group(2);

        String[] argArray   = args.split(",");
        String[] result     = new String[argArray.length + 1];

        result[0] = name;

        for (int i = 0; i < argArray.length; i++)
        {
            argArray[i] = argArray[i].trim();
        }

        System.arraycopy(argArray, 0, result, 1, argArray.length);

        return result;
    }

}
