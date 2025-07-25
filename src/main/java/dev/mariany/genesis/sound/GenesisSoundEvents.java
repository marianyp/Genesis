package dev.mariany.genesis.sound;

import dev.mariany.genesis.Genesis;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class GenesisSoundEvents {
    public static final RegistryEntry<SoundEvent> ITEM_ARMOR_EQUIP_COPPER = registerReference("item.armor.equip_copper");
    public static final SoundEvent FLINTS = register("item.flints");
    public static final SoundEvent UI_TOAST_INSTRUCTIONS_COMPLETE = register("ui.toast.instructions_complete");

    private static SoundEvent register(String id) {
        return register(Genesis.id(id));
    }

    private static SoundEvent register(Identifier id) {
        return register(id, id);
    }

    private static SoundEvent register(Identifier id, Identifier soundId) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }

    private static RegistryEntry.Reference<SoundEvent> registerReference(String id) {
        return registerReference(Genesis.id(id));
    }

    private static RegistryEntry.Reference<SoundEvent> registerReference(Identifier id) {
        return registerReference(id, id);
    }

    private static RegistryEntry.Reference<SoundEvent> registerReference(Identifier id, Identifier soundId) {
        return Registry.registerReference(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }

    public static void bootstrap() {
        Genesis.LOGGER.info("Registering Sound Events for " + Genesis.MOD_ID);
    }
}
