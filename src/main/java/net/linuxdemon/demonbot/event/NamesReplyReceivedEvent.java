package net.linuxdemon.demonbot.event;

import net.linuxdemon.demonbot.irc.ConnectionHandler;

class NamesReplyReceivedEvent extends Event {
    public NamesReplyReceivedEvent(ConnectionHandler conn, String line) {
        super(conn, line);
    }
}
