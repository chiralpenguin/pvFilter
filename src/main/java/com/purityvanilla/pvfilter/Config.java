package com.purityvanilla.pvfilter;

import com.purityvanilla.pvlib.config.ConfigFile;
import com.purityvanilla.pvlib.config.Messages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Config extends ConfigFile {
    private final Set<Integer> blockedChars;
    private final Set<String> blockedStrings;
    private final Set<String> blockedWords;
    private final boolean enableContentFilter;
    private final String replacementString;
    private final boolean verbose;

    public Config(Logger logger) {
        super("plugins/pvChat/config.yml");
        messages = new Messages(this, "plugins/pvChat/messages.json");

        Set<Integer> loadedChars = Set.of();
        try {
            loadedChars = readBlockedCharacters("plugins/pvChat/blocked_chars.txt");
        } catch (IOException e) {
            logger.severe("Could not read blocked_chars.txt! Ensure the file exists and is valid.");
        }
        blockedChars = loadedChars;

        Set<String> loadedStrings = new HashSet<>();
        Set<String> loadedWords = new HashSet<>();
        try {
            for (String line : readBlockedStrings("plugins/pvChat/blocked_words.txt")) {
                if (line.startsWith("*")) {
                    loadedStrings.add(line.substring(1).strip());
                } else {
                    loadedWords.add(line);
                }
            }
        } catch (IOException e) {
            logger.severe("Could not read blocked_words.txt! Ensure the file exists and is valid.");
        }
        blockedStrings = loadedStrings;
        blockedWords = loadedWords;

        enableContentFilter = configRoot.node("enable-content-filter").getBoolean();
        replacementString = configRoot.node("replacement-string").getString();
        verbose = configRoot.node("verbose").getBoolean();
    }

    private Set<Integer> readBlockedCharacters(String filepath) throws IOException {
        Set<Integer> blocked = new HashSet<>();

        try (BufferedReader buffer = new BufferedReader(new FileReader(filepath))) {
            String line = buffer.readLine();

            while (line != null) {
                blocked.add(Integer.decode(line));
                line = buffer.readLine();
            }
        }

        return blocked;
    }

    private Set<String> readBlockedStrings(String filepath) throws IOException {
        Set<String> blocked = new HashSet<>();

        try (BufferedReader buffer = new BufferedReader(new FileReader(filepath))) {
            String line = buffer.readLine();

            while (line != null) {
                blocked.add(line);
                line = buffer.readLine();
            }
        }

        return blocked;
    }

    public Set<Integer> getBlockedChars() {
        return blockedChars;
    }

    public Set<String> getBlockedStrings() {
        return blockedStrings;
    }

    public Set<String> getBlockedWords() {
        return blockedWords;
    }

    public boolean contentFilterEnabled() {
        return enableContentFilter;
    }

    public String getReplacementString() {
        return replacementString;
    }

    public boolean verbose() {
        return this.verbose;
    }
}
