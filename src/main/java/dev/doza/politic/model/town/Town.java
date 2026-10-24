package dev.doza.politic.model.town;

import dev.doza.politic.model.country.manager.CountryManager;
import dev.doza.politic.model.region.Region;
import dev.doza.politic.model.region.flag.manager.FlagManager;
import dev.doza.politic.model.region.flag.type.FlagType;
import dev.doza.politic.model.town.manager.TownManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Town {
    private String name;
    private UUID mayor;
    private double balance;
    private List<UUID> players;
    private boolean isCapital;
    private boolean friendlyFire;
    private Map<FlagType, Boolean> Memberflags;
    private Map<FlagType, Boolean> NonMemberFlags;
    private Map<FlagType, Boolean> CountryMemberFlags;
    private boolean pvp;
    private Location center;

    public Town(String name, UUID mayor, boolean capital) {
        this.name = name;
        this.mayor = mayor;
        this.balance = 0;
        this.players = List.of(mayor);
        this.isCapital = capital;
        this.friendlyFire = false;
        this.Memberflags = FlagManager.getTownFlags("member");
        this.NonMemberFlags = FlagManager.getTownFlags("non-member");
        this.NonMemberFlags = FlagManager.getTownFlags("country-member");
        this.pvp = true;
        this.center = Bukkit.getPlayer(mayor).getLocation();
        new Region(Bukkit.getPlayer(mayor).getChunk(), CountryManager.getCountry(Bukkit.getPlayer(mayor)), this);
        TownManager.towns.put(name, this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        TownManager.towns.put(name,this);
    }

    public UUID getMayor() {
        return mayor;
    }

    public void setMayor(UUID mayor) {
        this.mayor = mayor;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void setPlayers(List<UUID> players) {
        this.players = players;
    }

    public boolean isCapital() {
        return isCapital;
    }

    public void setCapital(boolean capital) {
        isCapital = capital;
    }

    public boolean getFriendlyFire(){
        return friendlyFire;
    }
    public void setFriendlyFire(boolean friendlyFire){
        this.friendlyFire = friendlyFire;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public Map<FlagType, Boolean> getMemberflags() {
        return Memberflags;
    }

    public void setMemberflags(Map<FlagType, Boolean> memberflags) {
        Memberflags = memberflags;
    }

    public Map<FlagType, Boolean> getNonMemberFlags() {
        return NonMemberFlags;
    }

    public void setNonMemberFlags(Map<FlagType, Boolean> nonMemberFlags) {
        NonMemberFlags = nonMemberFlags;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public Map<FlagType, Boolean> getCountryMemberFlags() {
        return CountryMemberFlags;
    }

    public void setCountryMemberFlags(Map<FlagType, Boolean> countryMemberFlags) {
        CountryMemberFlags = countryMemberFlags;
    }
}
