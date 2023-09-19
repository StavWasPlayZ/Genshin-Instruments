package com.cstav.genshinstrument.client.midi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import org.slf4j.Logger;

import com.cstav.genshinstrument.event.MidiEvent;
import com.mojang.logging.LogUtils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;

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

        for (int i = 0; i < infos.length; i++) {
            try {

                final MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
                // Only obtain devices that can transmit
                device.getTransmitter();

                DEVICES.put(infos[i], device);

            } catch (MidiUnavailableException e) {
                LOGGER.warn("MIDI device "+infos[i]+" cannot transmit any MIDI; ommitting!");
            } catch (Exception e) {
                LOGGER.error("Unexpected error occured while trying to obtain MIDI device!", e);
            }
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

    /**
     * @return A list of available MIDI devices by their indexes, and -1 for None
     */
    public static List<Integer> getValuesForOption() {
        final List<Integer> result = new ArrayList<>(DEVICES.size() + 1);
        result.add(-1);

        for (int i = 0; i < DEVICES.size(); i++)
            result.add(i);

        return result;
    }


    public static void loadDevice(final int infoIndex) {
        if (reloadIfEmpty())
            return;
            
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

                    LogicalSidedProvider.WORKQUEUE.get(LogicalSide.CLIENT)
                        .executeBlocking(() -> MinecraftForge.EVENT_BUS.post(new MidiEvent(message, timeStamp)));
                }

                @Override
                public void close() {
                    // dunno
                }
                
            });

            isTransmitting = true;
        } catch (Exception e) {
            LOGGER.error("Error occured while opening MIDI device for listen!\nDevice: "+infoAsString(info), e);
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
        final Iterator<Info> infoIterator = DEVICES.keySet().iterator();

        for (int i = 0; i < index; i++)
            infoIterator.next();

        return infoIterator.next();
    }

    public static String infoAsString(final Info info) {
        return info.getName() +" - "+ info.getDescription() + " ("+info.getVendor()+")";
    }
    

}
