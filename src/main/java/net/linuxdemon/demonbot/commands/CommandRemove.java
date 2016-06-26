package net.linuxdemon.demonbot.commands;

import net.linuxdemon.demonbot.DemonBot;
import net.linuxdemon.demonbot.irc.ConnectionHandler;
import net.linuxdemon.demonbot.util.LogHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommandRemove extends Command {
    public CommandRemove() {
        super();
        this.setHelpMsg("Removes commands from the Command Registry");
        this.setNumArgs(1);
    }

    @Override
    public String run(ConnectionHandler conn, String user, String channel, String... args) {
        if (this.getNumArgs() > args.length && this.getNumArgs() != 0) {
            return String.format("Error: Too few arguments, at least %d required", this.getNumArgs());
        }
        String cmd = args[1];
        if (Commands.channelCommandList.containsKey(channel) && Commands.channelCommandList.get(channel).containsKey(cmd)) {
            Commands.channelCommandList.get(channel).remove(cmd);
            try {
                PreparedStatement stmt = DemonBot.db.prepareStatement("DELETE FROM commands WHERE command = ?");
                stmt.setString(1, cmd);
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                return "An unknown error occurred, check log for details.";
            }
            return user + " removed command " + cmd;
        }
        LogHelper.debug(Commands.channelCommandList);
        return "Command '" + cmd + "' does not exist, or cannot be removed";
    }
}
