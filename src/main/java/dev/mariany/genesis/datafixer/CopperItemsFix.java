package dev.mariany.genesis.datafixer;

import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.fix.ItemNameFix;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CopperItemsFix extends ItemNameFix {
    public CopperItemsFix(Schema outputSchema, String name) {
        super(outputSchema, name);
    }

    @Override
    protected String rename(String input) {
        Map<String, String> itemMap = this.getItems().stream().collect(Collectors.toMap(
                item -> "genesis:" + item,
                item -> "minecraft:" + item
        ));

        return itemMap.getOrDefault(IdentifierNormalizingSchema.normalize(input), input);
    }

    protected List<String> getItems() {
        return List.of(
                "copper_nugget",
                "copper_sword",
                "copper_shovel",
                "copper_pickaxe",
                "copper_axe",
                "copper_hoe",
                "copper_helmet",
                "copper_chestplate",
                "copper_leggings",
                "copper_boots"
        );
    }
}
