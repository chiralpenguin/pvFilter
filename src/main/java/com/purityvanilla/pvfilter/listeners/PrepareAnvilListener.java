package com.purityvanilla.pvfilter.listeners;

import com.purityvanilla.pvfilter.PVFilter;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PrepareAnvilListener implements Listener {
    private final PVFilter plugin;

    public PrepareAnvilListener(PVFilter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!plugin.config().contentFilterEnabled()) return;
        if (event.getResult() == null || event.getInventory().getRenameText() == null || event.getInventory().getRenameText().isEmpty()) {
            return;
        }

        ItemStack result = event.getResult();
        ItemMeta meta = result.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        Component filteredName = PVFilter.getTextFilter().filterComponent(meta.displayName());
        meta.displayName(filteredName);
        result.setItemMeta(meta);
    }
}
