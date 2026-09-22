package dev.doza.politic.bukkit.items;

import dev.doza.politic.Plugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.doza.politic.bukkit.manager.ConfigManager;

import java.util.HashMap;
import java.util.Map;

import static dev.doza.politic.bukkit.util.ChatUtil.*;

public class Items {
    public static boolean isPlayerHas(Player player, String path){
        if(!ConfigManager.getConfig("config").getBoolean(path+".items.enable")&&
                !ConfigManager.getConfig("config").getBoolean(path+".economy.enable")){
            return true;
        }

        if(ConfigManager.getConfig("config").getBoolean(path+".economy.enable")){
            if(!Plugin.getInstance().economy.has(player, ConfigManager.getConfig("config").getDouble("path.economy.sum"))){
                send(player, ConfigManager.getConfig(
                        Plugin.getInstance().getConfig().getString("lang_file")).getStringList("not_enough_resources_for_create_country").getFirst()
                );
                send(player, ConfigManager.getConfig(
                        Plugin.getInstance().getConfig().getString("lang_file")).getStringList("not_enough_resources_for_create_country").get(1),
                        Map.of("{sum}",ConfigManager.getConfig("config").getDouble("path.economy.sum")+"")
                        );
                return false;
            }
        }

        Map<String, Integer> falseItems= new HashMap<>();

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
                    falseItems.put(ConfigManager.getConfig(itemsConfig).getString(key+".name"),requiredAmount-totalCount);
                }
            }
            if(!falseItems.isEmpty()){
                send(player, ConfigManager.getConfig(
                        Plugin.getInstance().getConfig().getString("lang_file")).getStringList("not_enough_resources_for_create_country").getFirst()
                );
                for (Map.Entry<String, Integer> entry : falseItems.entrySet()) {
                    send(player, ConfigManager.getConfig(
                                    Plugin.getInstance().getConfig().getString("lang_file")).getStringList("not_enough_resources_for_create_country").get(2),
                            Map.of("{item}", entry.getKey(),"{amount}",entry.getValue()+"")
                    );
                }
                return false;
            }
        }

        return true;
    }
}
