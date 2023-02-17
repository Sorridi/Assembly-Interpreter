package xyz.sorridi.are.interpreters;

import xyz.sorridi.are.utils.LogFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

public abstract class Interpreter implements IScanner
{
    protected final Logger logger;

    public Interpreter(String loggerName)
    {
        this.logger = Logger.getLogger(loggerName);
        setupLogger();
    }

    public void setupLogger()
    {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new LogFormatter());

        logger.setUseParentHandlers(false);
        logger.addHandler(consoleHandler);
    }

    public void info(String message)
    {
        logger.info(message);
    }

    public void warning(String message)
    {
        logger.warning(message);
    }

    public void error(String message)
    {
        logger.severe(message);
    }

    public abstract void execute(String instructions, Object stack);

    public abstract <L extends List<String>> void execute(L instructions, Object stack);

}
