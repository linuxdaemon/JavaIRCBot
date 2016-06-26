package net.linuxdemon.demonbot.commands;

import net.linuxdemon.demonbot.DemonBot;
import net.linuxdemon.demonbot.irc.ConnectionHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommandJoin extends Command {
    public CommandJoin() {
        super();
        setUL(3);
        setHelpMsg("Makes the bot join a channel");
        setNumArgs(1);
    }

    @Override
    public String run(ConnectionHandler conn, String user, String channel, String... args) {
        conn.write("JOIN " + args[1] + "\r\n");
        try {
            PreparedStatement stmt = DemonBot.db.prepareStatement("INSERT INTO channels (name, network) VALUES (?, ?);");
            stmt.setString(1, args[1]);
            stmt.setString(2, conn.network);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return "An unknown error occurred, check log for details.";
        }
        return "Joined " + args[1];
    }
}
