package io.github.skuffy808.sunemoji;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class Sunemoji extends JavaPlugin implements Listener {

    private Map<String, String> emojiMap = new LinkedHashMap<>();

    private String headerMessage;
    private String footerMessage;

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Creates config.yml if it does not already exist
        loadEmojisFromConfig(); // Load emoji data

        headerMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("header-message", ""));
        footerMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("footer-message", ""));

        getServer().getPluginManager().registerEvents(this, this); // Register chat listener
        getLogger().info("Sunemoji plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Sunemoji plugin disabled.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        for (Map.Entry<String, String> entry : emojiMap.entrySet()) {
            msg = msg.replace(entry.getKey(), entry.getValue());
        }
        if (event.getPlayer().hasPermission("sunemoji.use")) {
            event.setMessage(msg); // Set updated message
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("emojis")) {
            sender.sendMessage(headerMessage);
            StringBuilder emojisLine = new StringBuilder();

            for (Map.Entry<String, String> entry : emojiMap.entrySet()) {
                emojisLine.append("§f").append(entry.getKey())
                        .append(" ")  // just a space after the key
                        .append(entry.getValue())
                        .append("  ");  // space between emojis
            }

            sender.sendMessage(emojisLine.toString());
            sender.sendMessage(footerMessage);
            return true;
        }
        return false;
    }

    private void loadEmojisFromConfig() {
        FileConfiguration config = getConfig();
        if (!config.isConfigurationSection("replacements")) {
            getLogger().warning("No 'replacements' section found in config!");
            return;
        }

        Set<String> keys = config.getConfigurationSection("replacements").getKeys(false);
        for (String key : keys) {
            String value = config.getString("replacements." + key);
            emojiMap.put(key, value);
        }

        getLogger().info("Loaded " + emojiMap.size() + " emoji replacements.");
    }
}
