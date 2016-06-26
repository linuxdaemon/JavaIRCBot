package net.linuxdemon.demonbot.event;

import net.linuxdemon.demonbot.irc.ConnectionHandler;

class UserEvent extends Event {
    public UserEvent(ConnectionHandler conn, String line) {
        super(conn, line);
    }
}
