package me.yirf.linkplugin.commands;

import me.yirf.linkplugin.LinkPlugin;
import me.yirf.linkplugin.linking.LinkSession;
import me.yirf.linkplugin.utils.Translator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class LinkCommand implements CommandExecutor {

    private FileConfiguration config;
    private LinkSession session;
    private MiniMessage miniMessage;

    private List<Component> inviteMessage;

    public LinkCommand(LinkPlugin plugin) {
        config = plugin.getConfig();
        session = plugin.linkSession;
        this.miniMessage = MiniMessage.miniMessage();

        inviteMessage = Translator.mini(config.getStringList("minecraft.messages.invite"));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(text("This command is made for players only!", NamedTextColor.RED));
            return true;
        }

        Player player = (Player) commandSender;

        if (config.contains(player.getUniqueId().toString())) {
            player.sendMessage(miniMessage.deserialize(config.getString("minecraft.messages.already-linked")));
            return true;
        }

        if (session.sessions.containsValue(player.getUniqueId())) {
            Component comp = miniMessage.deserialize(config.getString("minecraft.messages.pending-code"));
            comp = Translator.replacePlaceholder(comp, "[code]", session.getPendingCode(player.getUniqueId()) + "");
            player.sendMessage(comp);
            return true;
        }

        int code = session.create(player.getUniqueId());
        if (code == 0) {
            player.sendMessage(miniMessage.deserialize(config.getString("minecraft.messages.already-linked")));
            return true;
        }

        for (Component comp : inviteMessage) {
            comp = Translator.replacePlaceholder(comp, "[code]", code + "");
            player.sendMessage(comp);
        }
        return true;
    }
}
