package dev.mariany.genesis.age;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record AgeDisplay(ItemStack icon, Text title, Text description) {
    public static final Codec<AgeDisplay> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ItemStack.VALIDATED_CODEC.fieldOf("icon").forGetter(AgeDisplay::icon),
                            TextCodecs.CODEC.fieldOf("title").forGetter(AgeDisplay::title),
                            TextCodecs.CODEC.fieldOf("description").forGetter(AgeDisplay::description)
                    )
                    .apply(instance, AgeDisplay::new)
    );
}
