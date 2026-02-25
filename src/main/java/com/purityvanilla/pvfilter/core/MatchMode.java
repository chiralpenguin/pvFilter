package com.purityvanilla.pvfilter.core;

/**
 * Controls how a blocked term is matched against input text.
 */
public enum MatchMode {

    /**
     * Matches anywhere in the normalized text, ignoring whitespace and
     * non-alpha characters. Use for severe terms where aggressive evasion
     * detection is worth the risk of false positives.
     */
    SUBSTRING,

    /**
     * Matches only when the term appears at word boundaries in the original
     * text.
     */
    WORD
}
