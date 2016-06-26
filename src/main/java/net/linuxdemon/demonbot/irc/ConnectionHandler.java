package net.linuxdemon.demonbot.irc;

import net.linuxdemon.demonbot.DemonBot;
import net.linuxdemon.demonbot.event.ChannelChatEvent;
import net.linuxdemon.demonbot.event.EventPublisher;
import net.linuxdemon.demonbot.event.ServerEvent;
import net.linuxdemon.demonbot.util.ArrayHelper;
import net.linuxdemon.demonbot.util.LogHelper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionHandler {
    public String network;
    private BufferedWriter writer;
    private BufferedReader reader;

    public ConnectionHandler() {

    }

    public ConnectionHandler(String network, String server, String serverPass, String login, String nick, String nickServPass) throws ClassNotFoundException, NoSuchMethodException, SQLException, ParseException, IOException {
        this.network = network;
        join(server, serverPass, login, nick, nickServPass);
    }

    public boolean write(String text) {
        try {
            this.writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private ConnectionHandler join(String server, String serverPass, String login, String nick,
                                   String nickServPass) throws IOException {
        Socket socket = new Socket(server, 6667);
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        // Log on to the server.
        if (serverPass != null && !serverPass.isEmpty())
            this.writer.write("PASS " + serverPass + "\r\n");
        this.writer.write("NICK " + nick + "\r\n");
        this.writer.write("USER " + login + " 8 * : Java IRC Bot\r\n");
        this.writer.flush();

        // Read lines from the server until it tells us we have connected.
        String line;
        while ((line = this.reader.readLine()) != null) {
            LogHelper.info(line);
            if (line.contains("004")) {
                break;
            } else if (line.contains("433")) {
                System.out.println("Nickname is already in use.");
                return null;
            } else if (line.toLowerCase().startsWith("ping ")) {
                LogHelper.info("PONG " + line.split(":")[1]);
                this.writer.write("PONG " + line.substring(5) + "\r\n");
                this.writer.flush();
            }
        }
        this.writer.write("PRIVMSG NICKSERV :IDENTIFY " + nickServPass + "\r\n");
        this.writer.flush();
        startListener(nick);
        return this;
    }

    public void joinChannel(String channel) throws IOException {
        this.writer.write("JOIN " + channel + "\r\n");
        this.writer.flush();
    }

    private void startListener(final String nick) {
        final ConnectionHandler conn = this;
        new Thread() {
            @Override
            public void run() {
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        Pattern linePattern = Pattern.compile("^(:(?<prefix>\\S+) )?(?<command>\\S+)( (?!:)(?<params>.+?))?( :(?<trail>.+))?$");
                        Matcher parsedLine = linePattern.matcher(line);
                        if (!parsedLine.matches())
                            continue;
                        String prefix = parsedLine.group("prefix");
                        String command = parsedLine.group("command");
                        String[] params = {};
                        if (parsedLine.group("params") != null) {
                            params = parsedLine.group("params").split(" ");
                        }
                        String trail = StringEscapeUtils.escapeJava(parsedLine.group("trail"));

                        //LogHelper.debug(String.format( "['%s','%s','%s','%s']", prefix, command, ArrayHelper.join(params, " "), StringEscapeUtils.escapeJava( trail) ));

                        switch (command) {
                            case "372":
                            case "375":
                            case "376":
                            case "366":
                                EventPublisher.raise(new ServerEvent(conn, line, trail));
                                break;
                            case "353":
                                LogManager.getLogger("Server").info(String.format("* | Users on %s: %s", params[2], trail));
                                String channel = params[2];
                                ResultSet rs = DemonBot.db.createStatement().executeQuery("SELECT op_list FROM channels WHERE name = '" + channel + "';");
                                Array array = null;
                                while (rs.next()) {
                                    array = rs.getArray("op_list");
                                }
                                String[] opList = new String[0];
                                if (array != null) {
                                    opList = (String[]) array.getArray();
                                }
                                rs = DemonBot.db.createStatement().executeQuery("SELECT reg_list FROM channels WHERE name = '" + channel + "';");
                                Array o = null;
                                while (rs.next()) {
                                    o = rs.getArray("reg_list");
                                }
                                String[] regList = new String[0];
                                if (o != null) {
                                    regList = (String[]) o.getArray();
                                }
                                for (String item : trail.split(" ")) {
                                    String nck = item.replaceAll("[\\+@]", "");
                                    if (!nck.equals(nick)) {
                                        switch (item.charAt(0)) {
                                            case '+':
                                                if (!ArrayHelper.contains(regList, nck)) {
                                                    DemonBot.db.createStatement().executeUpdate(String.format("UPDATE channels SET reg_list = reg_list || '{%s}' WHERE name = '%s'", nck, channel));
                                                }
                                                if (ArrayHelper.contains(opList, nck)) {
                                                    DemonBot.db.createStatement().executeUpdate(String.format("UPDATE channels SET op_list = array_remove(op_list, '%s') WHERE name = '%s'", nck, channel));
                                                }
                                                break;
                                            case '@':
                                                if (!ArrayHelper.contains(opList, nck)) {
                                                    DemonBot.db.createStatement().executeUpdate(String.format("UPDATE channels SET op_list = op_list || '{%s}' WHERE name = '%s'", nck, channel));
                                                }
                                                if (ArrayHelper.contains(regList, nck)) {
                                                    DemonBot.db.createStatement().executeUpdate(String.format("UPDATE channels SET reg_list = array_remove(reg_list, '%s') WHERE name = '%s'", nck, channel));
                                                }
                                                break;
                                            default:
                                                if (ArrayHelper.contains(opList, nck)) {
                                                    DemonBot.db.createStatement().executeUpdate(String.format("UPDATE channels SET op_list = array_remove(op_list, '%s') WHERE name = '%s'", nck, channel));
                                                }
                                                if (ArrayHelper.contains(regList, nck)) {
                                                    DemonBot.db.createStatement().executeUpdate(String.format("UPDATE channels SET reg_list = array_remove(reg_list, '%s') WHERE name = '%s'", nck, channel));
                                                }
                                                break;
                                        }
                                    }
                                }
                                break;
                            case "PING":
                                write("PONG " + line.substring(5) + "\r\n");
                                break;
                            case "PRIVMSG":
                                if (trail != null && trail.startsWith("\\u0001ACTION")) {
                                    trail = trail.replace("\\u0001", "").replace("ACTION ", "");
                                    LogHelper.info(String.format("[%s] *%s %s", params[0], prefix.split("!")[0], trail));
                                } else {
                                    LogHelper.info(String.format("[%s]<%s>: %s", params[0], prefix.split("!")[0], trail));
                                }
                                EventPublisher.raise(new ChannelChatEvent(conn, line, params[0], prefix.split("!")[0], trail));
                                break;
                            case "NOTICE":
                                EventPublisher.raise(new ServerEvent(conn, line, trail));
                                break;
                            case "JOIN":
                                LogManager.getLogger("Server").info(String.format("[%s] %s (%s) joined", params[0], prefix.split("!")[0], prefix.split("!")[1]));
                                break;
                            case "PART":
                                LogManager.getLogger("Server").info(String.format("[%s] %s (%s) left (%s)", params[0], prefix.split("!")[0], prefix.split("!")[1], trail));
                                break;
                            case "QUIT":
                                LogManager.getLogger("Server").info(String.format("* %s has quit (%s)", prefix.split("!")[0], trail));
                                break;
                            case "MODE":
                                if (trail == null && params.length == 3) {
                                    if (params[1].startsWith("+")) {
                                        String pmode = "";
                                        if (params[1].substring(1).length() > 1)
                                            pmode = "s";
                                        LogManager.getLogger("Server").info(String.format("[%s] %s set mode%s '%s' on %s", params[0], prefix.split("!")[0], pmode, params[1], params[2]));
                                    } else {
                                        String pmode = "";
                                        if (params[1].substring(1).length() > 1)
                                            pmode = "s";
                                        LogManager.getLogger("Server").info(String.format("[%s] %s unset mode%s '%s' on %s", params[0], prefix.split("!")[0], pmode, params[1], params[2]));
                                    }
                                    write("NAMES " + params[0] + "\r\n");
                                } else {
                                    LogManager.getLogger("Server").info(line);
                                }
                                break;
                            default:
                                LogHelper.info(line);
                                break;
                        }
                    }
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void msg(String channel, String msg) {
        this.write("PRIVMSG " + channel + " :" + msg + "\r\n");
    }
}
