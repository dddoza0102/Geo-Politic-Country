package dev.doza.politic.country;

import dev.doza.politic.country.manager.CountryManager;
import dev.doza.politic.town.Town;

import java.awt.*;
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

    public Country(String name, UUID resident, Town capital, int r, int g, int b) {
        this.name = name;
        this.resident = resident;
        this.towns = Map.of(capital.getName(), capital);
        this.balance = 0.0;
        this.players = List.of(resident);
        this.capital = capital;
        this.color = new Color(r,g,b);
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
}
