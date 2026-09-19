package dev.doza.politic.webhook.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class WebhookConfig {

    private final JavaPlugin plugin;
    
    private boolean discordEnabled;
    private String discordUrl;
    
    private boolean telegramEnabled;
    private String telegramToken;
    private String telegramChatId;

    public WebhookConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        // Загрузка настроек Discord
        this.discordEnabled = config.getBoolean("webhooks.discord.enabled", false);
        this.discordUrl = config.getString("webhooks.discord.url", "");

        // Загрузка настроек Telegram
        this.telegramEnabled = config.getBoolean("webhooks.telegram.enabled", false);
        this.telegramToken = config.getString("webhooks.telegram.bot-token", "");
        this.telegramChatId = config.getString("webhooks.telegram.chat-id", "");
    }

    public boolean isDiscordEnabled() { return discordEnabled && !discordUrl.isEmpty(); }
    public String getDiscordUrl() { return discordUrl; }

    public boolean isTelegramEnabled() { return telegramEnabled && !telegramToken.isEmpty() && !telegramChatId.isEmpty(); }
    public String getTelegramToken() { return telegramToken; }
    public String getTelegramChatId() { return telegramChatId; }
}