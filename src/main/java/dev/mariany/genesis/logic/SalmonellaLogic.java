package dev.mariany.genesis.logic;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public class SalmonellaLogic {
    private static final MobEffectInstance SALMONELLA_EFFECT = new MobEffectInstance(
            MobEffects.HUNGER,
            600,
            0
    );

    public static void bootstrap() {
        Genesis.bootstrapLog("Salmonella Logic");
        DefaultItemComponentEvents.MODIFY.register(SalmonellaLogic::modifyItems);
    }

    private static void modifyItems(DefaultItemComponentEvents.ModifyContext context) {
        context.modify(_ -> true, SalmonellaLogic::modifyItem);
    }

    private static void modifyItem(DataComponentMap.Builder builder, HolderLookup.Provider lookupProvider, Item item) {
        HolderLookup.RegistryLookup<Item> items = lookupProvider.lookupOrThrow(Registries.ITEM);
        HolderSet.Named<Item> causesHungerItems = items.getOrThrow(GenesisTags.Items.CAUSES_HUNGER);

        if (!causesHungerItems.contains(item.builtInRegistryHolder())) {
            return;
        }

        if (!builder.contains(DataComponents.CONSUMABLE)) {
            return;
        }

        Consumable previousConsumable = builder.getOrDefault(DataComponents.CONSUMABLE, Consumables.DEFAULT_FOOD);

        Consumable.Builder consumableBuilder = Consumable
                .builder()
                .consumeSeconds(previousConsumable.consumeSeconds())
                .animation(previousConsumable.animation())
                .sound(previousConsumable.sound())
                .hasConsumeParticles(previousConsumable.hasConsumeParticles())
                .onConsume(new ApplyStatusEffectsConsumeEffect(SALMONELLA_EFFECT));

        previousConsumable.onConsumeEffects().forEach(consumableBuilder::onConsume);

        builder.set(DataComponents.CONSUMABLE, consumableBuilder.build());
    }
}
