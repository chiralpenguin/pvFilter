package com.purityvanilla.pvfilter;

import com.purityvanilla.pvfilter.core.MatchMode;
import com.purityvanilla.pvfilter.core.TrackedString;
import com.purityvanilla.pvfilter.stages.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PipelineTextFilter implements TextFilter {
    private final MiniMessage mm;
    private final List<FilterStage> pipeline;

    private final Pattern blockedCharPattern;
    private final Set<String> blockedStrings;
    private final Pattern blockedStringsPattern;
    private final Pattern blockedWordsPattern;
    private final String replacementString;

    public PipelineTextFilter(Set<Integer> blockedChars,
                              Set<String> blockedStrings,
                              Set<String> blockedWords,
                              String replacementString) {
        this.mm = buildStrictMiniMessage();
        this.pipeline = buildFilterPipeline();

        this.blockedCharPattern = buildBlockedCharPattern(blockedChars);
        this.blockedStrings = blockedStrings;
        this.blockedStringsPattern = buildPattern(normalizeTerms(blockedStrings));
        this.blockedWordsPattern = buildPattern(normalizeTerms(blockedWords));
        this.replacementString = replacementString;
    }

    public PipelineTextFilter(Config config) {
        this(
                config.getBlockedChars(),
                config.getBlockedStrings(),
                config.getBlockedWords(),
                config.getReplacementString()
        );
    }

    private MiniMessage buildStrictMiniMessage() {
        return MiniMessage.builder()
                .strict(true)
                .tags(TagResolver.builder()
                        .resolvers(
                                StandardTags.color(),
                                StandardTags.decorations(),
                                StandardTags.reset()
                        ).build()
                ).build();
    }

    private List<FilterStage> buildFilterPipeline() {
        return List.of(
                new LowercaseStage(),
                new LeetspeakStage(),
                new HomoglyphStage(),
                new IgnoreNonAlphaStage(),
                new CollapseRepeatStage()
        );
    }

    private Pattern buildBlockedCharPattern(Set<Integer> blockedChars) {
        StringBuilder patternBuilder = new StringBuilder("[");
        for (int codePoint : blockedChars) {
            patternBuilder.append("\\x{").append(Integer.toHexString(codePoint)).append("}");
        }
        patternBuilder.append("]");

        return Pattern.compile(patternBuilder.toString());
    }

    private Set<String> normalizeTerms(Set<String> terms) {
        Set<String> normalized = new HashSet<>();
        for (String raw : terms) {
            TrackedString ts = TrackedString.from(raw);
            for (FilterStage stage : pipeline) {
                ts = stage.apply(ts);
            }
            normalized.add(ts.getMatchable());
        }
        return normalized;
    }

    private Pattern buildPattern(Set<String> normalisedTerms) {
        if (normalisedTerms.isEmpty()) {
            return Pattern.compile("(?!)");
        }

        List<String> sorted = new ArrayList<>(normalisedTerms);
        sorted.sort(Comparator.comparingInt(String::length).reversed());

        StringBuilder patternBuilder = new StringBuilder("(");
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0) patternBuilder.append("|");
            patternBuilder.append(Pattern.quote(sorted.get(i)));
        }
        patternBuilder.append(")");

        return Pattern.compile(patternBuilder.toString());
    }

    private String removeBannedCharacters(String text) {
        return blockedCharPattern.matcher(text).replaceAll("");
    }

    private List<int[]> findOriginalRanges(TrackedString ts, String original,
                                           Pattern pattern, MatchMode mode) {
        List<int[]> ranges = new ArrayList<>();
        Matcher matcher = pattern.matcher(ts.getMatchable());

        while (matcher.find()) {
            int origStart = ts.getOrigStart(ts.getSpanIndex(matcher.start()));
            int origEnd = ts.getOrigEnd(ts.getSpanIndex(matcher.end() - 1));

            if (mode == MatchMode.WORD) {
                if (!isWordBoundary(original, origStart, true)
                        || !isWordBoundary(original, origEnd, false)) {
                    continue;
                }
            }

            ranges.add(new int[]{ origStart, origEnd });
        }
        return ranges;
    }

    private boolean isWordBoundary(String original, int index, boolean checkBefore) {
        if (checkBefore) {
            return index <= 0 || !Character.isLetter(original.charAt(index - 1));
        } else {
            return index >= original.length() - 1 || !Character.isLetter(original.charAt(index + 1));
        }
    }

    private String reconstruct(String original, List<int[]> ranges) {
        StringBuilder result = new StringBuilder();
        int cursor = 0;

        for (int[] range : ranges) {
            if (range[0] < cursor) continue;
            result.append(original, cursor, range[0]);
            result.append(replacementString);
            cursor = range[1] + 1;
        }

        result.append(original, cursor, original.length());
        return result.toString().strip();
    }

    public String filterText(String text) {
        String symbolRemoved = removeBannedCharacters(text);
        String normalized = Normalizer.normalize(symbolRemoved, Normalizer.Form.NFKC);

        TrackedString ts = TrackedString.from(normalized);
        for (FilterStage stage : pipeline) {
            ts = stage.apply(ts);
        }

        List<int[]> allHits = new ArrayList<>();
        allHits.addAll(findOriginalRanges(ts, normalized, blockedStringsPattern, MatchMode.SUBSTRING));
        allHits.addAll(findOriginalRanges(ts, normalized, blockedWordsPattern, MatchMode.WORD));

        if (allHits.isEmpty()) {
            return symbolRemoved;
        }

        allHits.sort(Comparator.comparingInt(a -> a[0]));
        return reconstruct(normalized, allHits);
    }

    public Component filterComponent(Component component) {
        // TranslatableComponents can't be handled by MiniMessage and are only created server-side, so can be ignored
        if (component instanceof TranslatableComponent) return component;

        String text = mm.serialize(component);
        String filtered = filterText(text);
        return mm.deserialize(filtered);
    }
}
