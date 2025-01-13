package me.yirf.linkplugin.listeners;

import me.yirf.linkplugin.LinkPlugin;
import me.yirf.linkplugin.bot.Logger;
import me.yirf.linkplugin.linking.LinkSession;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.UUID;

public class LinkListener extends ListenerAdapter {

    private LinkSession linkSession;
    private FileConfiguration config;
    private Logger logger;
    private JDA jda;

    public LinkListener(LinkSession linkSession, FileConfiguration config, JDA jda) {
        this.linkSession = linkSession;
        this.config = config;
        this.logger = new Logger(config, jda);
        this.jda = jda;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getMessage().getContentRaw().equalsIgnoreCase("!util_link")) {
            TextChannel channel = e.getChannel().asTextChannel();
            if (channel == null) {
                Bukkit.getLogger().severe("Channel is unknown! LinkListener -> onMessageReceived");
                return;
            }
            sendEmbed(channel);
        }
    }

    private void sendEmbed(TextChannel channel) {
        String colorString = config.getString("discord.link.embed.color").replace("#", "");
        Color color = new Color(Integer.parseInt(colorString, 16));
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(config.getString("discord.link.embed.title"))
                .setDescription(config.getString("discord.link.embed.description"))
                .setColor(color);

        Button createB = Button.primary("link_account", "🔐Link");

        channel.sendMessageEmbeds(embed.build())
                .setActionRow(createB)
                .queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        if (!e.getButton().getId().equals("link_account")) return;

        TextInput code = TextInput.create("code", "Code", TextInputStyle.SHORT)
                .setPlaceholder("Ex: 1111")
                .setMinLength(4)
                .setMaxLength(4)
                .build();


        Modal modal = Modal.create("link_forum", "Link your account!")
                .addActionRow(code)
                .build();

        e.replyModal(modal).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {
        if (e.getModalId().equalsIgnoreCase("link_forum")) {

            int code = Integer.valueOf(e.getValue("code").getAsString());
            if (!linkSession.sessions.containsKey(code)) {
                e.reply("That link code is expired or incorrect!").setEphemeral(true).queue();
                return;
            }


            UUID uuid = linkSession.sessions.get(code);

            String url = "https://minotar.net/helm/" + uuid + "/600.png";
            String response = linkSession.claim(code, e.getUser().getIdLong());
            // Build a success embed
            EmbedBuilder successEmbed = new EmbedBuilder();
            String colorString = config.getString("bot.embed.color").replace("#", "");
            Color color = new Color(Integer.parseInt(colorString, 16));
            successEmbed.setTitle(config.getString("bot.embed.title"))
                    .setDescription(response)
                    .setColor(color)
                    .addField(config.getString("bot.embed.field-title"), config.getString("bot.embed.field"), false)
                    .setFooter(config.getString("bot.embed.footer"));
            successEmbed.setThumbnail(url);

            e.getUser();

            e.replyEmbeds(successEmbed.build())
                    .setEphemeral(true)
                    .queue();
            logger.create(uuid, e.getUser().getIdLong());
            modifyName(e.getMember(), Bukkit.getPlayer(uuid).getName());
        }
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
            // Build a success embed
            EmbedBuilder successEmbed = new EmbedBuilder();
            String colorString = config.getString("bot.embed.color").replace("#", "");
            Color color = new Color(Integer.parseInt(colorString, 16));
            if (response.equalsIgnoreCase("that link is invalid!")) {
                successEmbed.setTitle(config.getString("bot.embed.title"))
                        .setDescription(response)
                        .setColor(color)
                        .setFooter(config.getString("bot.embed.footer"));
            } else {
                successEmbed.setTitle(config.getString("bot.embed.title"))
                        .setDescription(response)
                        .setColor(color)
                        .addField(config.getString("bot.embed.field-title"), config.getString("bot.embed.field"), false)
                        .setFooter(config.getString("bot.embed.footer"));
                successEmbed.setThumbnail(url);
            }

            event.getUser();


            event.replyEmbeds(successEmbed.build())
                    .setEphemeral(true)
                    .queue();
            logger.create(uuid, event.getUser().getIdLong());
            modifyName(event.getMember(), Bukkit.getPlayer(uuid).getName());

        } else {
            return;
        }
    }

    private void modifyName(Member member, String name) {
        if (!config.getBoolean("discord.change-username")) return;
        Guild guild = jda.getGuildById(config.getLong("guild"));
        Member bot = guild.getSelfMember();

        Role botHighestRole = bot.getRoles().stream()
                .max((role1, role2) -> Integer.compare(role1.getPosition(), role2.getPosition()))
                .orElse(null);

        Role memberHighestRole = member.getRoles().stream()
                .max((role1, role2) -> Integer.compare(role1.getPosition(), role2.getPosition()))
                .orElse(null);

        // Check if bot's highest role is higher than the member's highest role
        if (botHighestRole != null && memberHighestRole != null &&
                botHighestRole.getPosition() > memberHighestRole.getPosition()) {
                member.modifyNickname(name).queue();
        }
    }
}