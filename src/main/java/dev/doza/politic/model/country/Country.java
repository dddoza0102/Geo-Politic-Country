package dev.doza.politic.model.country;

import dev.doza.politic.model.country.manager.CountryManager;
import dev.doza.politic.model.region.Region;
import dev.doza.politic.model.region.flag.Flag;
import dev.doza.politic.model.region.flag.manager.FlagManager;
import dev.doza.politic.model.town.Town;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Country {
    private String name;
    private UUID resident;
    private Map<String, Town> towns;
    private double balance;
    private List<UUID> players;
    private Town capital;
    private Color color;
    private boolean friendlyFire;
    private List<Flag> MemberFlags;
    private List<Flag> NonMemberFlags;
    private boolean pvp;
    private Location center;
    private Map<UUID, List<Flag>> playersFlag;

    public Country(String name, UUID resident, Town capital, int r, int g, int b) {
        this.name = name;
        this.resident = resident;
        this.towns = Map.of(capital.getName(), capital);
        this.balance = 0.0;
        this.players = List.of(resident);
        this.capital = capital;
        this.color = new Color(r,g,b);
        this.friendlyFire = false;
        this.MemberFlags = FlagManager.getCountryFlags("member");
        this.NonMemberFlags = FlagManager.getCountryFlags("non-member");
        this.pvp = true;
        this.center = Bukkit.getPlayer(resident).getLocation();
        this.playersFlag = new HashMap<>();
        new Region(Bukkit.getPlayer(resident).getChunk(), this, capital);
        CountryManager.countries.put(name, this);
    }

    public UUID getResident() {
        return resident;
    }

    public void setResident(UUID resident) {
        this.resident = resident;
    }

    public Map<String, Town> getTowns() {
        return towns;
    }

    public void setTowns(Map<String, Town> towns) {
        this.towns = towns;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        CountryManager.countries.put(name, this);
    }

    public Town getCapital() {
        return capital;
    }

    public void setCapital(Town capital) {
        this.capital = capital;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
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

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public List<Flag> getNonMemberFlags() {
        return NonMemberFlags;
    }

    public void setNonMemberFlags(List<Flag> nonMemberFlags) {
        NonMemberFlags = nonMemberFlags;
    }

    public List<Flag> getMemberFlags() {
        return MemberFlags;
    }

    public void setMemberFlags(List<Flag> memberFlags) {
        MemberFlags = memberFlags;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public Map<UUID, List<Flag>> getPlayersFlag() {
        return playersFlag;
    }

    public void setPlayersFlag(Map<UUID, List<Flag>> playersFlag) {
        this.playersFlag = playersFlag;
    }
}
