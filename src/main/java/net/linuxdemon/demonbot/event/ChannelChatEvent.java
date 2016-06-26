package net.linuxdemon.demonbot.event;

import net.linuxdemon.demonbot.irc.ConnectionHandler;

public class ChannelChatEvent extends ChannelEvent {

    public final String msg;
    public final String user;

    public ChannelChatEvent(ConnectionHandler conn, String line, String channel, String user, String msg) {
        super(conn, line, channel);
        this.msg = msg;
        this.user = user;
    }
}
