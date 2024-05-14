package com.cstav.genshinstrument.sound.registrar;

import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import com.cstav.genshinstrument.sound.registrar.impl.AbstractNoteSoundRegistrar;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
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
    protected float holdDelay = 0, chainedHoldDelay = 0;


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
     * The delay after which the next Hold phase should take
     */
    public HeldNoteSoundRegistrar chainedHoldDelay(float chainedHoldDelay) {
        this.chainedHoldDelay = chainedHoldDelay;
        return getThis();
    }


    public HeldNoteSoundRegistrar buildSoundsForAll(final Function<NoteSoundRegistrar, NoteSound[]> builder) {
        return getThis()
            .attackBuilder(builder)
            .holdBuilder(builder);
    }

    public HeldNoteSoundRegistrar attackBuilder(final Function<NoteSoundRegistrar, NoteSound[]> builder) {
        this.attack = builder.apply(nsr(baseSoundLocation.withSuffix("_attack")));
        return getThis();
    }
    public HeldNoteSoundRegistrar holdBuilder(final Function<NoteSoundRegistrar, NoteSound[]> builder) {
        this.hold = builder.apply(nsr(baseSoundLocation.withSuffix("_hold")));
        return getThis();
    }

    /**
     * Shorthand for {@code new NoteSoundRegistrar(soundRegistrar, instrumentId)}
     */
    private NoteSoundRegistrar nsr(ResourceLocation instrumentId) {
        return new NoteSoundRegistrar(soundRegistrar, instrumentId);
    }


    public HeldNoteSound[] register(final float holdDuration) {
        if (!validateLengths())
            throw new IllegalStateException(
                "Invalid lengths of sounds provided to HeldNoteSoundRegistrar!" +
                    "\nAll sounds must be of equal lengths."
            );

        final HeldNoteSound[] noteSounds = new HeldNoteSound[sounds()];

        for (int i = 0; i < sounds(); i++) {
            noteSounds[i] = new HeldNoteSound(
                baseSoundLocation, i,
                attack[i], hold[i], holdDuration,
                holdDelay,
                chainedHoldDelay
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
