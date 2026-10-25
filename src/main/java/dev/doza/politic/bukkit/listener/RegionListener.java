package dev.doza.politic.bukkit.listener;

import dev.doza.politic.bukkit.util.ChatUtil;
import dev.doza.politic.model.country.Country;
import dev.doza.politic.model.country.manager.CountryManager;
import dev.doza.politic.model.region.flag.type.FlagType;
import dev.doza.politic.model.region.manager.RegionManager;
import dev.doza.politic.model.town.Town;
import dev.doza.politic.model.town.manager.TownManager;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static dev.doza.politic.model.region.flag.manager.FlagManager.hasPermission;
import static dev.doza.politic.model.region.flag.manager.FlagManager.players;

public class RegionListener {
    @EventHandler // Обработка удара по союзнику
    public void onHit(EntityDamageByEntityEvent e){
        if(CountryManager.getCountry((Player) e.getDamager())==null||TownManager.getTown((Player) e.getDamager())==null) return;

        if(CountryManager.getCountry((Player) e.getEntity()) != CountryManager.getCountry((Player) e.getDamager()) || TownManager.getTown((Player) e.getEntity()) != TownManager.getTown((Player) e.getDamager())) return;

        if (!CountryManager.getCountry((Player) e.getDamager()).getFriendlyFire()||!TownManager.getTown(((Player) e.getDamager())).getFriendlyFire()) return;

        ChatUtil.send("friendly-fire-disabled");
        e.setCancelled(true);
    }

    @EventHandler //Обработка удара в регионе
    public void onDamage(EntityDamageByEntityEvent e){
        if(!(e.getDamager() instanceof Player) && !(e.getEntity() instanceof Player)) return;

        if(
                !RegionManager.getRegion(e.getDamager().getLocation()).getCountry().isPvp() || !RegionManager.getRegion(e.getDamager().getLocation()).getTown().isPvp()
                        || !RegionManager.getRegion(e.getEntity().getLocation()).getCountry().isPvp() || !RegionManager.getRegion(e.getEntity().getLocation()).getTown().isPvp()
        ) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        // Проверка для обычного ломания
        if (!hasPermission(player, loc, FlagType.BREAK)) {
            event.setCancelled(true);
            return;
        }

        // Проверка для кастомного списка блоков
        var playerBlocks = players.get(player);
        if (playerBlocks != null && playerBlocks.contains(loc)) {
            if (!hasPermission(player, loc, FlagType.BREAK_MY)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        if (!hasPermission(player, loc, FlagType.PLACE)) {
            event.setCancelled(true);
            return;
        }

        var playerBlocks = players.get(player);
        if (playerBlocks != null && playerBlocks.contains(loc)) {
            if (!hasPermission(player, loc, FlagType.PLACE_MY)) {
                event.setCancelled(true);
            }else {
                playerBlocks.remove(loc)
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        Player player = event.getPlayer();
        Location loc = event.getClickedBlock().getLocation();

        if (!hasPermission(player, loc, FlagType.USE)) {
            event.setCancelled(true);
            return;
        }

        var playerBlocks = players.get(player);
        if (playerBlocks != null && playerBlocks.contains(loc)) {
            if (!hasPermission(player, loc, FlagType.USE_MY)) {
                event.setCancelled(true);
            }
        }

    }
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        Entity victim = event.getEntity();
        Location loc = victim.getLocation();

        if (victim instanceof Animals || victim instanceof WaterMob || victim instanceof Ambient) {
            if (!hasPermission(player, loc, FlagType.PVP_ANIMAL)) {
                event.setCancelled(true);
            }
        }
        else if (victim instanceof Monster || victim instanceof Slime || victim instanceof Enemy) {
            if (!hasPermission(player, loc, FlagType.PVP_MONSTER)) {
                event.setCancelled(true);
            }
        }
    }


}
