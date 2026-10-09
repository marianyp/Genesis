package dev.mariany.genesis.attachment;

import com.mojang.serialization.Codec;
import dev.mariany.genesis.Genesis;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;

public class GenesisAttachmentTypes {
    public static final AttachmentType<Integer> AWAKE_TICKS = AttachmentRegistry.create(
            Genesis.id("awake_ticks"),
            builder -> builder.initializer(() -> 0).persistent(Codec.INT).copyOnDeath()
    );

    public static final AttachmentType<BlockState> BED = AttachmentRegistry.create(
            Genesis.id("bed"),
            builder -> builder.persistent(BlockState.CODEC)
    );

    public static final AttachmentType<List<UUID>> CELEBRATION_PARTICIPANTS = AttachmentRegistry.create(
            Genesis.id("celebration_participants"),
            builder -> builder.initializer(List::of).persistent(Codec.list(UUIDUtil.CODEC))
    );

    public static final AttachmentType<List<MobEffectInstance>> POORLY_RESTED_EFFECTS = AttachmentRegistry.create(
            Genesis.id("poorly_rested_effects"),
            builder -> builder.persistent(MobEffectInstance.CODEC.listOf())
    );

    public static void bootstrap() {
        Genesis.bootstrapLog("Attachment Types");
    }
}
