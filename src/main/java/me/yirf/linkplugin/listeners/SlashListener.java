package me.yirf.linkplugin.listeners;

import me.yirf.linkplugin.linking.LinkSession;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.UUID;

public class SlashListener extends ListenerAdapter {

    private LinkSession linkSession;
    private FileConfiguration config;

    public SlashListener(LinkSession linkSession, FileConfiguration config) {
        this.linkSession = linkSession;
        this.config = config;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("link")) {
            int code = event.getOption("code").getAsInt();
            if (!linkSession.sessions.containsKey(code)) {
                event.reply("That link code is expired or incorrect!").setEphemeral(true).queue();
                return;
            }

            UUID uuid = linkSession.sessions.get(code);

            String url = "https://minotar.net/helm/" + uuid + "/600.png";
            String response = linkSession.claim(code, event.getUser().getIdLong());
            Bukkit.broadcastMessage("avatar: " + url);
            Bukkit.broadcastMessage("discord avatar: " + event.getUser().getAvatarUrl());
            // Build a success embed
            EmbedBuilder successEmbed = new EmbedBuilder();
            String colorString = config.getString("bot.embed.color").replace("#", "");
            int colorInt = Integer.parseInt(colorString, 16);
            Color color = new Color(colorInt);
            successEmbed.setTitle(config.getString("bot.embed.title"))
                    .setDescription(response)
                    .setColor(color)
                    .addField(config.getString("bot.embed.field-title"), config.getString("bot.embed.field"), false)
                    .setFooter(config.getString("bot.embed.footer"));
            successEmbed.setThumbnail(url);


            event.replyEmbeds(successEmbed.build())
                    .setEphemeral(true)
                    .queue();
            //event.reply("Attemping to link to " + Bukkit.getPlayer(uuid).getName()).addEmbeds(successEmbed.build()).setEphemeral(true).queue();
        } else {
            return;
        }
    }
}