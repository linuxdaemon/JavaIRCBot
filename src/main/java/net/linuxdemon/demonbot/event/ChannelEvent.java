package net.linuxdemon.demonbot.event;

import net.linuxdemon.demonbot.irc.ConnectionHandler;

public class ChannelEvent extends Event {
    public final String channel;

    ChannelEvent(ConnectionHandler conn, String line, String channel) {
        super(conn, line);
        this.channel = channel;
    }
}
