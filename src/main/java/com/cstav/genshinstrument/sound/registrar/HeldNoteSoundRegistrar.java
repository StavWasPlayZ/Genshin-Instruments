package com.cstav.genshinstrument.sound.registrar;

import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import com.cstav.genshinstrument.sound.registrar.impl.AbstractNoteSoundRegistrar;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.function.Function;

public class HeldNoteSoundRegistrar extends AbstractNoteSoundRegistrar<HeldNoteSound, HeldNoteSoundRegistrar> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final HashMap<ResourceLocation, HeldNoteSound[]> SOUNDS_REGISTRY = new HashMap<>();
    public static HeldNoteSound[] getSounds(final ResourceLocation baseSoundName) {
        return SOUNDS_REGISTRY.get(baseSoundName);
    }


    protected NoteSound[] attack, hold;
    @Nullable protected NoteSound[] release = null;

    protected float holdDelay = 0, chainedHoldDelay = 0;
    protected float decay = -1, releaseFadeOut = 0, fullHoldFadeoutTime = 0;


    public HeldNoteSoundRegistrar(DeferredRegister<SoundEvent> sounds, ResourceLocation baseSoundLocation) {
        super(sounds, baseSoundLocation);
    }

    @Override
    public HeldNoteSoundRegistrar getThis() {
        return this;
    }


    /**
     * The delay after which the Hold phase should take
     * place after the Attack phase
     */
    public HeldNoteSoundRegistrar holdDelay(float holdDelay) {
        this.holdDelay = holdDelay;
        return getThis();
    }
    /**
     * The delay in seconds after which the next Hold phase should take
     */
    public HeldNoteSoundRegistrar chainedHoldDelay(float chainedHoldDelay) {
        this.chainedHoldDelay = chainedHoldDelay;
        return getThis();
    }

    /**
     * <p>The holding sound will decay for {@code iterations}
     * hold iterations until inaudible.</p>
     * <br/>
     * The exact formula is:
     * <p>{@code 1 / iterations},<p>
     * which gets subtracted from the volume (1.0) every 1 hold iteration, until it hits 0.2.
     */
    public HeldNoteSoundRegistrar decays(float iterations) {
        decay = 1 / iterations;
        return getThis();
    }

    /**
     * The fade out unit per tick when releasing a held note
     */
    public HeldNoteSoundRegistrar releaseFadeOut(float releaseFadeOut) {
        this.releaseFadeOut = releaseFadeOut;
        return getThis();
    }

    /**
     * Defines the length a note must be pressed in order
     * to activate a full fade out (in seconds).
     * Applies in linear time.
     */
    public HeldNoteSoundRegistrar fullHoldFadeoutTime(float fullHoldFadeoutTime) {
        this.fullHoldFadeoutTime = fullHoldFadeoutTime;
        return getThis();
    }


    /**
     * Builds both Attack and Hold phase's note sounds
     */
    public HeldNoteSoundRegistrar buildSoundsForAll(final Function<NoteSoundRegistrar, NoteSound[]> builder) {
        return getThis()
            .attackBuilder(builder)
            .holdBuilder(builder)
            .releaseBuilder(builder);
    }

    public HeldNoteSoundRegistrar attackBuilder(final Function<NoteSoundRegistrar, NoteSound[]> builder) {
        this.attack = applyBuilder(builder, "_attack");
        return getThis();
    }
    public HeldNoteSoundRegistrar holdBuilder(final Function<NoteSoundRegistrar, NoteSound[]> builder) {
        this.hold = applyBuilder(builder, "_hold");
        return getThis();
    }
    public HeldNoteSoundRegistrar releaseBuilder(final Function<NoteSoundRegistrar, NoteSound[]> builder) {
        this.release = applyBuilder(builder, "_release");
        return getThis();
    }

    protected NoteSound[] applyBuilder(Function<NoteSoundRegistrar, NoteSound[]> builder, String pathSuffix) {
        return builder.apply(nsr(baseSoundLocation.withSuffix(pathSuffix)));
    }
    /**
     * Shorthand for {@code new NoteSoundRegistrar(soundRegistrar, instrumentId)}
     */
    private NoteSoundRegistrar nsr(ResourceLocation instrumentId) {
        return new NoteSoundRegistrar(soundRegistrar, instrumentId);
    }


    /**
     * @param holdDuration The duration of the held sound in seconds
     */
    public HeldNoteSound[] register(final float holdDuration) {
        assert !validateLengths() : "Invalid lengths of sounds provided to HeldNoteSoundRegistrar!"
            + "\nAll sounds must be of equal lengths.";

        final HeldNoteSound[] noteSounds = new HeldNoteSound[sounds()];

        for (int i = 0; i < sounds(); i++) {
            noteSounds[i] = new HeldNoteSound(
                baseSoundLocation, i,
                attack[i], hold[i],
                (release == null) ? null : release[i],

                holdDuration,
                holdDelay,
                chainedHoldDelay,
                decay,
                releaseFadeOut,
                fullHoldFadeoutTime
            );
        }

        SOUNDS_REGISTRY.put(baseSoundLocation, noteSounds);

        LOGGER.info("Successfully loaded {} (x2, {} total) held note sounds for {}",
            noteSounds.length, noteSounds.length * 2, baseSoundLocation);

        return noteSounds;
    }

    protected boolean validateLengths() {
        return (attack.length == hold.length)
            && (sounds() != 0);
    }

    /**
     * @return The amount of sounds inserted into this registrar.
     * @implNote In reality, this is the {@link HeldNoteSoundRegistrar#attack} length,
     * but it should all be equal.
     */
    public int sounds() {
        return attack.length;
    }
}
