package dev.doza.politic.bukkit.listener;

import dev.doza.politic.bukkit.util.ChatUtil;
import dev.doza.politic.model.country.Country;
import dev.doza.politic.model.country.manager.CountryManager;
import dev.doza.politic.model.region.manager.RegionManager;
import dev.doza.politic.model.town.Town;
import dev.doza.politic.model.town.manager.TownManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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

}
