package net.linuxdemon.demonbot.commands;

import net.linuxdemon.demonbot.DemonBot;
import net.linuxdemon.demonbot.irc.ConnectionHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommandEdit extends Command {
    public CommandEdit() {
        super();
        this.setHelpMsg("Allows you to edit existing commands");
        this.setNumArgs(2);
    }

    @Override
    public String run(ConnectionHandler conn, String user, String channel, String... args) {
        if (this.getNumArgs() > args.length && this.getNumArgs() != 0) {
            return String.format("Error: Too few arguments, at least %d required", this.getNumArgs());
        }
        String cmd = args[1];
        if (Commands.channelCommandList.containsKey(cmd)) {
            String reply = "";
            for (int i = 2; i < args.length; i++) {
                reply += args[i] + " ";
            }
            reply = reply.trim();
            Commands.registerCommand(channel, cmd, new Command().setReply(reply), true);
            try {
                PreparedStatement stmt = DemonBot.db.prepareStatement("UPDATE commands SET reply = ? WHERE command = ?");
                stmt.setString(1, reply);
                stmt.setString(2, cmd);
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                return "An unknown error occurred, check log for details.";
            }
            return user + " edited command " + cmd;
        }
        return "Command '" + cmd + "' does not exist, or cannot be edited";
    }
}
