package com.purityvanilla.pvfilter.stages;

import com.purityvanilla.pvfilter.core.CharSpan;
import com.purityvanilla.pvfilter.core.TrackedString;

import java.util.ArrayList;
import java.util.List;

public class IgnoreNonAlphaStage implements FilterStage {

    @Override
    public TrackedString apply(TrackedString input) {
        List<CharSpan> result = new ArrayList<>();
        for (CharSpan span : input.chars()) {
            // ! is excluded from Leetspeak stage to not break tag negation, so must be considered a letter
            boolean ignored = !Character.isLetter(span.transformed()) && span.transformed() != '!';
            result.add(span.withIgnored(ignored));
        }
        return new TrackedString(result);
    }
}
