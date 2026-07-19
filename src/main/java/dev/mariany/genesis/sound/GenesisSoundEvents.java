package dev.mariany.genesis.sound;

import dev.mariany.genesis.Genesis;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class GenesisSoundEvents {
    public static final Holder<SoundEvent> ITEM_ARMOR_EQUIP_COPPER = registerReference("item.armor.equip_copper");

    public static final SoundEvent FLINTS = register("item.flints");

    public static final SoundEvent ENTITY_BOAR_AMBIENT = register("entity.boar.ambient");
    public static final SoundEvent ENTITY_BOAR_ATTACK = register("entity.boar.attack");
    public static final SoundEvent ENTITY_BOAR_DEATH = register("entity.boar.death");
    public static final SoundEvent ENTITY_BOAR_HURT = register("entity.boar.hurt");
    public static final SoundEvent ENTITY_BOAR_STEP = register("entity.boar.step");

    public static final Holder<SoundEvent> BLOCK_ASSEMBLY_TABLE_USE = registerReference("block.assembly_table.use");

    private static SoundEvent register(String id) {
        return register(Genesis.id(id));
    }

    private static SoundEvent register(Identifier id) {
        return register(id, id);
    }

    private static SoundEvent register(Identifier id, Identifier soundId) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(soundId));
    }

    private static Holder.Reference<SoundEvent> registerReference(String id) {
        return registerReference(Genesis.id(id));
    }

    private static Holder.Reference<SoundEvent> registerReference(Identifier id) {
        return registerReference(id, id);
    }

    private static Holder.Reference<SoundEvent> registerReference(Identifier id, Identifier soundId) {
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(soundId));
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Sound Events");
    }
}
