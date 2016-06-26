package net.linuxdemon.demonbot.init;

import net.linuxdemon.demonbot.DemonBot;
import net.linuxdemon.demonbot.commands.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterCommands {
    public static void init() {
        Commands.registerGlobalCommand(".help", new CommandHelp(), false);
        Commands.registerGlobalCommand(".join", new CommandJoin(), false);
        Commands.registerGlobalCommand(".part", new CommandPart().setUL(3).setNumArgs(1), false);

        Commands.registerGlobalCommand(".addcom", new CommandAdd(), false);
        Commands.registerGlobalCommand(".editcom", new CommandEdit(), false);
        Commands.registerGlobalCommand(".delcom", new CommandRemove(), false);

        Commands.registerGlobalCommand(".reload", new CommandReload(), false);
        Commands.registerGlobalCommand(".seen", new CommandSeen(), false);
        Commands.registerGlobalCommand(".time", new CommandTime(), false);

        Commands.registerGlobalCommand(".op", new CommandRawReturn("MODE ${c} +o ${1}").setUL(3).setNumArgs(1), false);
        Commands.registerGlobalCommand(".deop", new CommandRawReturn("MODE ${c} -o ${1}").setUL(3).setNumArgs(1), false);

        Commands.registerGlobalCommand(".voice", new CommandRawReturn("MODE ${c} +v ${1}").setUL(3).setNumArgs(1), false);
        Commands.registerGlobalCommand(".devoice", new CommandRawReturn("MODE ${c} -v ${1}").setUL(3).setNumArgs(1), false);

        Commands.registerGlobalCommand(".ban", new CommandRawReturn("MODE ${c} +b ${1}").setUL(3).setNumArgs(1), false);
        Commands.registerGlobalCommand(".unban", new CommandRawReturn("MODE ${c} -b ${1}").setUL(3).setNumArgs(1), false);

        try {
            PreparedStatement stmt = DemonBot.db.prepareStatement("SELECT * FROM commands;");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String command = rs.getString("command");
                String reply = rs.getString("reply");
                String channel = rs.getString("channel");
                int ul = rs.getInt("ul");
                boolean removable = rs.getBoolean("removable");
                Commands.registerCommand(channel, command, new Command().setReply(reply).setUL(ul), removable);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
