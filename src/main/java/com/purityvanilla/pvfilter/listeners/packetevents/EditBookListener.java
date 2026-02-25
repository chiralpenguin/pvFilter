package com.purityvanilla.pvfilter.listeners.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEditBook;
import com.purityvanilla.pvfilter.PVFilter;

import java.util.ArrayList;
import java.util.List;

public class EditBookListener implements PacketListener {
    private final PVFilter plugin;

    public EditBookListener(PVFilter plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!plugin.config().contentFilterEnabled()) return;
        if (event.getPacketType() != PacketType.Play.Client.EDIT_BOOK) return;
        WrapperPlayClientEditBook packet = new WrapperPlayClientEditBook(event);

        List<String> pages = packet.getPages();
        if (pages == null) return;

        List<String> filteredPages = new ArrayList<>();
        for (String page : pages) {
            filteredPages.add(PVFilter.getTextFilter().filterText(page));
        }

        packet.setPages(filteredPages);
    }
}
