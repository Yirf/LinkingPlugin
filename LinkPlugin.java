package me.yirf.linkplugin;

import me.yirf.linkplugin.bot.Bot;
import me.yirf.linkplugin.commands.LinkCommand;
import me.yirf.linkplugin.commands.UpdateCommand;
import me.yirf.linkplugin.linking.LinkSession;
import me.yirf.linkplugin.linking.storage.PlayerStorage;
import me.yirf.linkplugin.listeners.ChangeLogListener;
import me.yirf.linkplugin.listeners.LinkListener;
import net.dv8tion.jda.api.JDA;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.internal.utils.JDALogger;

public final class LinkPlugin extends JavaPlugin {

    private JDA jda;
    private Bot bot;
    public LinkSession linkSession;
    public PlayerStorage playerStorage;
    public static String RECENT_UPDATE_TITLE;
    public static String RECENT_UPDATE_DESCRIPTION;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        changelog();
        linkSession = new LinkSession(this);
        playerStorage = new PlayerStorage(getDataFolder());

        // [BOT SETUP>>]

        bot = new Bot(getConfig().getString("token"), getConfig().getLong("guild"));
        getLogger().info("Bot is turning on.");
        jda = JDABuilder.createLight(bot.token(), EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.competing(getConfig().getString("bot.status")))
                .build();
        JDALogger.setFallbackLoggerEnabled(false);
        // have too add after creation
        jda.addEventListener(new LinkListener(linkSession, getConfig(), jda));
        jda.addEventListener(new ChangeLogListener(getConfig(), jda));

        commands();

        // [<<BOT SETUP]
    }

    @Override
    public void onDisable() {
        if (jda != null) {
            try {
                jda.shutdown();
                System.out.println("JDA has been successfully shut down.");
            } catch (Exception e) {
                System.err.println("Error occurred while shutting down JDA: " + e.getMessage());
            }
        } else {
            System.err.println("JDA instance is null. Cannot shut down.");
        }
        getConfig().set("last-update.title", RECENT_UPDATE_TITLE);
        getConfig().set("last-update.description", RECENT_UPDATE_DESCRIPTION);
        reloadConfig();
    }

    private void changelog() {
        RECENT_UPDATE_TITLE = getConfig().getString("last-update.title");
        RECENT_UPDATE_DESCRIPTION = getConfig().getString("last-update.description");
        saveConfig();
    }

    private void commands() {
        // [BOT COMMANDS>>]
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(Commands.slash("link", "Enter your discord link code here.")
                .addOptions(new OptionData(OptionType.INTEGER, "code", "Enter code"))
        );
        commands.queue();
        // [<<BOT COMMANDS]

        // [MINECRAFT COMMANDS>>]

        getCommand("link").setExecutor(new LinkCommand(this));
        getCommand("changelog").setExecutor(new UpdateCommand());

        // [<<MINECRAFT COMMANDS]
    }


}
