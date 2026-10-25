package dev.doza.politic.model.region.flag.manager;

import dev.doza.politic.bukkit.manager.ConfigManager;
import dev.doza.politic.model.region.flag.Flag;
import dev.doza.politic.model.region.flag.type.FlagType;
import dev.doza.politic.model.region.manager.RegionManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class FlagManager {
    static FileConfiguration townConfig = ConfigManager.getConfig("settings/town/flags");
    static FileConfiguration countryConfig = ConfigManager.getConfig("settings/town/flags");

    public static Map<UUID, List<Flag>> players = new HashMap<>();

    public static List<Flag> getTownFlags(String group){
        List<Flag> flags = new ArrayList<>();
        for(String key: townConfig.getConfigurationSection(group).getKeys(false)){
            flags.add(new Flag(FlagType.valueOf(countryConfig.getString(key+".flag")), countryConfig.getBoolean(key+".value")));
        }
        return flags;
    }

    public static List<Flag> getCountryFlags(String group){
        List<Flag> flags = new ArrayList<>();
        for(String key: countryConfig.getConfigurationSection(group).getKeys(false)){
            flags.add(new Flag(FlagType.valueOf(countryConfig.getString(key+".flag")), countryConfig.getBoolean(key+".value")));
        }
        return flags;
    }

    public static List<Flag> getPlayerFlags(Player player){
        UUID uuid = player.getUniqueId();
        return players.get(uuid);
    }

    public static Boolean getBreak(Player player){
        if (getPlayerFlags(player) != null){
            for(Flag flag:getPlayerFlags(player)){
                if(flag.getType() == FlagType.BREAK){
                    return flag.isValue();
                }
            }
        }

        if (RegionManager.ge){

        }
    }
}
