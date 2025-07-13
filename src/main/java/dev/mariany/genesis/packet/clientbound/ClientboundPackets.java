package dev.mariany.genesis.packet.clientbound;

import dev.mariany.genesis.client.age.ClientAgeManager;
import dev.mariany.genesis.mixin.accessor.ToastManagerAccessor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.Text;

public class ClientboundPackets {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(UpdateAgeItemUnlocksPayload.ID, (payload, context) -> {
            ClientAgeManager.getInstance().updateItemUnlocks(payload.items());
        });

        ClientPlayNetworking.registerGlobalReceiver(NotifyAgeLockedPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            ToastManager toastManager = client.getToastManager();

            if (
                    ((ToastManagerAccessor) toastManager).genesis$visibleEntries()
                            .stream()
                            .noneMatch(entry -> entry.getInstance() instanceof TutorialToast)
            ) {
                toastManager.add(new TutorialToast(
                        client.textRenderer,
                        TutorialToast.Type.RIGHT_CLICK,
                        Text.translatable(
                                "tutorial.ageLocked",
                                Text.translatable(payload.ageTranslation()),
                                Text.translatable("age.genesis.age"),
                                Text.translatable(payload.itemTranslation())
                        ),
                        null,
                        true,
                        3000
                ));
            }
        });
    }
}
