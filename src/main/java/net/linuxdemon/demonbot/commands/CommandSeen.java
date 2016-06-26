package net.linuxdemon.demonbot.commands;

import net.linuxdemon.demonbot.DemonBot;
import net.linuxdemon.demonbot.irc.ConnectionHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandSeen extends Command {
    public CommandSeen() {
        super();
        this.setNumArgs(1);
        this.setUL(1);
    }

    @Override
    public String run(ConnectionHandler conn, String user, String channel, String... args) {
        if (this.getNumArgs() > args.length && this.getNumArgs() != 0) {
            return String.format("Error: Too few arguments, at least %d required", this.getNumArgs());
        }
        String nick = args[1];
        ResultSet rs;
        try {
            PreparedStatement stmt = DemonBot.db.prepareStatement("SELECT last_seen, last_seen_channel, last_message FROM users WHERE nick = ?;");
            stmt.setString(1, nick);
            rs = stmt.executeQuery();
            int year = 0;
            int month = 0;
            int hour = 0;
            int day = 0;
            int minute = 0;
            String lastChannel = "";
            String lastMessage = "";
            while (rs.next()) {
                Pattern timeStampPattern = Pattern.compile("([^-]+)-([^-]+)-([^ ]+) ([^:]+):([^:]+):([^\\.]+).*");
                Matcher parsedLine = timeStampPattern.matcher(rs.getString("last_seen"));
                if (!parsedLine.matches()) {
                    return "An unknown error occurred, check log for details.";
                }
                year = Integer.parseInt(parsedLine.group(1));
                month = Integer.parseInt(parsedLine.group(2));
                day = Integer.parseInt(parsedLine.group(3));
                hour = Integer.parseInt(parsedLine.group(4));
                minute = Integer.parseInt(parsedLine.group(5));
                lastChannel = rs.getString("last_seen_channel");
                lastMessage = rs.getString("last_message");
            }
            if (lastChannel.isEmpty()) {
                return nick + " has not been seen.";
            }
            return String.format("%s was last seen %d/%d/%d %d:%d in channel %s saying '%s'", nick, day, month, year, hour, minute, lastChannel, lastMessage);
        } catch (SQLException e) {
            e.printStackTrace();
            return "An unknown error occurred, check log for details.";
        }
    }
}
