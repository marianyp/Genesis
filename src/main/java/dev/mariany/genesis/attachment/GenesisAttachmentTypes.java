package dev.mariany.genesis.attachment;

import com.mojang.serialization.Codec;
import dev.mariany.genesis.Genesis;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class GenesisAttachmentTypes {
    public static final AttachmentType<Integer> AWAKE_TICKS = AttachmentRegistry.create(
            Genesis.id("awake_ticks"),
            builder -> builder
                    .initializer(() -> 0)
                    .persistent(Codec.INT)
                    .copyOnDeath()
    );

    public static void bootstrap() {
        Genesis.bootstrapLog("Attachment Types");
    }
}
