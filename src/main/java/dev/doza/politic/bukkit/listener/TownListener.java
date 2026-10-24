package dev.doza.politic.bukkit.listener;

import dev.doza.politic.bukkit.util.ChatUtil;
import dev.doza.politic.model.town.Town;
import dev.doza.politic.model.town.manager.TownManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TownListener implements Listener {
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){
        if(TownManager.getTown((Player) e.getDamager())==null) return;

        Town town = TownManager.getTown((Player) e.getDamager());

        if(TownManager.getTown((Player) e.getEntity()) != town) return;

        if (!town.getFriendlyFire()) return;

        ChatUtil.send("friendly-fire-disabled");
        e.setCancelled(true);
    }
}
