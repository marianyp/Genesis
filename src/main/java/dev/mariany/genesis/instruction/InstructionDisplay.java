package dev.mariany.genesis.instruction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record InstructionDisplay(ItemStack icon, Text title, Text description) {
    public static final Codec<InstructionDisplay> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ItemStack.VALIDATED_CODEC.fieldOf("icon").forGetter(InstructionDisplay::icon),
                            TextCodecs.CODEC.fieldOf("title").forGetter(InstructionDisplay::title),
                            TextCodecs.CODEC.optionalFieldOf("description", Text.empty())
                                    .forGetter(InstructionDisplay::description)
                    )
                    .apply(instance, InstructionDisplay::new)
    );
}
