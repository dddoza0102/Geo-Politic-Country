package dev.doza.politic;

import dev.doza.politic.bukkit.armor.FakeArmorManager;
import dev.doza.politic.bukkit.webhook.config.WebhookConfig;
import dev.doza.politic.bukkit.webhook.manager.WebhookManager;
import dev.doza.politic.storage.data.DatabaseManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

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
        FakeArmorManager armorManager = new FakeArmorManager(this);
        DatabaseManager.init();
        DatabaseManager.loadAll();
        getServer().getScheduler().runTaskTimer(this, DatabaseManager::saveAll, 6000L, 6000L);
    }

    public WebhookManager getWebhookManager() {
        return webhookManager;
    }

    @Override
    public void onDisable() {
        DatabaseManager.close();
    }
}
