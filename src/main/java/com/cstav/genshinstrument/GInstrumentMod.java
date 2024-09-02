package com.cstav.genshinstrument;

import com.cstav.genshinstrument.client.ModArmPose;
import com.cstav.genshinstrument.criteria.ModCriteria;
import com.cstav.genshinstrument.item.ModItems;
import com.cstav.genshinstrument.item.clientExtensions.ModItemPredicates;
import com.cstav.genshinstrument.networking.ModPacketHandler;
import com.cstav.genshinstrument.sound.ModSounds;
import net.minecraftforge.common.MinecraftForge;
import com.cstav.genshinstrument.item.GIItems;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.buttonidentifier.DrumNoteIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifiers;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteGridButtonIdentifier;
import com.cstav.genshinstrument.sound.GISounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class of the Genshin Instruments mod
 * 
 * @author StavWasPlayZ
 */
@Mod(GInstrumentMod.MODID)
public class GInstrumentMod
{
    public static final String MODID = "genshinstrument";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);


    public GInstrumentMod()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        GIPacketHandler.registerPackets();
        NoteButtonIdentifiers.register(
            NoteGridButtonIdentifier.class,
            DrumNoteIdentifier.class
        );

        ModCriteria.register(bus);

        GIItems.register(bus);
        // ModBlocks.register(bus);
        // ModBlockEntities.register(bus);

        GISounds.register(bus);
        GICreativeModeTabs.regsiter(bus);
    }
}
