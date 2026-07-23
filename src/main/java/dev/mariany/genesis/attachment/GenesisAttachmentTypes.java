package dev.mariany.genesis.attachment;

import com.mojang.serialization.Codec;
import dev.mariany.genesis.Genesis;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;

public class GenesisAttachmentTypes {
    public static final AttachmentType<Integer> AWAKE_TICKS = AttachmentRegistry.create(
            Genesis.id("awake_ticks"),
            builder -> builder
                    .initializer(() -> 0)
                    .persistent(Codec.INT)
                    .syncWith(ByteBufCodecs.INT, AttachmentSyncPredicate.targetOnly())
                    .copyOnDeath()
    );

    public static void bootstrap() {
        Genesis.bootstrapLog("Attachment Types");
    }
}
