package xyz.sorridi.are.interpreters;

import java.util.List;

public interface IScanner
{

    /**
     * Scans the input and executes the instructions.
     * @param instructions The instructions to be executed.
     */
    void scan(String instructions);

    /**
     * Scans the instructions and executes them.
     * @param instructions The instructions to be executed.
     */
    void scan(List<String> instructions);

    /**
     * Transforms a string into a list of strings.
     * @param input The string to be transformed.
     * @param delimiter The delimiter to be used.
     * @return The list of strings.
     */
    default List<String> transform(String input, String delimiter)
    {
        return List.of(input.split(delimiter));
    }

    /**
     * Transforms an array of strings into a list of strings.
     * @param input The array of strings to transform.
     * @return The list of strings.
     */
    default List<String> transform(String[] input)
    {
        return List.of(input);
    }

    /**
     * Transforms a list of strings into a single string divided by newlines.
     * @param input The list of strings to transform.
     * @param delimiter The delimiter to be used.
     * @return The transformed string.
     */
    default <L extends Iterable<String>> String transform(L input, String delimiter)
    {
        return String.join(delimiter, input);
    }

}
