package net.linuxdemon.demonbot.event;

import net.linuxdemon.demonbot.irc.ConnectionHandler;

public class ServerEvent extends Event {
    public final String trail;

    public ServerEvent(ConnectionHandler conn, String line, String trail) {
        super(conn, line);
        this.trail = trail;
    }
}
