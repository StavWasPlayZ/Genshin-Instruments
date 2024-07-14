package com.cstav.genshinstrument.sound.held.cached;

import net.minecraft.resources.ResourceLocation;

public record HeldNoteSoundKey(String initiatorId, ResourceLocation baseSoundLocation, int noteIndex) {
    @Override
    public boolean equals(Object o) {
        return (o instanceof HeldNoteSoundKey other)
            && other.initiatorId.equals(initiatorId)
            && other.baseSoundLocation.equals(baseSoundLocation)
            && (other.noteIndex == noteIndex);
    }

    @Override
    public int hashCode() {
        return initiatorId.hashCode() + baseSoundLocation.hashCode() + noteIndex;
    }
}
