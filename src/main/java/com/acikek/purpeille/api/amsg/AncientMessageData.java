package com.acikek.purpeille.api.amsg;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AncientMessageData {

    public List<Text> lines;
    public SoundEvent soundEvent;
    public Integer ticks;

    public boolean acknowledged;

    public AncientMessageData(List<Text> lines, SoundEvent soundEvent, Integer ticks) {
        this.lines = lines;
        this.soundEvent = soundEvent;
        this.ticks = ticks;
    }

    public AncientMessageData(List<Text> lines) {
        this(lines, null, null);
    }

    public int getDefaultTicks() {
        return lines.stream()
                .map(Text::getString)
                .map(String::length)
                .reduce(Integer::sum)
                .orElse(0);
    }

    public int getTicks() {
        return ticks != null
                ? ticks
                : 80 + getDefaultTicks() * 4;
    }

    public static AncientMessageData read(PacketByteBuf buf) {
        List<Text> lines = buf.readCollection(i -> new ArrayList<>(), PacketByteBuf::readText);
        SoundEvent soundEvent = buf.readOptional((byteBuf) -> byteBuf.readRegistryValue(Registry.SOUND_EVENT)).orElse(null);
        Integer ticks = buf.readOptional(PacketByteBuf::readInt).orElse(null);
        return new AncientMessageData(lines, soundEvent, ticks);
    }

    public void write(PacketByteBuf buf) {
        buf.writeCollection(lines, PacketByteBuf::writeText);
        buf.writeOptional(Optional.ofNullable(soundEvent), (byteBuf, event) -> byteBuf.writeRegistryValue(Registry.SOUND_EVENT, event));
        buf.writeOptional(Optional.ofNullable(ticks), PacketByteBuf::writeInt);
    }
}
