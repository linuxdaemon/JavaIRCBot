package net.linuxdemon.demonbot.event;

import java.util.ArrayList;
import java.util.List;

public class HandlerRegistry {
    private static final List<Class> handlers = new ArrayList<>();

    public static void register(Class clazz) {
        handlers.add(clazz);
    }

    public static List<Class> getHandlers() {
        return handlers;
    }
}