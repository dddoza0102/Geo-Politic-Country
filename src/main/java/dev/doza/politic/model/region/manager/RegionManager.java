package dev.doza.politic.model.region.manager;

import dev.doza.politic.bukkit.util.CraftUtil;
import dev.doza.politic.model.country.Country;
import dev.doza.politic.model.country.manager.CountryManager;
import dev.doza.politic.model.region.Region;
import dev.doza.politic.model.town.Town;
import dev.doza.politic.model.town.manager.TownManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static dev.doza.politic.bukkit.util.ChatUtil.*;

public class RegionManager {
    public static List<Region> regions;

    @Nullable
    public static Region getRegion(Location location){
        for(Region region:regions){
            if(region.getChunk() == location.getChunk()){
                return region;
            }
        }
        return null;
    }

    public static void setRegion(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (CraftUtil.isCraftItem(itemStack, "chunk_country")) {
            Country country = CountryManager.getCountry(player);

            if (country == null) {
                send(player, "you_have_no_country");
                return;
            }

            if (!hasAdjacentRegion(country, player.getChunk())) {
                send(player, "chunk_have_no_adjacent_region");
                return;
            }

            new Region(player.getChunk(), country, null);
        } else if (CraftUtil.isCraftItem(itemStack, "chunk_town")) {
            Town town = TownManager.getTown(player);
            Country country = CountryManager.getCountry(player);

            if (town == null) {
                send(player, "you_have_no_town");
            }

            if (!hasAdjacentRegion(town, player.getChunk())) {
                send(player, "chunk_have_no_adjacent_region");
                return;
            }
            new Region(player.getChunk(), country, town);
        }
    }

    public static void setRegion(Country country){
        new Region(country.getCenter().getChunk(), country, country.getCapital());
    }

    public static List<Region> getAdjacentRegions(Country country, Chunk chunk) {
        return findAdjacent(chunk, region -> region.getCountry() == country);
    }

    public static boolean hasAdjacentRegion(Country country, Chunk chunk) {
        return !getAdjacentRegions(country, chunk).isEmpty();
    }

    public static List<Region> getAdjacentRegions(Town town, Chunk chunk) {
        return findAdjacent(chunk, region -> region.getTown() == town);
    }

    public static boolean hasAdjacentRegion(Town town, Chunk chunk) {
        return !getAdjacentRegions(town, chunk).isEmpty();
    }

    private static List<Region> findAdjacent(Chunk chunk, Predicate<Region> owner) {
        List<Region> result = new ArrayList<>();

        for (Region region : regions) {
            Chunk other = region.getChunk();
            if (!other.getWorld().equals(chunk.getWorld()) || !owner.test(region)) {
                continue;
            }

            int distance = Math.abs(other.getX() - chunk.getX()) + Math.abs(other.getZ() - chunk.getZ());
            if (distance == 1) {
                result.add(region);
            }
        }

        return result;
    }
}
