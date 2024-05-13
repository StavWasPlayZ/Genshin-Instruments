package com.cstav.genshinstrument.sound.held;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.held.IHoldableNoteButton;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.registrar.HeldNoteSoundRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

/**
 * A container for {@link NoteSound}s that act together
 * as a holdable note sound.
 * <p>The attack sound plays once at the beginning, and the hold
 * sound is held and repeated until released.</p>
 * All time units are in seconds.
 */
public record HeldNoteSound(
    ResourceLocation baseSoundLocation, int index,
    NoteSound attack, NoteSound hold, float holdDuration,
    float holdDelay,
    float holdFadeIn, float holdFadeOut,
    float chainedHoldDelay
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
    public void startPlaying(final IHoldableNoteButton noteButton, final Player player) {
        Minecraft.getInstance().getSoundManager().queueTickingSound(new HeldNoteSoundInstance(
            this, Phase.ATTACK, noteButton,
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
