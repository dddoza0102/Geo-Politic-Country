package dev.doza.politic.model.region.manager;

import dev.doza.politic.model.region.Region;
import org.bukkit.Location;
import org.jspecify.annotations.Nullable;

import java.util.List;

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
}
