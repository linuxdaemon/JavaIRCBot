package net.linuxdemon.demonbot.commands;

import net.linuxdemon.demonbot.DemonBot;
import net.linuxdemon.demonbot.irc.ConnectionHandler;
import net.linuxdemon.demonbot.util.ArrayHelper;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Commands {
    public static final Map<String, Command> commandList = new HashMap<>();
    public static final Map<String, Command> immutableCommandList = new HashMap<>();

    public static final Map<String, Map<String, Command>> channelCommandList = new HashMap<>();
    public static final Map<String, Map<String, Command>> immutableChannelCommandList = new HashMap<>();

    public static void registerGlobalCommand(String command, Command commandClass, boolean removable) {
        if (removable) {
            commandList.put(command, commandClass);
        } else {
            immutableCommandList.put(command, commandClass);
        }
    }

    public static void registerCommand(String channel, String command, Command commandClass, boolean removable) {
        if (removable) {
            Map<String, Command> cmdList = new HashMap<>();
            cmdList.put(command, commandClass);
            channelCommandList.put(channel, cmdList);
        } else {
            Map<String, Command> cmdList = new HashMap<>();
            cmdList.put(command, commandClass);
            immutableChannelCommandList.put(channel, cmdList);
        }
    }

    public static boolean isCommand(String text, String channel) {
        String cmd = text.split(" ")[0];
        return commandList.containsKey(cmd) || immutableCommandList.containsKey(cmd) || channelCommandList.size() > 0 && channelCommandList.containsKey(channel) && channelCommandList.get(channel).containsKey(cmd) || immutableChannelCommandList.size() > 0 && immutableChannelCommandList.containsKey(channel) && immutableChannelCommandList.get(channel).containsKey(cmd);
    }

    private static String getCommand(String text, String channel) {
        return text.split(" ")[0];
    }

    private static int getUserLevel(ConnectionHandler conn, String user, String channel) {
        if (isOPInChannel(conn, user, channel)) {
            return 3;
        }
        return 0;
    }

    private static boolean isOPInChannel(ConnectionHandler conn, String user, String channel) {
        try {
            PreparedStatement stmt = DemonBot.db.prepareStatement("SELECT op_list FROM channels WHERE name = ?;");
            stmt.setString(1, channel);
            ResultSet rs = stmt.executeQuery();
            Array array = null;
            while (rs.next()) {
                array = rs.getArray("op_list");
            }
            String[] opList = new String[0];
            if (array != null) {
                opList = (String[]) array.getArray();
            }
            if (ArrayHelper.contains(opList, user.replaceFirst("@", ""))) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String exec(ConnectionHandler conn, String user, String channel, String text) {
        String command = getCommand(text, channel);
        String out = "";
        if (commandList.containsKey(command)) {
            if (getUserLevel(conn, user, channel) >= commandList.get(command).getUL()) {
                out = commandList.get(command).run(conn, user, channel, text.split(" "));
            }
        } else if (immutableCommandList.containsKey(command)) {
            if (getUserLevel(conn, user, channel) >= immutableCommandList.get(command).getUL()) {
                out = immutableCommandList.get(command).run(conn, user, channel, text.split(" "));
            }
        } else if (channelCommandList.get(channel).containsKey(command)) {
            if (getUserLevel(conn, user, channel) >= channelCommandList.get(channel).get(command).getUL()) {
                out = channelCommandList.get(channel).get(command).run(conn, user, channel, text.split(" "));
            }
        } else if (immutableChannelCommandList.get(channel).containsKey(command)) {
            if (getUserLevel(conn, user, channel) >= immutableChannelCommandList.get(channel).get(command).getUL()) {
                out = immutableChannelCommandList.get(channel).get(command).run(conn, user, channel, text.split(" "));
            }
        }
        //LogHelper.command( command, out );
        return String.valueOf(out);
    }
}
