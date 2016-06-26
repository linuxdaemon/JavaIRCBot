package net.linuxdemon.demonbot.handler;

import net.linuxdemon.demonbot.DemonBot;
import net.linuxdemon.demonbot.commands.Commands;
import net.linuxdemon.demonbot.event.ChannelChatEvent;
import net.linuxdemon.demonbot.event.SubscribeEvent;
import net.linuxdemon.demonbot.irc.ConnectionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class ChatEventHandler {
    @SubscribeEvent
    public void onSayMessage(ChannelChatEvent e) throws SQLException {
        String channel = e.channel;
        String user = e.user;
        String message = e.msg;
        Connection db = DemonBot.db;
        ConnectionHandler conn = e.conn;
        Pattern commandRegex = Pattern.compile("^(\\.(?<command>\\S+))?( (?<params>.*)?)?$");

        if (channel.startsWith("#")) {
            PreparedStatement stmt = DemonBot.db.prepareStatement("SELECT nick FROM users WHERE nick = ?;");
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            boolean exists = false;
            while (rs.next()) {
                if (rs.getString("nick").equals(user)) {
                    exists = true;
                }
            }
            if (!exists) {
                stmt = db.prepareStatement("INSERT INTO users (nick) VALUES (?)");
                stmt.setString(1, user);
                stmt.execute();
            }

            stmt = db.prepareStatement("UPDATE users SET last_seen = current_timestamp WHERE nick = ?;");
            stmt.setString(1, user);
            stmt.execute();

            stmt = db.prepareStatement("UPDATE users SET last_message = ? WHERE nick = ?;");
            stmt.setString(1, message);
            stmt.setString(2, user);
            stmt.execute();

            stmt = db.prepareStatement("UPDATE users SET last_seen_channel = ? WHERE nick = ?;");
            stmt.setString(1, channel);
            stmt.setString(2, user);
            stmt.execute();
        }

        if (commandRegex.matcher(message).matches()) {
            if (Commands.isCommand(message, channel)) {
                String out = Commands.exec(conn, user, channel, message);
                if (out != null && !out.isEmpty()) {
                    if (channel.startsWith("#")) {
                        conn.msg(channel, out);
                    } else {
                        conn.msg(user, out);
                    }
                }
            } else {
                if (channel.startsWith("#")) {
                    conn.msg(channel, String.format("Command '%s' is not registered in this channel", message.split(" ")[0]));
                } else {
                    conn.msg(user, String.format("Command '%s' is not registered in this channel", message.split(" ")[0]));
                }
            }
        }
    }
}
