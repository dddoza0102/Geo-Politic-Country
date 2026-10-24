package dev.doza.politic.bukkit.listener;

import dev.doza.politic.bukkit.util.ChatUtil;
import dev.doza.politic.model.country.Country;
import dev.doza.politic.model.country.manager.CountryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ContryListener implements Listener {
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){
        if(CountryManager.getCountry((Player) e.getDamager())==null) return;

        Country country = CountryManager.getCountry((Player) e.getDamager());

        if(CountryManager.getCountry((Player) e.getEntity()) != country) return;

        if (!country.getFriendlyFire()) return;

        ChatUtil.send("friendly-fire-disabled");
        e.setCancelled(true);
    }
}
