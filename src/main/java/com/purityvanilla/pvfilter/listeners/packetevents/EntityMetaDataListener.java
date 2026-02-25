package com.purityvanilla.pvfilter.listeners.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.purityvanilla.pvfilter.PVFilter;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class EntityMetaDataListener implements PacketListener {
    private final PVFilter plugin;

    public EntityMetaDataListener(PVFilter plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (!plugin.config().contentFilterEnabled()) return;
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_METADATA) return;
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);

        for (EntityData metadata : packet.getEntityMetadata())
            if (metadata.getIndex() == 2 && metadata.getValue() instanceof Optional<?> opt) {
                if (opt.isPresent() && opt.get() instanceof Component name) {
                    Component filtered = PVFilter.getTextFilter().filterComponent(name);
                    metadata.setValue(Optional.of(filtered));
                }
            }
    }
}
