package dev.doza.politic.model.country.manager;

import dev.doza.politic.Plugin;
import dev.doza.politic.bukkit.util.ChatUtil;
import dev.doza.politic.model.country.Country;
import dev.doza.politic.bukkit.items.Items;
import dev.doza.politic.model.town.Town;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static dev.doza.politic.bukkit.util.ChatUtil.send;

public class CountryManager {
    public static Map<String, Country> countries = new HashMap<>();

    public static void create(String name, UUID resident, String capitalName, int r, int g, int b){
        Player player = Bukkit.getPlayer(resident);
        if(player == null){
            return;
        }

        if(!player.hasPermission("geo.country.create")){
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
        new Country(name, resident, town, r, g, b);
    }

    public static void rename(Player player, Country country, String name){
        if(!player.hasPermission("geo.country.rename")){
            send(player, "not_enough_permission");
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

        if(countries.get(name) != null){
            send(player, "name_is_busy");
            return;
        }

        String oldName = country.getName();

        countries.remove(oldName);
        country.setName(name);
        countries.put(name, country);

        sendMessageCountryPlayers(country, "player_rename_country", Map.of(
                "{player}", player.getName(),
                "{old name}", oldName,
                "{new name}", name
        ));
    }

    public static void invitePlayer(Player inviter, Country country, Player player){
        if(!inviter.hasPermission("geo.country.invite")){
            send(inviter, "not_enough_permission");
            return;
        }

        if(getCountry(player) != null){
            send(inviter, "player_already_in_country");
            return;
        }

        NamespacedKey invitationKey = getInvitationKey(country);

        if(player.getPersistentDataContainer().has(invitationKey, PersistentDataType.BOOLEAN)){
            send(inviter, "you_already_send_invitation");
            return;
        }

        player.getPersistentDataContainer().set(invitationKey, PersistentDataType.BOOLEAN, true);

        String fullText = ChatUtil.getConfigString("you_invited_in_country", Map.of("{country}", country.getName()));
        String[] parts = fullText.split("\\{confirm\\}|\\{cancel\\}", -1);

        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();

        Component acceptButton = serializer.deserialize(ChatUtil.getConfigString("prefixes.success"))
                .clickEvent(ClickEvent.runCommand("/accept " + country.getName()));
        Component cancelButton = serializer.deserialize(ChatUtil.getConfigString("prefixes.cancel"))
                .clickEvent(ClickEvent.runCommand("/cancel " + country.getName()));

        Component message = serializer.deserialize(parts[0])
                .append(acceptButton)
                .append(serializer.deserialize(parts.length > 1 ? parts[1] : " "))
                .append(cancelButton);

        if(parts.length > 2){
            message = message.append(serializer.deserialize(parts[2]));
        }

        player.sendMessage(message);
        send(inviter, "invitation_sent", Map.of("{player}", player.getName()));
    }

    public static void addPlayer(Country country, Player player){
        country.getPlayers().add(player.getUniqueId());
        sendMessageCountryPlayers(country, "player_joined_country", Map.of(
                "{player}", player.getName(),
                "{country}", country.getName()
        ));
    }

    public static void join(Player player, Country country){
        if(getCountry(player) != null){
            send(player, "you_already_in_country");
            return;
        }

        NamespacedKey invitationKey = getInvitationKey(country);

        if(!player.getPersistentDataContainer().has(invitationKey, PersistentDataType.BOOLEAN)){
            send(player, "you_have_no_invitation");
            return;
        }

        player.getPersistentDataContainer().remove(invitationKey);
        addPlayer(country, player);
    }

    public static void declineInvitation(Player player, Country country){
        NamespacedKey invitationKey = getInvitationKey(country);

        if(!player.getPersistentDataContainer().has(invitationKey, PersistentDataType.BOOLEAN)){
            send(player, "you_have_no_invitation");
            return;
        }

        player.getPersistentDataContainer().remove(invitationKey);
        send(player, "invitation_declined");
    }

    public static void sendMessageCountryPlayers(Country country, String path){
        sendMessageCountryPlayers(country, path, Map.of());
    }

    public static void sendMessageCountryPlayers(Country country, String path, Map<String, String> args){
        for(UUID uuid : country.getPlayers()){
            Player player = Bukkit.getPlayer(uuid);
            if(player != null){
                send(player, path, args);
            }
        }
    }

    @Nullable
    public static Country getCountry(String name){
        return countries.get(name);
    }

    @Nullable
    public static Country getCountry(Player player){
        UUID uuid = player.getUniqueId();
        for(Country country : countries.values()){
            if(country.getPlayers().contains(uuid)){
                return country;
            }
        }
        return null;
    }

    private static NamespacedKey getInvitationKey(Country country){
        UUID nameId = UUID.nameUUIDFromBytes(country.getName().getBytes(StandardCharsets.UTF_8));
        return new NamespacedKey(Plugin.getInstance(), "invite_" + nameId);
    }
}