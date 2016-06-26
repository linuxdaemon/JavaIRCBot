package net.linuxdemon.demonbot.commands;

import net.linuxdemon.demonbot.irc.ConnectionHandler;

import java.util.HashMap;
import java.util.Map;

public class CommandHelp extends Command {
    public CommandHelp() {
        super();
        this.setHelpMsg("Returns a list of all currently registered commands with their respective descriptions");
    }

    @Override
    public String run(ConnectionHandler conn, String user, String channel, String... args) {
        if (this.getNumArgs() > args.length && this.getNumArgs() != 0) {
            return String.format("Error: Too few arguments, at least %d required", this.getNumArgs());
        }
        Map<String, Command> cmdList = new HashMap<>();
        cmdList.putAll(Commands.immutableCommandList);
        cmdList.putAll(Commands.commandList);
        if (Commands.channelCommandList.containsKey(channel)) {
            cmdList.putAll(Commands.channelCommandList.get(channel));
        }
        if (Commands.immutableChannelCommandList.containsKey(channel)) {
            cmdList.putAll(Commands.immutableChannelCommandList.get(channel));
        }

        String[] out = new String[cmdList.size() > 0 ? cmdList.size() : 1];
        if (cmdList.isEmpty() || cmdList.size() == 0) {
            out[0] = "No commands currently registered";
        } else {
            for (int i = 0; i < cmdList.size(); i++) {
                String command = (String) cmdList.keySet().toArray()[i];
                out[i] = command + ": " + cmdList.get(command).getHelpMsg() + "\r\n";
            }
        }

        for (String anOut : out) {
            conn.write("PRIVMSG " + user + " :" + anOut + "\r\n");
        }
        return user + ", check your PMs";
    }
}
