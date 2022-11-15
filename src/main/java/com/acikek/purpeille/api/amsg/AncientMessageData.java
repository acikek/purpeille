package com.acikek.purpeille.api.amsg;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AncientMessageData {

    public List<Text> lines;
    public SoundEvent soundEvent;
    public Integer ticks;
    public Integer color;

    public boolean acknowledged;

    public AncientMessageData(List<Text> lines, SoundEvent soundEvent, Integer ticks, Integer color) {
        this.lines = lines;
        this.soundEvent = soundEvent;
        this.ticks = ticks;
        this.color = color;
    }

    public static class Builder {

        List<Text> lines;
        SoundEvent soundEvent;
        Integer ticks;
        Integer color = 0xFFFFFFFF;

        public Builder lines(List<Text> lines) {
            this.lines = lines;
            return this;
        }

        public Builder soundEvent(SoundEvent soundEvent) {
            this.soundEvent = soundEvent;
            return this;
        }

        public Builder ticks(Integer ticks) {
            this.ticks = ticks;
            return this;
        }

        public Builder color(Integer color) {
            this.color = color;
            return this;
        }

        public AncientMessageData build() {
            Objects.requireNonNull(lines);
            return new AncientMessageData(lines, soundEvent, ticks, color);
        }
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
        return new Builder()
                .lines(buf.readCollection(i -> new ArrayList<>(), PacketByteBuf::readText))
                .soundEvent(buf.readOptional((byteBuf) -> byteBuf.readRegistryValue(Registry.SOUND_EVENT)).orElse(null))
                .ticks(buf.readOptional(PacketByteBuf::readInt).orElse(null))
                .color(buf.readOptional(PacketByteBuf::readInt).orElse(null))
                .build();
    }

    public void write(PacketByteBuf buf) {
        buf.writeCollection(lines, PacketByteBuf::writeText);
        buf.writeOptional(Optional.ofNullable(soundEvent), (byteBuf, event) -> byteBuf.writeRegistryValue(Registry.SOUND_EVENT, event));
        buf.writeOptional(Optional.ofNullable(ticks), PacketByteBuf::writeInt);
        buf.writeOptional(Optional.ofNullable(color), PacketByteBuf::writeInt);
    }
}
