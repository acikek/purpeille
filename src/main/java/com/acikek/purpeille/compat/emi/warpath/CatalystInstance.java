package com.acikek.purpeille.compat.emi.warpath;

import com.acikek.purpeille.warpath.component.Aspect;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record CatalystInstance<T extends Aspect>(T component, Item catalyst) {

    public static <T extends Aspect> List<CatalystInstance<T>> getInstances(List<Item> catalysts, Map<Identifier, T> registry) {
        List<CatalystInstance<T>> result = new ArrayList<>();
        for (T component : registry.values()) {
            for (Item catalyst : catalysts) {
                if (component.catalyst.test(catalyst.getDefaultStack())) {
                    result.add(new CatalystInstance<>(component, catalyst));
                }
            }
        }
        return result;
    }

    public static <T extends Aspect> void addItems(List<CatalystInstance<T>> instances, List<Item> items) {
        for (CatalystInstance<T> instance : instances) {
            items.add(instance.catalyst);
        }
    }
}
