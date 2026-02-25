package com.purityvanilla.pvfilter.stages;

import com.purityvanilla.pvfilter.core.CharSpan;
import com.purityvanilla.pvfilter.core.CharacterMappings;
import com.purityvanilla.pvfilter.core.TrackedString;

import java.util.ArrayList;
import java.util.List;

public class LeetspeakStage implements FilterStage {

    @Override
    public TrackedString apply(TrackedString input) {
        List<CharSpan> result = new ArrayList<>(input.size());

        for (CharSpan span : input.chars()) {
            if (span.ignored()) {
                result.add(span);
                continue;
            }

            char resolved = CharacterMappings.resolveLeet(span.transformed());
            result.add(span.withChar(resolved));
        }

        return new TrackedString(result);
    }
}
