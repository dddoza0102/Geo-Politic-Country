package dev.doza.politic.bukkit.listener;

import dev.doza.politic.bukkit.util.ChatUtil;
import dev.doza.politic.model.country.manager.CountryManager;
import dev.doza.politic.model.region.flag.type.FlagType;
import dev.doza.politic.model.region.manager.RegionManager;
import dev.doza.politic.model.town.manager.TownManager;
import dev.doza.politic.model.region.flag.manager.FlagManager;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static dev.doza.politic.model.region.flag.manager.FlagManager.hasPermission;
import static dev.doza.politic.model.region.flag.manager.FlagManager.getPlayerData;

public class RegionListener {

    @EventHandler // Обработка удара по союзнику
    public void onHit(EntityDamageByEntityEvent e){
        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) return;

        Player damager = (Player) e.getDamager();
        Player victim = (Player) e.getEntity();

        if(CountryManager.getCountry(damager) == null || TownManager.getTown(damager) == null) return;
        if(CountryManager.getCountry(victim) != CountryManager.getCountry(damager) || TownManager.getTown(victim) != TownManager.getTown(damager)) return;
        if (!CountryManager.getCountry(damager).getFriendlyFire() || !TownManager.getTown(damager).getFriendlyFire()) return;

        ChatUtil.send("friendly-fire-disabled");
        e.setCancelled(true);
    }

    @EventHandler // Обработка удара в регионе
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

        // 1. Общая проверка региона на ломание блоков
        if (!hasPermission(player, loc, FlagType.BREAK)) {
            event.setCancelled(true);
            return;
        }

        // 2. Проверка персонального списка блоков для ломания
        var data = getPlayerData(player);
        if (data.getBreakableBlocks().contains(loc)) {
            if (!hasPermission(player, loc, FlagType.BREAK_MY)) {
                event.setCancelled(true);
            } else {
                data.getBreakableBlocks().remove(loc); // Успешно сломан, удаляем из памяти
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        // 1. Общая проверка региона на установку блоков
        if (!hasPermission(player, loc, FlagType.PLACE)) {
            event.setCancelled(true);
            return;
        }

        // 2. Проверка персонального списка разрешенных мест для установки
        var data = getPlayerData(player);
        if (data.getPlaceableBlocks().contains(loc)) {
            if (!hasPermission(player, loc, FlagType.PLACE_MY)) {
                event.setCancelled(true);
                return;
            } else {
                data.getPlaceableBlocks().remove(loc); // Место использовано
            }
        }

        // 3. Автоматически добавляем успешно установленный блок в список ломания этого игрока
        if (!event.isCancelled()) {
            data.getBreakableBlocks().add(loc);
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

        var data = getPlayerData(player);
        if (data.getBreakableBlocks().contains(loc)) {
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
