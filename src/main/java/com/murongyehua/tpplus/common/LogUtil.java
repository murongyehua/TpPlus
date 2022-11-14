package com.murongyehua.tpplus.common;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class LogUtil {

    public static void log(String content) {
        CommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage(content);
    }

}
