package net.linuxdemon.demonbot;

import net.linuxdemon.demonbot.init.InitializeLogger;
import net.linuxdemon.demonbot.init.RegisterCommands;
import net.linuxdemon.demonbot.init.RegisterEventHandlers;
import net.linuxdemon.demonbot.irc.ConnectionHandler;
import net.linuxdemon.demonbot.util.LogHelper;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DemonBot {
    private static final List<ConnectionHandler> conns = new ArrayList<>();
    public static Connection db = null;
    public static Logger logger;

    public static void main(String[] args) throws SQLException, IOException, ParseException, NoSuchMethodException, ClassNotFoundException {
        InitializeLogger.init();

        db = DriverManager.getConnection("jdbc:postgresql://localhost/db_name", "db_user", "db_pass");

        RegisterEventHandlers.init();
        RegisterCommands.init();

        PreparedStatement stmt = db.prepareStatement("SELECT network,ip,login,nick,password,nickservpass FROM servers;");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String netName = rs.getString("network");
            String server = rs.getString("ip");
            String login = rs.getString("login");
            String nick = rs.getString("nick");
            String serverPass = rs.getString("password");
            String nickServPass = rs.getString("nickservpass");
            conns.add(new ConnectionHandler(netName, server, serverPass, login, nick, nickServPass));
        }

        for (ConnectionHandler conn : conns) {
            stmt = db.prepareStatement("SELECT name FROM channels WHERE network = ?;");
            stmt.setString(1, conn.network);
            rs = stmt.executeQuery();
            while (rs.next()) {
                conn.joinChannel(rs.getString("name"));
            }
        }

        while (true) {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String s = scanner.next();
                LogHelper.info(s);
                if (s.equals("quit")) {
                    for (ConnectionHandler conn : conns) {
                        conn.write("JOIN 0\r\n");
                        conn.write("QUIT\r\n");
                    }
                    System.exit(0);
                }
            }
        }
    }
}
