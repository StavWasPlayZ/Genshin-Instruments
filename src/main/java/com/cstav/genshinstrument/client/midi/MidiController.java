package com.cstav.genshinstrument.client.midi;

import com.cstav.genshinstrument.client.config.ModClientConfigs;
import com.cstav.genshinstrument.event.MidiEvent;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import org.slf4j.Logger;

import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;
import java.util.Iterator;
import java.util.LinkedHashMap;

@OnlyIn(Dist.CLIENT)
public abstract class MidiController {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final LinkedHashMap<MidiDevice.Info, MidiDevice> DEVICES = new LinkedHashMap<>();

    private static MidiDevice currDevice;
    private static Transmitter deviceTransmitter;
    private static Info info;
    private static boolean isTransmitting = false;

    public static void reloadDevices() {
        LOGGER.info("Reloading MIDI devices...");
        DEVICES.clear();

        final MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

        for (final Info info : infos) {
            final MidiDevice device;

            try {
                device = MidiSystem.getMidiDevice(info);
            } catch (Exception e) {
                LOGGER.error("Unexpected error occurred while trying to obtain MIDI device " + info.getName().strip(), e);
                continue;
            }

            if (device.getMaxTransmitters() == 0) {
                LOGGER.warn("MIDI device {} cannot transmit MIDI; omitting!", info.getName().strip());
                continue;
            }

            LOGGER.info("Found transmittable MIDI device {}", info.getName().strip());
            DEVICES.put(info, device);
        }
    }

    /**
     * Reloads the list of MIDI devices only if there are none
     * @return Whether there are still no devices available
     * @see MidiController#reloadDevices
     */
    public static boolean reloadIfEmpty() {
        if (DEVICES.isEmpty())
            reloadDevices();

        return DEVICES.isEmpty();
    }


    public static void loadDevice(final int infoIndex) {
        if (reloadIfEmpty()) {
            LOGGER.warn("Attempted to load MIDI device #"+infoIndex+", but there are no devices available!");
            return;
        }
            
        unloadDevice();

        info = getInfoFromIndex(infoIndex);
        currDevice = DEVICES.get(info);
    }
    public static void unloadDevice() {
        if (deviceTransmitter != null)
            deviceTransmitter.close();
        if (currDevice != null)
            currDevice.close();

        isTransmitting = false;
    }

    public static void loadByConfigs() {
        if (!ModClientConfigs.MIDI_ENABLED.get()) {
            unloadDevice();
            return;
        }

        final int infoIndex = ModClientConfigs.MIDI_DEVICE_INDEX.get();
        if (infoIndex == -1)
            return;


        MidiController.reloadIfEmpty();
        if (infoIndex > (MidiController.DEVICES.size() - 1)) {
            LogUtils.getLogger().warn("MIDI device out of range; setting device to none");
            ModClientConfigs.MIDI_DEVICE_INDEX.set(-1);
            return;
        }

        if (!MidiController.isLoaded(infoIndex)) {
            MidiController.loadDevice(infoIndex);
            MidiController.openForListen();
        }
    }


    public static MidiDevice getCurrDevice() {
        return currDevice;
    }
    public static Transmitter getDeviceTransmitter() {
        return deviceTransmitter;
    }

    public static boolean isLoaded(final int infoIndex) {
        return (info != null) && info.equals(getInfoFromIndex(infoIndex));
    }


    public static void openForListen() {
        if (isTransmitting || (currDevice == null))
            return;
            
        try {
            currDevice.open();
            deviceTransmitter = currDevice.getTransmitter();

            deviceTransmitter.setReceiver(new Receiver() {

                @Override
                public void send(MidiMessage message, long timeStamp) {
                    // We only want this to run on the render thread, not the MIDI one
                    LogicalSidedProvider.WORKQUEUE.get(LogicalSide.CLIENT).executeBlocking(() -> {
                        try {
                            MinecraftForge.EVENT_BUS.post(new MidiEvent(message, timeStamp));
                        } catch (Exception ignored) {}
                    });
                }

                @Override
                public void close() {
                    // dunno
                }
                
            });

            isTransmitting = true;
        } catch (Exception e) {
            LOGGER.error("Error occurred while opening MIDI device for listen!\nDevice: "+infoAsString(info), e);
        }

    }

    public static boolean isTransmitting() {
        return isTransmitting;
    }



    public static int getInfoSerial(final Info info) {
        int i = 0;
        
        for (final Info _info : DEVICES.keySet()) {
            if (info.equals(_info))
                return i;

            i++;
        }

        LOGGER.error("Failed to retrieve info from devices map!: "+infoAsString(info), new RuntimeException());
        return -1;
    }

    public static Info getInfoFromIndex(final int index) {
        //TODO: Validate with hasNext; return null if not.
        // Reset the MIDI devices upon null (not here).

        final Iterator<Info> infoIterator = DEVICES.keySet().iterator();

        for (int i = 0; i < index; i++)
            infoIterator.next();

        return infoIterator.next();
    }

    public static String infoAsString(final Info info) {
        return info.getName().strip() +" - "+ info.getDescription() + " ("+info.getVendor()+")";
    }
    

}
