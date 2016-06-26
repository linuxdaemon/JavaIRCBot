package net.linuxdemon.demonbot.init;

import net.linuxdemon.demonbot.event.HandlerRegistry;
import net.linuxdemon.demonbot.handler.ChatEventHandler;
import net.linuxdemon.demonbot.handler.ServerEventHandler;

public class RegisterEventHandlers {
    public static void init() {
        HandlerRegistry.register(ChatEventHandler.class);
        HandlerRegistry.register(ServerEventHandler.class);
    }
}
