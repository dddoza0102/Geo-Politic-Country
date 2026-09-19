package dev.doza.politic.items;

import dev.doza.politic.Plugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.doza.politic.config.manager.ConfigManager;

public class Items {
    public static boolean isPlayerHas(Player player, String path){
        if(!ConfigManager.getConfig("config").getBoolean(path+".items.enable")&&
                !ConfigManager.getConfig("config").getBoolean(path+".economy.enable")){
            return true;
        }

        if(ConfigManager.getConfig("config").getBoolean(path+".economy.enable")){
            if(!Plugin.getInstance().economy.has(player, ConfigManager.getConfig("config").getDouble("path.economy.sum"))){
                return false;
            }
        }

        if (ConfigManager.getConfig("config").getBoolean("need_to_create_country.items")) {
            String itemsConfig = ConfigManager.getConfig("config").getString(path+".items.item_config");
            if (ConfigManager.getConfig(itemsConfig) == null) {
                return false;
            }

            for (String key : ConfigManager.getConfig(itemsConfig).getKeys(false)) {
                String materialStr = ConfigManager.getConfig(itemsConfig).getString(key + ".material");
                if (materialStr == null) continue;

                Material material = Material.valueOf(materialStr.toUpperCase());
                int requiredAmount = ConfigManager.getConfig(itemsConfig).getInt(key + ".amount", 1);

                int totalCount = 0;
                for (ItemStack invItem : player.getInventory().getContents()) {
                    if (invItem != null && invItem.getType() == material) {
                        totalCount += invItem.getAmount();
                    }
                }

                if (totalCount < requiredAmount) {
                    return false;
                }
            }
        }

        return true;
    }
}
