package dev.doza.politic.bukkit.items;

import dev.doza.politic.Plugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import dev.doza.politic.bukkit.manager.ConfigManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static dev.doza.politic.bukkit.util.ChatUtil.*;

public class Items {
    private static final String RESOURCES_MESSAGE_PATH = "not_enough_resources_for_create_country";

    public static boolean isPlayerHas(Player player, String path){
        double missingMoney = getMissingMoney(player, path);
        Map<String, Integer> missingItems = getMissingItems(player, path);

        if(missingMoney <= 0 && missingItems.isEmpty()){
            return true;
        }

        send(player, RESOURCES_MESSAGE_PATH + ".header");

        if(missingMoney > 0){
            send(player, RESOURCES_MESSAGE_PATH + ".money", Map.of("{sum}", formatSum(missingMoney)));
        }

        for(Map.Entry<String, Integer> entry : missingItems.entrySet()){
            send(player, RESOURCES_MESSAGE_PATH + ".item", Map.of(
                    "{item}", entry.getKey(),
                    "{amount}", String.valueOf(entry.getValue())
            ));
        }

        return false;
    }

    public static double getMissingMoney(Player player, String path){
        var config = ConfigManager.getConfig("config");

        if(!config.getBoolean(path + ".economy.enable")){
            return 0;
        }

        double requiredSum = config.getDouble(path + ".economy.sum");
        double balance = Plugin.getInstance().economy.getBalance(player);

        return Math.max(0, requiredSum - balance);
    }

    public static Map<String, Integer> getMissingItems(Player player, String path){
        Map<String, Integer> missingItems = new LinkedHashMap<>();
        var config = ConfigManager.getConfig("config");

        if(!config.getBoolean(path + ".items.enable")){
            return missingItems;
        }

        String itemsConfigName = config.getString(path + ".items.item_config");
        var itemsConfig = ConfigManager.getConfig(itemsConfigName);

        if(itemsConfig == null){
            throw new IllegalStateException("Items config '" + itemsConfigName + "' not found for path " + path);
        }

        for(String key : itemsConfig.getKeys(false)){
            Material material = Material.matchMaterial(itemsConfig.getString(key + ".material", ""));
            if(material == null){
                continue;
            }

            int requiredAmount = itemsConfig.getInt(key + ".amount", 1);
            int totalCount = countItems(player, material);

            if(totalCount < requiredAmount){
                missingItems.put(itemsConfig.getString(key + ".name", key), requiredAmount - totalCount);
            }
        }

        return missingItems;
    }

    private static int countItems(Player player, Material material){
        int totalCount = 0;

        for(ItemStack itemStack : player.getInventory().getContents()){
            if(itemStack != null && itemStack.getType() == material){
                totalCount += itemStack.getAmount();
            }
        }

        return totalCount;
    }

    private static String formatSum(double sum){
        if(sum == Math.floor(sum)){
            return String.valueOf((long) sum);
        }
        return String.format("%.2f", sum);
    }
}
