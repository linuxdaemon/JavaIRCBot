package net.linuxdemon.demonbot.commands;

import net.linuxdemon.demonbot.init.RegisterCommands;
import net.linuxdemon.demonbot.irc.ConnectionHandler;

public class CommandReload extends Command {
    public CommandReload() {
        super();
        this.setHelpMsg("Reloads commands from configuration file");
    }

    @Override
    public String run(ConnectionHandler conn, String user, String channel, String... args) {
        if (this.getNumArgs() > args.length && this.getNumArgs() != 0) {
            return String.format("Error: Too few arguments, at least %d required", this.getNumArgs());
        }
        Commands.immutableCommandList.clear();
        Commands.commandList.clear();
        Commands.channelCommandList.clear();
        Commands.immutableChannelCommandList.clear();
        RegisterCommands.init();
        return "Reload complete";
    }
}
