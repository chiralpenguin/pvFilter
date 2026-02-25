package com.purityvanilla.pvfilter.listeners.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import com.purityvanilla.pvfilter.PVFilter;

public class UpdateSignListener implements PacketListener {
    private final PVFilter plugin;

    public UpdateSignListener(PVFilter plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!plugin.config().contentFilterEnabled()) return;
        if (event.getPacketType() != PacketType.Play.Client.UPDATE_SIGN) return;
        WrapperPlayClientUpdateSign packet = new WrapperPlayClientUpdateSign(event);

        String[] lines = packet.getTextLines();
        for (int i = 0; i < lines.length; i++) {
            String original = lines[i];
            lines[i] = PVFilter.getTextFilter().filterText(original);
        }

        packet.setTextLines(lines);
    }
}