package com.cstav.genshinstrument;

import com.cstav.genshinstrument.criteria.ModCriteria;
import com.cstav.genshinstrument.item.GIItems;
import com.cstav.genshinstrument.networking.GIPacketHandler;
import com.cstav.genshinstrument.networking.buttonidentifier.DrumNoteIdentifier;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteButtonIdentifiers;
import com.cstav.genshinstrument.networking.buttonidentifier.NoteGridButtonIdentifier;
import com.cstav.genshinstrument.sound.GISounds;
import net.minecraft.resources.ResourceLocation;
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
    //test
    public static ResourceLocation loc(final String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }


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
