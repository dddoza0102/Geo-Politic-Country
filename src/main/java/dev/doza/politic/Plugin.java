package dev.doza.politic;

import dev.doza.politic.webhook.config.WebhookConfig;
import dev.doza.politic.webhook.manager.WebhookManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {
    
    private static Plugin instance;
    public Economy economy;

    private WebhookConfig webhookConfig;
    private WebhookManager webhookManager;

    public static Plugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
    }

    public WebhookManager getWebhookManager() {
        return webhookManager;
    }
}
