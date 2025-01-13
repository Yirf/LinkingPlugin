package me.yirf.linkplugin.bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.UUID;

public class Logger {

    FileConfiguration config;
    JDA jda;

    public Logger(FileConfiguration config, JDA jda) {
        this.config = config;
        this.jda = jda;
    }

    public void create(UUID uuid, long discordId) {
        long time = System.currentTimeMillis();

        Guild guild = jda.getGuildById(config.getLong("guild"));
        if (guild == null) {
            System.out.println("Guild not found!");
            return;
        }

        // Retrieve the text channel by ID
        TextChannel textChannel = guild.getTextChannelById(config.getLong("logs.channel-id"));
        if (textChannel == null) {
            System.out.println("Text channel not found!");
            return;
        }

        String url = "https://minotar.net/helm/" + uuid + "/600.png";
        EmbedBuilder embed = new EmbedBuilder();

        String colorString = config.getString("logs.embed.color").replace("#", "");
        int colorInt = Integer.parseInt(colorString, 16);
        Color color = new Color(colorInt);
        embed.setTitle(config.getString("logs.embed.title"))
                .setDescription("<@" + discordId + ">" + " has connected there accounts together!")
                .setColor(color)
                .addField(
                        config.getString("logs.embed.field-title")
                                .replace("[discordUser]", "<@" + discordId + ">")
                                .replace("[minecraftUser]", Bukkit.getPlayer(uuid).getName()),
                        config.getString("logs.embed.field")
                                .replace("[discordUser]", "<@" + discordId + ">")
                                .replace("[minecraftUser]", Bukkit.getPlayer(uuid).getName()),
                        false)
                .setFooter(config.getString("logs.embed.footer"));
        embed.setThumbnail(url);

        textChannel.sendMessageEmbeds(embed.build()).queue(
                success -> System.out.println("Message sent successfully!"),
                error -> System.err.println("Failed to send message: " + error.getMessage())
        );;
    }


}
