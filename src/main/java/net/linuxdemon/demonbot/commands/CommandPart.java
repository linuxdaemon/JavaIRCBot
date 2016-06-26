package net.linuxdemon.demonbot.commands;

import net.linuxdemon.demonbot.DemonBot;
import net.linuxdemon.demonbot.irc.ConnectionHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommandPart extends Command {
    public CommandPart() {
        super();
        setUL(3);
        setHelpMsg("Makes the bot leave a channel");
        setNumArgs(1);
    }

    @Override
    public String run(ConnectionHandler conn, String user, String channel, String... args) {
        conn.write("PART " + args[1] + "\r\n");
        try {
            PreparedStatement stmt = DemonBot.db.prepareStatement("DELETE FROM channels WHERE name = ?;");
            stmt.setString(1, args[1]);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return "An unknown error occurred, check log for details.";
        }
        return "Left " + args[1];
    }
}
