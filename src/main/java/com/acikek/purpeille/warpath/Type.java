package com.acikek.purpeille.warpath;

import com.acikek.purpeille.command.WarpathCommand;
import com.acikek.purpeille.warpath.component.Component;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Map;

public enum Type {

    REVELATION("revelation", "Revelation"),
    ASPECT("aspect", "Aspect");

    public String translationKey;
    public String nbtKey;
    public DynamicCommandExceptionType exception;

    Type(String translationKey, String nbtKey) {
        this.translationKey = translationKey;
        this.nbtKey = nbtKey;
        exception = WarpathCommand.getException("invalid." + translationKey);
    }


    public void addNbt(NbtCompound nbt, Identifier id) {
        nbt.putString(nbtKey, id.toString());
    }

    public <T extends Component> T getFromNbt(NbtCompound nbt, Map<Identifier, T> registry) {
        String id = nbt.getString(nbtKey);
        if (id == null) {
            return null;
        }
        return registry.get(Identifier.tryParse(id));
    }
}
