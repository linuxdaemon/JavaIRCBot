package net.linuxdemon.demonbot.commands;

import net.linuxdemon.demonbot.irc.ConnectionHandler;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandTime extends Command {
    public CommandTime() {
        super();
        this.setNumArgs(0);
        this.setUL(1);
    }

    @Override
    public String run(ConnectionHandler conn, String user, String channel, String... args) {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String t = new SimpleDateFormat("HH:mm:ss").format(timestamp);
        String d = new SimpleDateFormat("dd/MM/yyyy").format(timestamp);
        String s = t + " on " + d;
        return "It is currently " + s;
    }
}
