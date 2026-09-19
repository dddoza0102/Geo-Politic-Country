package dev.doza.politic.country.manager;

import dev.doza.politic.chat.util.ChatUtil;
import dev.doza.politic.country.Country;
import dev.doza.politic.items.Items;
import dev.doza.politic.town.Town;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CountryManager {
    public static Map<String, Country> countries = new HashMap<>();

    public static void create(String name, UUID resident, String capitalName, int r,int g,int b){
        Player player = Bukkit.getPlayer(resident);
        if(!(player.hasPermission("geo.country.create"))){
            ChatUtil.send(player, "not_enough_permission");
            return;
        }

        if(Items.isPlayerHas(player, "need_to_create_country")){
            ChatUtil.send(player,"");
            return;
        }

        Town town = new Town(capitalName, resident, true);
        new Country(name, resident, town, r,g,b);
    }
}
