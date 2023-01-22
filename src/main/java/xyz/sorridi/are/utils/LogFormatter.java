package xyz.sorridi.are.utils;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter
{
    private static final String FORMAT_MESSAGE = "[%1$tF %1$tT] [%4$s] [%2$s] %5$s %n";

    public LogFormatter()
    {
        super();
    }

    @Override
    public String format(LogRecord record)
    {
        return String.format(FORMAT_MESSAGE,
                record.getMillis(),
                record.getLoggerName(),
                record.getSourceMethodName(),
                record.getLevel().getName(),
                record.getMessage());
    }

}
