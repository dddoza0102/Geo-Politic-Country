package dev.doza.politic.model.region;

import dev.doza.politic.bukkit.region.RegionManager;
import dev.doza.politic.model.country.Country;
import dev.doza.politic.model.town.Town;
import org.bukkit.Chunk;

public class Region {
    private Chunk chunk;
    private Country country;
    private Town town;

    public Region (Chunk chunk, Country country, Town town){
        this.chunk = chunk;
        this.country = country;
        this.town = town;
        RegionManager.regions.add(this);
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public Town getTown() {
        return town;
    }
}
