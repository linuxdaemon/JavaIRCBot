package net.linuxdemon.demonbot.event;

import net.linuxdemon.demonbot.irc.ConnectionHandler;

public class Event {
    public final String line;
    public final ConnectionHandler conn;

    Event(ConnectionHandler conn, String line) {
        this.conn = conn;
        this.line = line;
    }
}
