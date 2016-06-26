package net.linuxdemon.demonbot.commands;

import net.linuxdemon.demonbot.DemonBot;
import net.linuxdemon.demonbot.irc.ConnectionHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommandAdd extends Command {
    public CommandAdd() {
        super();
        this.setHelpMsg("Adds basic commands to the Command Registry");
        this.setNumArgs(2);
        this.setUL(3);
    }

    @Override
    public String run(ConnectionHandler conn, String user, String channel, String... args) {
        if (this.getNumArgs() > args.length && this.getNumArgs() != 0) {
            return String.format("Error: Too few arguments, at least %d required", this.getNumArgs());
        }
        String cmd = args[1];
        if (!Commands.commandList.containsKey(cmd) || !Commands.immutableCommandList.containsKey(cmd) || !Commands.channelCommandList.get(channel).containsKey(cmd) || !Commands.immutableChannelCommandList.get(channel).containsKey(cmd)) {
            String reply = "";
            for (int i = 2; i < args.length; i++) {
                reply += args[i] + " ";
            }
            reply = reply.trim();
            Commands.registerCommand(channel, cmd, new Command().setReply(reply), true);
            try {
                PreparedStatement stmt = DemonBot.db.prepareStatement("INSERT INTO commands (command, reply, channel) VALUES (?, ?, ?)");
                stmt.setString(1, cmd);
                stmt.setString(2, reply);
                stmt.setString(3, channel);
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                return "An unknown error occurred, check log for details.";
            }
            return user + " added command " + cmd;
        }
        return "Command already exists";
    }
}