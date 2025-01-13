package me.yirf.linkplugin.linking;

import me.yirf.linkplugin.LinkPlugin;
import me.yirf.linkplugin.utils.Translator;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class LinkSession {

    private static final Random random = new Random();
    private LinkPlugin plugin;
    private FileConfiguration config;

    public Map<Integer, UUID> sessions = new HashMap<>();
    private Component linkedMessage;

    public LinkSession(LinkPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
        linkedMessage = Translator.mini(config.getString("minecraft.messages.linked"));
    }

    public int create(UUID uuid) {
        if (plugin.playerStorage.loadPlayerData(uuid).get("discord_id") != null) return 0;
        int code;
        do {
            code = 1000 + random.nextInt(9000);
        } while (sessions.containsKey(code));

        sessions.put(code, uuid);
        return code;
    }

    public String claim(int code, long discordId) {
        if (!sessions.containsKey(code)) {
            return config.getString("discord.messages.invalid-link");
        }

        UUID uuid = sessions.get(code);

        if (config.contains(uuid.toString())) {
            return config.getString("discord.messages.already-linked");
        }

        config.set("users." + uuid, discordId);
        new LinkHandler(plugin).execute("minecraft.commands.link", Bukkit.getPlayer(uuid).getName());
        sessions.remove(code);
        Bukkit.getPlayer(uuid).sendMessage(linkedMessage);
        plugin.playerStorage.savePlayerData(uuid, discordId);
        return config.getString("discord.messages.claimed");
    }

    public void delete(UUID uuid) {
        sessions.values().remove(uuid);
    }

    public int getPendingCode(UUID uuid) {
        for (int code : sessions.keySet()) {
            if (sessions.get(code).equals(uuid)) {
                return code;
            }
        }
        return 0;
    }
}
