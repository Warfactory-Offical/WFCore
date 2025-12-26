package wfcore.api.util;

import wfcore.WFCore;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class LogUtil {
    public static void logExceptionWithTrace(String text, Exception exception) {
        // log the passed exception text
        WFCore.LOGGER.atError().log(text + " --- STACKTRACE BELOW ---");

        // write the stack trace to a string and then log it
        Writer strBuf = new StringWriter();
        PrintWriter traceWriter = new PrintWriter(strBuf);
        exception.printStackTrace(traceWriter);
        WFCore.LOGGER.atError().log(strBuf.toString());
    }
}
