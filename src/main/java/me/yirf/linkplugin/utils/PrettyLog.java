package me.yirf.linkplugin.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

public class PrettyLog {

    public static void log(String msg) {
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize(msg));
    }
}
