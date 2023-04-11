<img align="right" src=https://github.com/StavWasPlayZ/Genshin-Instruments/tutorial/images/logo.png?raw=true width="100">

# Genshin Instruments

Genshin Instruments is a Forge mod that brings in Genshin Impact's set of instruments into your Minecraft worlds!

For full documentation of gameplay, visit [the curseforge page](https://www.curseforge.com/minecraft/mc-mods/genshin-instruments). Although, if you're here, it means you're somewhat of a geek as myself, so let's get technical, shall we?

## Quick Note

This is my first ever mod and API (and its README) I ever publicly do. So, just expect some quirky stuff here and there..-

I'll try my best to stay on-point, though.


# Creating Your Own Instrument!

Let's say you're a fellow modder who'd like to use this mod as a dependency for making a new instrument. While I heavily doubt anyone would actually do it, I still left a pretty neat API for you to use.
In any case, this section is mostly for me to remember my stuff :P

Anywho, this tutorial will assume that you are a complete beginner to the subject of modding and such in general, as it makes it easier for me to write it that way.  
Of course, you will still need basic Java, Forge (and Minecraft) knowledge.

## Introduction To the System

### Server and Client
The instrument is built on 2 parts:
1. **the client** is responsible for having an **instrument screen**. When the player plays a sound, it will produce a sound for themselves locally, while sending an `InstrumentPacket`s to the server - passing in their `NoteSound` object.
2. **The server** is responsible for handling said `InstrumentPackets`s. It will play all the stuff as described by the `NoteSound` object to each individual client within a range of 16 blocks by sending them a `PlayNotePacket`.  
It is done like so because each client can request to play a different type instance of a note's sound, as described in the instrument's *"Instrument Audio Channel Type"* setting.



### Resources
All resources for any instrument's screen should be kept under `assets\<modid>\textures\gui\instrument\<instrument>`.
Although it is not required, I'd generally recommend it. Not only do I use it, but the instrument screen is basically a GUI element - So, just keep it sorted under that folder.

## Our Goal

We're going to start simple, and assume that all you have in mind is a simple instrument that inherits the 21 notes style - per se, **a piano**.

<sub>_*Quick sidenote, I'd actually love to implement a piano into this mod but I just don't have the sounds for it. If you actually make it that'd be very epic and I'll be in a life's debt to you._</sub>

## Creating an Instrument Screen

Firstly, we'll need to create an **instrument screen**.
As the name may suggest, it is the container for our instrument's GUI.

Such a screen must extend the `AbstractInstrumentScreen` class. Note that it is a **client only** class, as screens do not exist on dedicated servers. We will therefore annotate it as such:

```java
@OnlyIn(Dist.CLIENT)
public class PianoScreen extends AbstractInstrumentScreen {
    //...
}
```

This class will kindly ask you to implement its methods. Let's explore their purposes and how you should handle them:

### getInstrumentResourcesLocation()
This method should return the root directory of this instrument's resources. As discussed earlier, we should put our piano under `textures/gui/instrument/piano`.

### getThemeLoader()
This method should return an instance of the `InstrumentThemeLoader` class.

This class is responsible for handling the text color of the labels, and will automatically assign them according to the styler JSON associated in the directory specified earlier. We will explore its contents later.
Its parameters are:
1. **instrumentStyleLocation** - The location of the instrument's JSON styler
2. **defNoteTheme** - The default note theme
3. **defPressedNoteTheme** - The default note theme for when the note is pressed

For this tutorial, we want our text to appear black-ish, to represent the piano notes. To pass a color, we initiate an instance of the `RGBColor` class - thus having the 2nd parameter:
```java
new RGBColor(20, 20, 20);
```

Generally, we want the pressed theme to be the background color of the note. The default is the RGB code `(255, 249, 239)`. So, this will be our 3rd parameter.

#### **Note:** Loading
The initiator for this class must be called in about the same time as of the loading of the `Mod event bus`. This is so because when you call the initiator, it automatically subscribes the given JSON for any resource load event on the client, and the first one is just about when the game fires.  
This means that if you do not call it at that time, any resource pack that is modifying your piano will not have its effects applied until they press *F3+T* to trigger a resource load event.

The trick I use for it is to make the theme loader a static property of the screen class, and annotate the screen class as an `EventBusSubscriber` for the mod event bus.
This makes the FML load the class early on in search for any methods trying to subscribe to the specified event bus.  
We must also declare the `value` property to be of `CLIENT` for it to not attempt to load on the server.

### getSounds()
This method should return an array of `NoteSound`s. Their length must be equal to 21 - the total notes that can be pressed in such instrument.

Each one will be taken in the order of the note pressed by the player, as specified in the associated `NoteGrid`.

For now, at least, we don't have such array, so let's help ourselves by simply returning `null`.

### open()
Although unspecified by `AbstractInstrumentScreen`, this method is a static (void) method that should open the instrument screen for the client. it is done through:
```java
Minecraft.getInstance().setScreen(new PianoScreen());
```

We do this because we must open the screen via a packet, for us to not make any client class *(cough cough `PianoScreen`)* load on the server. The packet that we will create should make a call to this method.

### To conclude

With all said and done, we end up with this class at hand:

```java
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT)
public class PianoScreen extends AbstractInstrumentScreen {
    @Override
    protected ResourceLocation getInstrumentResourcesLocation() {
        return new ResourceLocation(Main.MODID, "textures/gui/instrument/piano");
    }
    
    private static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(
        new ResourceLocation(Main.MODID, "textures/gui/instrument/piano/instrument_style.json"),
        new RGBColor(154, 228, 212), new RGBColor(255, 249, 239)
    );
    @Override
    protected InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }

    @Override
    public NoteSound[] getSounds() {
        return null;
    }



    public static void open() {
        Minecraft.getInstance().setScreen(new PianoScreen());
    }
    
}
```

## Adding Sounds

Keep in mind that, according do [this very clever Reddit user](https://www.reddit.com/r/Genshin_Impact/comments/zjm340/comment/izvl4bd/?context=3), quote:

> The Genshin lyre is in C major, so transpose everything into 1-4-5 chords and play in C major

## [More to come soon]