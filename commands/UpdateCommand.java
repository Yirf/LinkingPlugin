package me.yirf.linkplugin.commands;

import me.yirf.linkplugin.LinkPlugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UpdateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

            BookMeta bookMeta = (BookMeta) book.getItemMeta();

            if (bookMeta != null) {
                bookMeta.setTitle("Changelog");
                bookMeta.setAuthor("Server Admin");

                bookMeta.setPages(LinkPlugin.RECENT_UPDATE_TITLE + "\n\n" +
                        LinkPlugin.RECENT_UPDATE_DESCRIPTION);

                book.setItemMeta(bookMeta);
            }

            player.openBook(book);
        }

        return true;
    }
}
