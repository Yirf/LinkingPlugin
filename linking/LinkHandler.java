package me.yirf.linkplugin.linking;

import me.yirf.linkplugin.LinkPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class LinkHandler {

    private LinkPlugin plugin;
    private FileConfiguration config;

    public LinkHandler(LinkPlugin plugin) {
        this.config = plugin.getConfig();
        this.plugin = plugin;
    }

    public void execute(String path, String username) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (String value : config.getStringList(path)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value.replace("[username]", username));
            }
        });
    }
}
