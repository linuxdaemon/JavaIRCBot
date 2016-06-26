package net.linuxdemon.demonbot.handler;

import net.linuxdemon.demonbot.event.ServerEvent;
import net.linuxdemon.demonbot.event.SubscribeEvent;
import org.apache.logging.log4j.LogManager;

public class ServerEventHandler {
    @SubscribeEvent
    public void onServerEvent(ServerEvent e) {
        LogManager.getLogger("Server").info("* " + e.trail);
    }
}
