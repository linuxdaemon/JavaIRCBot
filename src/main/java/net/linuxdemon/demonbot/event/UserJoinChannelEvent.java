package net.linuxdemon.demonbot.event;

import net.linuxdemon.demonbot.irc.ConnectionHandler;

class UserJoinChannelEvent extends ChannelEvent {
    public final String user;

    public UserJoinChannelEvent(ConnectionHandler conn, String user, String channel, String fullLine) {
        super(conn, fullLine, channel);
        this.user = user;
    }
}
