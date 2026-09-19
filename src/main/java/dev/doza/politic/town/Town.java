package dev.doza.politic.town;

import dev.doza.politic.town.manager.TownManager;

import java.util.List;
import java.util.UUID;

public class Town {
    private String name;
    private UUID mayor;
    private double balance;
    private List<UUID> players;
    private boolean isCapital;

    public Town(String name, UUID mayor, boolean capital) {
        this.name = name;
        this.mayor = mayor;
        this.balance = 0;
        this.players = List.of(mayor);
        this.isCapital = capital;
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
}
