package net.linuxdemon.demonbot.commands;

import net.linuxdemon.demonbot.irc.ConnectionHandler;
import net.linuxdemon.demonbot.util.LogHelper;

public class CommandRawReturn extends Command {
    private final String reply;

    public CommandRawReturn(String reply) {
        super();
        this.reply = reply;
    }

    @Override
    public String run(ConnectionHandler conn, String user, String channel, String... args) {
        LogHelper.debug(parse(reply, user, args, channel));
        conn.write(parse(reply, user, args, channel) + "\r\n");
        return "";
    }
}
