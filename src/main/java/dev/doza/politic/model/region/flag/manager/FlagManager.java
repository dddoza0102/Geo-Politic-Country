package dev.doza.politic.model.region.flag.manager;

import dev.doza.politic.bukkit.manager.ConfigManager;
import dev.doza.politic.model.country.Country;
import dev.doza.politic.model.country.manager.CountryManager;
import dev.doza.politic.model.region.Region;
import dev.doza.politic.model.region.flag.Flag;
import dev.doza.politic.model.region.flag.type.FlagType;
import dev.doza.politic.model.region.manager.RegionManager;
import dev.doza.politic.model.town.Town;
import dev.doza.politic.model.town.manager.TownManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FlagManager {
    static FileConfiguration townConfig = ConfigManager.getConfig("settings/town/flags");
    static FileConfiguration countryConfig = ConfigManager.getConfig("settings/town/flags");

    // Хранилище данных о блоках игроков
    public static final Map<UUID, PlayerBlockData> playerBlocks = new ConcurrentHashMap<>();

    // Класс-контейнер для хранения двух списков локаций
    public static class PlayerBlockData {
        private final List<Location> breakableBlocks = new ArrayList<>();
        private final List<Location> placeableBlocks = new ArrayList<>();

        public List<Location> getBreakableBlocks() { return breakableBlocks; }
        public List<Location> getPlaceableBlocks() { return placeableBlocks; }
    }

    // Удобный метод получения данных игрока с авто-созданием
    public static PlayerBlockData getPlayerData(Player player) {
        return playerBlocks.computeIfAbsent(player.getUniqueId(), k -> new PlayerBlockData());
    }

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

    public static Boolean hasPermission(Player player, Location targetLocation, FlagType flagType) {
        Region region = RegionManager.getRegion(targetLocation);
        if (region == null) return true;

        Town playerTown = TownManager.getTown(player);
        Town regionTown = region.getTown();

        if (playerTown != null && playerTown.equals(regionTown)) {
            var pFlags = playerTown.getPlayersFlag().get(player.getUniqueId());
            if (pFlags != null) {
                return pFlags.stream().filter(f -> f.getType() == flagType).map(Flag::isValue).findFirst().orElse(false);
            }
        }
        else {
            Country playerCountry = CountryManager.getCountry(player);
            Country regionCountry = region.getCountry();

            if (playerCountry != null && playerCountry.equals(regionCountry)) {
                if (regionTown != null) {
                    for (Flag flag : regionTown.getCountryMemberFlags()) {
                        if (flag.getType() == flagType) return flag.isValue();
                    }
                }
                var pFlags = playerCountry.getPlayersFlag().get(player.getUniqueId());
                if (pFlags != null) {
                    return pFlags.stream().filter(f -> f.getType() == flagType).map(Flag::isValue).findFirst().orElse(false);
                }
            }
            else if (regionTown != null) {
                for (Flag flag : regionTown.getNonMemberFlags()) {
                    if (flag.getType() == flagType) return flag.isValue();
                }
            }
        }
        return false;
    }
}
