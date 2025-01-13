package me.yirf.linkplugin.linking.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerStorage {

    private final File storageDirectory;

    public PlayerStorage(File pluginDataFolder) {
        this.storageDirectory = new File(pluginDataFolder, "data");
        if (!storageDirectory.exists()) {
            storageDirectory.mkdirs();
        }
    }

    /**
     * Get the YAML file for a player based on their UUID.
     *
     * @param uuid The player's UUID.
     * @return The File representing the player's YAML file.
     */
    private File getPlayerFile(UUID uuid) {
        return new File(storageDirectory, uuid + ".yml");
    }

    /**
     * Load a player's data from their YAML file.
     *
     * @param uuid The player's UUID.
     * @return The FileConfiguration for the player's data.
     */
    public FileConfiguration loadPlayerData(UUID uuid) {
        File playerFile = getPlayerFile(uuid);
        if (!playerFile.exists()) {
            // If the file doesn't exist, create a blank configuration
            return YamlConfiguration.loadConfiguration(playerFile);
        }
        return YamlConfiguration.loadConfiguration(playerFile);
    }

    /**
     * Save a player's data to their YAML file.
     *
     * @param uuid The player's UUID.
     * @param discordId The player's Discord ID.
     */
    public void savePlayerData(UUID uuid, long discordId) {
        File playerFile = getPlayerFile(uuid);
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        config.set("uuid", uuid.toString());
        config.set("discord_id", discordId);

        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}