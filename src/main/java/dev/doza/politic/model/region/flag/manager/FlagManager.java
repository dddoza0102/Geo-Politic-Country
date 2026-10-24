package dev.doza.politic.model.region.flag.manager;

import dev.doza.politic.bukkit.manager.ConfigManager;
import dev.doza.politic.model.region.flag.type.FlagType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class FlagManager {
    static FileConfiguration townConfig = ConfigManager.getConfig("settings/town/flags");
    static FileConfiguration countryConfig = ConfigManager.getConfig("settings/town/flags");

    public static Map<FlagType, Boolean> getTownFlags(String group){
        Map<FlagType,Boolean> flags = new HashMap<>();
        for(String key: townConfig.getConfigurationSection(group).getKeys(false)){
            flags.put(FlagType.valueOf(townConfig.getString(key+".flag")), townConfig.getBoolean(key+".value"));;
        }
        return flags;
    }

    public static Map<FlagType, Boolean> getCountryFlags(String group){
        Map<FlagType,Boolean> flags = new HashMap<>();
        for(String key: countryConfig.getConfigurationSection(group).getKeys(false)){
            flags.put(FlagType.valueOf(countryConfig.getString(key+".flag")), countryConfig.getBoolean(key+".value"));;
        }
        return flags;
    }
}
