package com.acikek.purpeille.warpath.component;

import net.minecraft.network.PacketByteBuf;

public interface Writer {

    void write(PacketByteBuf buf);
}
