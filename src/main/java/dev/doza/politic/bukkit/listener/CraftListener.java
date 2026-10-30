package dev.doza.politic.bukkit.listener;

import dev.doza.politic.bukkit.util.CraftUtil;
import dev.doza.politic.model.country.Country;
import dev.doza.politic.model.country.manager.CountryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import java.util.Map;

public class CraftListener implements Listener {
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        String id = CraftUtil.getCraftId(event.getRecipe());
        if (id == null || !CraftUtil.requiresCountry(id)) {
            return;
        }

        if (!(event.getView().getPlayer() instanceof Player player)) {
            return;
        }

        Country country = CountryManager.getCountry(player);
        if (country == null) {
            event.getInventory().setResult(null);
            return;
        }

        event.getInventory().setResult(CraftUtil.createItem(id, Map.of("{country}", country.getName()), country.getColor()));
    }
}