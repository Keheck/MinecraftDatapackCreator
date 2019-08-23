package io.github.keheck.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Log
{
    private static final Logger NORMAL_LOGGER = Logger.getLogger("DatPackLog");
    private static final Logger ERROR_LOGGER = Logger.getLogger("DatPackErr");

    private static final File NORMAL_FILE = new File(Directories.rootDir, "runtime.txt");
    private static final File ERROR_FILE = new File(Directories.rootDir, "error.txt");

    public static void init()
    {
        createRuntimeLog();
        createErrorLog();

        NORMAL_LOGGER.setUseParentHandlers(false);
        NORMAL_LOGGER.setLevel(Level.ALL);

        ERROR_LOGGER.setUseParentHandlers(false);
        ERROR_LOGGER.setLevel(Level.SEVERE);
    }

    private static void createRuntimeLog()
    {
        try
        {
            NORMAL_FILE.createNewFile();
            FileHandler handler = new FileHandler(NORMAL_FILE.getPath());
            handler.setFormatter(new LogFormatter());
            NORMAL_LOGGER.addHandler(handler);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void createErrorLog()
    {
        try
        {
            ERROR_FILE.createNewFile();
            FileHandler handler = new FileHandler(ERROR_FILE.getPath());
            handler.setFormatter(new LogFormatter());
            ERROR_LOGGER.addHandler(handler);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void convertLogs()
    {

    }

    public static void i(String msg) { NORMAL_LOGGER.log(Level.INFO, msg); }

    public static void f1(String msg) { NORMAL_LOGGER.log(Level.FINE, msg); }

    public static void f2(String msg) { NORMAL_LOGGER.log(Level.FINER, msg); }

    public static void f3(String msg) { NORMAL_LOGGER.log(Level.FINEST, msg); }

    public static void e(String msg) { NORMAL_LOGGER.log(Level.SEVERE, msg); }

    public static void e(String msg, Throwable t) { ERROR_LOGGER.log(Level.SEVERE, msg, t); }

    public static void w(String msg) { NORMAL_LOGGER.log(Level.WARNING, msg); }

    private static class LogFormatter extends Formatter
    {
        String newLine = System.lineSeparator();

        @Override
        public String format(LogRecord record)
        {
            StringBuilder builder = new StringBuilder(256);

            if(record.getMessage().equals("\n"))
                return newLine;

            if(record.getMessage().charAt(0) == '\n')
            {
                builder.append(newLine);
                record.setMessage(record.getMessage().substring(1));
            }

            Level level = record.getLevel();

            String prefix = " [---]: ";

            if(level == Level.FINE)
                prefix = " [TRACING]: ";
            if(level == Level.FINER)
                prefix = " [DETAILED]: ";
            if(level == Level.FINEST)
                prefix = " [HIGH DETAIL]: ";
            if(level == Level.INFO)
                prefix = " [INFO]: ";
            if(level == Level.SEVERE)
                prefix = " [ERROR]: ";
            if(level == Level.WARNING)
                prefix = " [WARN]: ";

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String time = dateFormat.format(new Date());

            record.setMessage(record.getMessage().replaceAll("\n", newLine + time + prefix));

            builder.append(time).append(prefix).append(formatMessage(record)).append(newLine);

            Throwable throwable = record.getThrown();

            if(throwable != null)
            {
                builder.append("at ").
                        append(record.getSourceClassName()).
                        append(":").append(record.getSourceMethodName()).
                        append(newLine);

                StringWriter writer = new StringWriter();
                throwable.printStackTrace(new PrintWriter(writer, true));
                builder.append(writer.toString());
                builder.append(newLine);
            }

            if(level == Level.WARNING || level == Level.SEVERE)
                System.err.print(builder.toString());
            else
                System.out.print(builder.toString());

            return builder.toString();
        }
    }
}
