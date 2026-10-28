package dev.doza.politic.bukkit.armor;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FakeArmorManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Color> activeFakeArmor = new ConcurrentHashMap<>();

    public FakeArmorManager(JavaPlugin plugin) {
        this.plugin = plugin;
        registerPacketListener();
    }

    public void enableFakeArmor(Player target, Color color) {
        activeFakeArmor.put(target.getUniqueId(), color);
        updateArmorPackets(target, color);
    }

    public void disableFakeArmor(Player target) {
        if (activeFakeArmor.remove(target.getUniqueId()) != null) {
            updateArmorPackets(target, null);
        }
    }

    public boolean hasFakeArmor(Player target) {
        return activeFakeArmor.containsKey(target.getUniqueId());
    }

    private void registerPacketListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                plugin,
                ListenerPriority.HIGHEST,
                PacketType.Play.Server.ENTITY_EQUIPMENT
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player receiver = event.getPlayer();
                
                int entityId = packet.getIntegers().read(0);
                Player target = Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.getEntityId() == entityId)
                        .findFirst()
                        .orElse(null);

                if (target == null || !activeFakeArmor.containsKey(target.getUniqueId())) return;
                if (receiver.equals(target)) return;

                Color color = activeFakeArmor.get(target.getUniqueId());
                List<Pair<ItemSlot, ItemStack>> fakeEquipment = createFakeEquipmentList(target, color);
                packet.getSlotStackPairLists().write(0, fakeEquipment);
            }
        });
    }

    private void updateArmorPackets(Player target, Color color) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet.getIntegers().write(0, target.getEntityId());
        
        List<Pair<ItemSlot, ItemStack>> equipment = createFakeEquipmentList(target, color);
        packet.getSlotStackPairLists().write(0, equipment);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.equals(target)) continue;
            
            if (onlinePlayer.getWorld().equals(target.getWorld()) && 
                onlinePlayer.getLocation().distanceSquared(target.getLocation()) < 2500) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(onlinePlayer, packet);
            }
        }
    }

    private List<Pair<ItemSlot, ItemStack>> createFakeEquipmentList(Player target, Color color) {
        List<Pair<ItemSlot, ItemStack>> list = new ArrayList<>();
        
        if (color != null) {
            list.add(new Pair<>(ItemSlot.HEAD, createColoredArmor(Material.LEATHER_HELMET, color)));
            list.add(new Pair<>(ItemSlot.CHEST, createColoredArmor(Material.LEATHER_CHESTPLATE, color)));
            list.add(new Pair<>(ItemSlot.LEGS, createColoredArmor(Material.LEATHER_LEGGINGS, color)));
            list.add(new Pair<>(ItemSlot.FEET, createColoredArmor(Material.LEATHER_BOOTS, color)));
        } else {
            var inv = target.getInventory();
            list.add(new Pair<>(ItemSlot.HEAD, inv.getHelmet()));
            list.add(new Pair<>(ItemSlot.CHEST, inv.getChestplate()));
            list.add(new Pair<>(ItemSlot.LEGS, inv.getLeggings()));
            list.add(new Pair<>(ItemSlot.FEET, inv.getBoots()));
        }
        return list;
    }

    private ItemStack createColoredArmor(Material material, Color color) {
        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        if (meta != null) {
            meta.setColor(color);
            item.setItemMeta(meta);
        }
        return item;
    }
}
