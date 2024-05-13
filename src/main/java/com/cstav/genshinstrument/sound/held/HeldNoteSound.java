package com.cstav.genshinstrument.sound.held;

import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.registrar.HeldNoteSoundRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

public record HeldNoteSound(
    ResourceLocation baseSoundLocation, int index,
    NoteSound attack, NoteSound hold,
    int holdDelay,
    int holdFadeIn, int holdFadeOut,
    int chainedHoldDelay
) {

    public NoteSound getSound(final Phase phase) {
        return switch (phase) {
            case HOLD -> hold;
            case ATTACK -> attack;
        };
    }

    public static NoteSound[] getSounds(final HeldNoteSound[] sounds, final Phase phase) {
        return Arrays.stream(sounds)
            .map((sound) -> sound.getSound(phase))
            .toArray(NoteSound[]::new);
    }


    @OnlyIn(Dist.CLIENT)
    public void startPlaying(final float pitch, final float volume, final Player player) {
        Minecraft.getInstance().getSoundManager().queueTickingSound(new HeldNoteSoundInstance(
            this, Phase.ATTACK, pitch, volume,
            player, player.position().distanceTo(Minecraft.getInstance().player.position())
        ));
    }


    public void writeToNetwork(final FriendlyByteBuf buf) {
        buf.writeResourceLocation(baseSoundLocation);
        buf.writeInt(index);
    }
    public static HeldNoteSound readFromNetwork(final FriendlyByteBuf buf) {
        return HeldNoteSoundRegistrar.getSounds(buf.readResourceLocation())[buf.readInt()];
    }


    public static enum Phase {
        ATTACK, HOLD
    }

}
