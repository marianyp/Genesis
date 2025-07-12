package dev.mariany.genesis.event.item;

import dev.mariany.genesis.GenesisConstants;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class ModifyItemComponentsHandler {
    public static void modify(DefaultItemComponentEvents.ModifyContext modifyContext) {
        modifyContext.modify(Items.MUSHROOM_STEW, stew());
        modifyContext.modify(Items.BEETROOT_SOUP, stew());
        modifyContext.modify(Items.RABBIT_STEW, stew());
        modifyContext.modify(Items.SUSPICIOUS_STEW, stew());
    }

    private static Consumer<ComponentMap.Builder> stew() {
        return builder -> builder.add(DataComponentTypes.MAX_STACK_SIZE, GenesisConstants.STEW_STACK_SIZE);
    }
}
