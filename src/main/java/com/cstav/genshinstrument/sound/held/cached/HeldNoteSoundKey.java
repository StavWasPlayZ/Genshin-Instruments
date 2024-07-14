package com.cstav.genshinstrument.sound.held.cached;

import net.minecraft.resources.ResourceLocation;

public record HeldNoteSoundKey(ResourceLocation baseSoundLocation, int noteIndex) {
    @Override
    public boolean equals(Object o) {
        return (o instanceof HeldNoteSoundKey other)
            && other.baseSoundLocation.equals(baseSoundLocation)
            && (other.noteIndex == noteIndex);
    }

    @Override
    public int hashCode() {
        return baseSoundLocation.hashCode() + noteIndex;
    }
}
