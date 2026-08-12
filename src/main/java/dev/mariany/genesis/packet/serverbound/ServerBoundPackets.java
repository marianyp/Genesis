package dev.mariany.genesis.packet.serverbound;

import dev.mariany.genesis.recipe.AssemblyRecipe;
import dev.mariany.genesis.screen.AssemblyScreenHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class ServerBoundPackets {
    private ServerBoundPackets() {
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(QuickCraftAssemblyPayload.ID, ServerBoundPackets::quickCraft);
    }

    private static void quickCraft(QuickCraftAssemblyPayload payload, ServerPlayNetworking.Context context) {
        quickCraft(payload, context.player());
    }

    private static void quickCraft(QuickCraftAssemblyPayload payload, ServerPlayer player) {
        if (!(player.containerMenu instanceof AssemblyScreenHandler menu)) {
            return;
        }

        player.level()
              .recipeAccess()
              .byKey(payload.recipeKey())
              .filter(recipe -> recipe.value() instanceof AssemblyRecipe)
              .map(ServerBoundPackets::asAssemblyRecipe)
              .ifPresent(menu::quickCraft);
    }

    private static RecipeHolder<AssemblyRecipe> asAssemblyRecipe(RecipeHolder<?> recipe) {
        return new RecipeHolder<>(recipe.id(), (AssemblyRecipe) recipe.value());
    }
}
