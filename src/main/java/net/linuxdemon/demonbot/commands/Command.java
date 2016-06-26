package net.linuxdemon.demonbot.commands;

import net.linuxdemon.demonbot.irc.ConnectionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Command {
    private String reply;
    private int UL = 0;
    private String helpMsg;
    private int numArgs = 0;

    public Command() {
        this.setReply("This command is not configured, but was called with these arguments: ${@}");
        this.setHelpMsg("No description currently registered for this command");
    }

    static String parse(String text, String user, String[] args, String channel) {
        Map<String, String> fmt = new HashMap<>();
        String argstring = "";
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            argstring += arg + " ";
        }
        argstring = argstring.trim();
        fmt.put("${@}", argstring);
        fmt.put("${*}", args[0] + " " + argstring);
        fmt.put("${+}", argstring.replace(" ", "+"));
        fmt.put("${u}", user);
        fmt.put("${c}", channel);
        for (int i = 0; i < args.length; i++) {
            fmt.put(String.format("${%d}", i), args[i]);
        }

        for (int i = 0; i < args.length; i++) {
            String argstr = "";
            for (int j = i; j < args.length; j++) {
                String arg = args[j];
                argstr += arg + " ";
            }

            fmt.put(String.format("${%d-}", i), argstr.trim());
        }

        Pattern pattern = Pattern.compile("\\$\\{([^\\}]+)\\}");
        Matcher matcher = pattern.matcher(text);

        List<String> subs = new ArrayList<>();
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                subs.add(matcher.group(i));
            }
        }
        for (String string : subs) {
            if (!fmt.containsKey(String.format("${%s}", string))) {
                return "Error in command, too few parameters";
            }
            text = text.replace(String.format("${%s}", string), fmt.get(String.format("${%s}", string)));
        }
        if (text.startsWith("/me")) {
            text = text.replace("/me", "\u0001" + "ACTION") + "\u0001";
        }
        return text;
    }

    public String run(ConnectionHandler conn, String user, String channel, String... args) {
        if (this.getNumArgs() > args.length && this.getNumArgs() != 0) {
            return String.format("Error: Too few arguments, at least %d required", this.getNumArgs());
        }
        return parse(this.getReply(), user, args, channel);
    }

    public int getUL() {
        return UL;
    }

    public Command setUL(int UL) {
        this.UL = UL;
        return this;
    }

    private String getReply() {
        return reply;
    }

    public Command setReply(String reply) {
        this.reply = reply;
        return this;
    }

    String getHelpMsg() {
        return helpMsg;
    }

    public Command setHelpMsg(String helpMsg) {
        this.helpMsg = helpMsg;
        return this;
    }

    int getNumArgs() {
        return numArgs;
    }

    public Command setNumArgs(int numArgs) {
        this.numArgs = numArgs;
        return this;
    }
}
