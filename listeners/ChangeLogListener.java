package me.yirf.linkplugin.listeners;

import me.yirf.linkplugin.LinkPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.Color;

public class ChangeLogListener extends ListenerAdapter {

    FileConfiguration config;
    JDA jda;

    public ChangeLogListener(FileConfiguration config, JDA jda) {
        this.config = config;
        this.jda = jda;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getMessage().getContentRaw().equalsIgnoreCase("!util_changelog")) {
            if (!(e.getMember().getIdLong() == 992583886393593986l)) return;
            TextChannel channel = e.getChannel().asTextChannel();
            if (channel == null) return;
            sendEmbed(channel);
        }
    }

    private void sendEmbed(TextChannel channel) {
        String colorString = config.getString("utilities.changelog.embed.color").replace("#", "");
        Color color = new Color(Integer.parseInt(colorString, 16));
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(config.getString("utilities.changelog.embed.title"))
                .setDescription(config.getString("utilities.changelog.embed.description"))
                .setColor(color);

        Button createB = Button.primary("create_update", "Post");;

        channel.sendMessageEmbeds(embed.build())
                .setActionRow(createB)
                .queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {

        if (!e.getButton().getId().equals("create_update")) return;

        TextInput title = TextInput.create("title", "Update Title", TextInputStyle.SHORT)
                .setPlaceholder("changelog 1.0")
                .setMinLength(3)
                .setMaxLength(50)
                .build();

        TextInput answer = TextInput.create("description", "Update description", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Your updates go here")
                .setMinLength(10)
                .setMaxLength(4000)
                .build();


        Modal modal = Modal.create("create_forum", "Post your update!")
                .addActionRow(title)
                .addActionRow(answer)
                .build();

        e.replyModal(modal).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {
        if (e.getModalId().equalsIgnoreCase("create_forum")) {

            String title = e.getValue("title").getAsString();
            String description = e.getValue("description").getAsString();
            String colorString = config.getString("utilities.changelog.post-color").replace("#", "");
            Color color = new Color(Integer.parseInt(colorString, 16));
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(title)
                    .setDescription(description)
                    .setColor(color);

            TextChannel channel = jda.getTextChannelById(config.getLong("utilities.changelog.post-channel"));
            channel.sendMessageEmbeds(embed.build()).queue();

            LinkPlugin.RECENT_UPDATE_TITLE = title;
            LinkPlugin.RECENT_UPDATE_DESCRIPTION = description;
        }
    }
}
