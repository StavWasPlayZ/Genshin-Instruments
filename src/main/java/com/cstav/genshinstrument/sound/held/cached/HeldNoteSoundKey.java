package com.cstav.genshinstrument.sound.held.cached;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record HeldNoteSoundKey(Player initiator, ResourceLocation baseSoundLocation, int noteIndex) {
    @Override
    public boolean equals(Object o) {
        return (o instanceof HeldNoteSoundKey other)
            && other.initiator.equals(initiator)
            && other.baseSoundLocation.equals(baseSoundLocation)
            && (other.noteIndex == noteIndex);
    }

    @Override
    public int hashCode() {
        return initiator.hashCode() + baseSoundLocation.hashCode() + noteIndex;
    }
}
