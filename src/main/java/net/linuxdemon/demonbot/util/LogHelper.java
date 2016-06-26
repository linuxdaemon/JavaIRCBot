package net.linuxdemon.demonbot.util;

import net.linuxdemon.demonbot.DemonBot;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;


public class LogHelper {
    private static final Logger logger = DemonBot.logger;

    private static void log(Object msg, Level level) {
        logger.log(level, msg);
    }

    public static void log(Object msg, String level) {
        log(msg, Level.getLevel(level));
    }

    public static void info(Object msg) {
        log(msg, Level.INFO);
    }

    public static void debug(Object msg) {
        log(msg, Level.DEBUG);
    }

    public static void fatal(Object msg) {
        log(msg, Level.FATAL);
    }

    public static void error(Object msg) {
        log(msg, Level.ERROR);
    }

    public static void warn(Object msg) {
        log(msg, Level.WARN);
    }

    public static void trace(Object msg) {
        log(msg, Level.TRACE);
    }
}
