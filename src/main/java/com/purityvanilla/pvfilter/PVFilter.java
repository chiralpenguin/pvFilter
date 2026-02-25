package com.purityvanilla.pvfilter;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.purityvanilla.pvfilter.commands.ReloadCommand;
import com.purityvanilla.pvfilter.listeners.CommandPreProcessListener;
import com.purityvanilla.pvfilter.listeners.PrepareAnvilListener;
import com.purityvanilla.pvfilter.listeners.packetevents.BossBarListener;
import com.purityvanilla.pvfilter.listeners.packetevents.EditBookListener;
import com.purityvanilla.pvfilter.listeners.packetevents.EntityMetaDataListener;
import com.purityvanilla.pvfilter.listeners.packetevents.UpdateSignListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PVFilter extends JavaPlugin {
    private Config config;
    private static TextFilter textFilter;

    @Override
    public void onEnable() {
        config = new Config(getLogger());
        textFilter = new PipelineTextFilter(config);

        registerCommands();
        registerListeners();
    }


    public void reload() {
        config = new Config(getLogger());
        textFilter = new PipelineTextFilter(config);
    }

    private void registerCommands() {
        getCommand("reload").setExecutor(new ReloadCommand(this));
    }

    private void registerListeners() {
        // Bukkit event listeners
        getServer().getPluginManager().registerEvents(new CommandPreProcessListener(this), this);
        getServer().getPluginManager().registerEvents(new PrepareAnvilListener(this), this);

        // PacketEvents packet listeners
        List<PacketListener> packetListeners = List.of(
                new BossBarListener(this),
                new EditBookListener(this),
                new EntityMetaDataListener(this),
                new UpdateSignListener(this)
        );

        for (PacketListener packetListener : packetListeners) {
            PacketEvents.getAPI().getEventManager().registerListener(packetListener, PacketListenerPriority.NORMAL);
        }

    }

    public Config config() {
        return config;
    }

    public static TextFilter getTextFilter() {
        return textFilter;
    }
}
