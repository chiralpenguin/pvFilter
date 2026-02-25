package com.purityvanilla.pvfilter.stages;

import com.purityvanilla.pvfilter.core.TrackedString;

@FunctionalInterface
public interface FilterStage {
    TrackedString apply(TrackedString input);
}
