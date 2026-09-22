package dev.doza.politic.model.country.manager;

import dev.doza.politic.Plugin;
import dev.doza.politic.bukkit.util.ChatUtil;
import dev.doza.politic.model.country.Country;
import dev.doza.politic.bukkit.items.Items;
import dev.doza.politic.model.town.Town;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static dev.doza.politic.bukkit.util.ChatUtil.send;

public class CountryManager {
    public static Map<String, Country> countries = new HashMap<>();

    public static void create(String name, UUID resident, String capitalName, int r,int g,int b){
        Player player = Bukkit.getPlayer(resident);
        if(!(player.hasPermission("geo.country.create"))){
            send(player, "not_enough_permission");
            return;
        }

        if(countries.get(name) != null){
            send(player, "name_is_busy");
            return;
        }

        if(!Items.isPlayerHas(player, "need_to_create_country")){
            return;
        }

        Town town = new Town(capitalName, resident, true);
        new Country(name, resident, town, r,g,b);
    }

    public static void rename(Player player, Country country, String name){
        if(!(player.hasPermission("geo.country.rename"))){
            send(player,"not_enough_permission");
            return;
        }

        if(countries.get(country.getName()) == null){
            Plugin.getInstance().getLogger().severe("country is null!");
            return;
        }

        if(country.getName().equals(name)){
            send(player, "this_name_is_already_specified");
            return;
        }

        String oldName = country.getName();

        countries.remove(country.getName());
        country.setName(name);

        countries.put(name, country);
        sendMessageCountryPlayers(country, "player_rename_country", Map.of("{player}",player.getName(), "{old name}", oldName, "{new name}", name));
    }

    public static void invitePlayer(Player inviter, Country country, Player player){
        if(!(inviter.hasPermission("geo.country.invite"))){
            send(inviter, "not_enough_permission");
            return;
        }

        if(getCountry(player) != null){
            send(inviter, "player_already_in_country");
            return;
        }

        if(player.getPersistentDataContainer().has(Objects.requireNonNull(NamespacedKey.fromString(country.getName())), PersistentDataType.BOOLEAN)){
            send(inviter, "you_already_send_invitation");
            return;
        }

        player.getPersistentDataContainer().set(NamespacedKey.fromString(country.getName()),PersistentDataType.BOOLEAN, true);

        String fullText = ChatColor.translateAlternateColorCodes('&', ChatUtil.getConfigString("you_invited_in_country"));

        String[] parts = fullText.split("\\{confirm\\}|\\{cancel\\}");

        Component message = Component.text(parts.length > 0 ? parts[0] : "")
                .append(Component.text(ChatColor.translateAlternateColorCodes('&', ChatUtil.getConfigString("prefixes.success", (Map<String, String>) player)))
                        .clickEvent(ClickEvent.runCommand("/accept "+country.getName())))
                .append(Component.text(parts.length > 1 ? parts[1] : " "))
                .append(Component.text(ChatColor.translateAlternateColorCodes('&', ChatUtil.getConfigString("prefixes.cancel", (Map<String, String>) player)))
                        .clickEvent(ClickEvent.runCommand("/cancel "+country.getName())));

        if (parts.length > 2) {
            message = message.append(Component.text(parts[2]));
        }

        player.sendMessage(message);
    }

    public static void addPlayer(){

    }

    public static void join(){

    }

    public static void sendMessageCountryPlayers(Country country, String path){
        for(UUID uuid:country.getPlayers()){
            Player player = Bukkit.getPlayer(uuid);
            send(player, path);
        }
    }

    public static void sendMessageCountryPlayers(Country country, String path, Map<String, String> args){
        for(UUID uuid:country.getPlayers()){
            Player player = Bukkit.getPlayer(uuid);
            send(player, path, args);
        }
    }

    @Nullable
    public static Country getCountry(String name){
        if(!countries.isEmpty()){
            return countries.get(name);
        } else {
            return null;
        }
    }

    @Nullable
    public static Country getCountry(Player player){
        UUID uuid = player.getUniqueId();
        for(Map.Entry<String, Country> entry : countries.entrySet()){
            if(entry.getValue().getPlayers().contains(uuid)){
                return entry.getValue();
            }
        }
        return null;
    }
}
