package com.purityvanilla.pvfilter.listeners;

import com.purityvanilla.pvfilter.PVFilter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPreProcessListener implements Listener {
    private final PVFilter plugin;

    public CommandPreProcessListener(PVFilter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandPreProcessEvent(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String command = message.split(" ")[0].toLowerCase();

        String filtered = PVFilter.getTextFilter().filterText(message);
        event.setMessage(filtered);
    }
}
