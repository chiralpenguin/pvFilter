package com.purityvanilla.pvfilter;

import net.kyori.adventure.text.Component;

/**
 * Filters text and Adventure components for blocked content.
 */
public interface TextFilter {

    /**
     * Filters blocked content from a plain text string, replacing matches
     * with the configured replacement string.
     *
     * @param text the raw text to filter
     * @return the filtered text with blocked content replaced
     */
    String filterText(String text);

    /**
     * Filters blocked content from an Adventure {@link Component},
     * preserving MiniMessage formatting such as colors and decorations.
     *
     * @param component the component to filter
     * @return a new component with blocked content replaced
     */
    Component filterComponent(Component component);
}
