package dev.doza.politic.model.town.manager;

import dev.doza.politic.model.country.Country;
import dev.doza.politic.model.town.Town;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class TownManager {
    public static Map<String, Town> towns;

    @Nullable
    public static Town getTown(String name){
        if(!towns.isEmpty()){
            return towns.get(name);
        } else {
            return null;
        }
    }

    @Nullable
    public static Town getTown(Player player){
        UUID uuid = player.getUniqueId();
        for(Map.Entry<String, Town> entry : towns.entrySet()){
            if(entry.getValue().getPlayers().contains(uuid)){
                return entry.getValue();
            }
        }
        return null;
    }
}
