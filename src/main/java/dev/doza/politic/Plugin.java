package dev.doza.politic;

import dev.doza.politic.bukkit.armor.FakeArmorManager;
import dev.doza.politic.bukkit.webhook.config.WebhookConfig;
import dev.doza.politic.bukkit.webhook.manager.WebhookManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {
    
    private static Plugin instance;
    public Economy economy;

    private WebhookConfig webhookConfig;
    private WebhookManager webhookManager;

    private FakeArmorManager armorManager;

    public static Plugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.armorManager = new FakeArmorManager(this);
    }

    public WebhookManager getWebhookManager() {
        return webhookManager;
    }
}
