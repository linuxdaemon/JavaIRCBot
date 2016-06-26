package net.linuxdemon.demonbot.init;

import net.linuxdemon.demonbot.DemonBot;
import org.apache.logging.log4j.LogManager;

public class InitializeLogger {
    public static void init() {
        DemonBot.logger = LogManager.getLogger("net.linuxdemon.demonbot");
    }
}

