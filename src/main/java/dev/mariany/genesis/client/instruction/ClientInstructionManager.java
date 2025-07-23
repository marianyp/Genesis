package dev.mariany.genesis.client.instruction;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.client.toast.HideableToast;
import dev.mariany.genesis.client.toast.InstructionToast;
import dev.mariany.genesis.client.toast.InstructionsCompleteToast;
import dev.mariany.genesis.config.ConfigHandler;
import dev.mariany.genesis.mixin.accessor.ClientAdvancementManagerAccessor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Environment(EnvType.CLIENT)
public class ClientInstructionManager {
    private static final ClientInstructionManager INSTANCE = new ClientInstructionManager();

    private static final Identifier INSTRUCTIONS_COMPLETE_TOAST_ID = Genesis.id("instructions_complete");

    private static final int QUEUE_DELAY_MS = 700;

    private final Set<Identifier> instructionAdvancements = new HashSet<>();
    private final Object2ObjectOpenHashMap<Identifier, HideableToast> toasts = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<Identifier, HideableToast> waitingToasts = new Object2ObjectOpenHashMap<>();

    private boolean complete = true;
    private long queueTargetMs = -1;

    private ClientInstructionManager() {
    }

    public static ClientInstructionManager getInstance() {
        return INSTANCE;
    }

    public void reset() {
        Genesis.LOGGER.info("Resetting Client Instruction Manager");

        this.complete = true;
        this.queueTargetMs = -1;
        this.instructionAdvancements.clear();
        this.waitingToasts.clear();
        this.toasts.forEach((id, toast) -> toast.hide());
        this.toasts.clear();
    }

    public void update() {
        if (!this.waitingToasts.isEmpty()) {
            if (this.queueTargetMs > 0 && this.queueTargetMs <= Util.getMeasuringTimeMs()) {
                this.toasts.putAll(waitingToasts);

                waitingToasts.values()
                        .forEach(toast -> MinecraftClient.getInstance().getToastManager().add(toast));

                this.waitingToasts.clear();
                this.queueTargetMs = -1;
            }
        }
    }

    public void updateInstructionAdvancements(Collection<Identifier> changes) {
        if (ConfigHandler.getConfig().enableInstructions) {
            int oldSize = this.instructionAdvancements.size();
            this.reset();
            this.instructionAdvancements.addAll(changes);

            Genesis.LOGGER.info("Updated instruction advancements. Old Size: {} | New Size: {}",
                    oldSize,
                    this.instructionAdvancements.size()
            );

            this.refreshInstructionToasts();
        }
    }

    public List<PlacedAdvancement> getInstructionAdvancements() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayer = client.player;

        if (clientPlayer != null) {
            ClientAdvancementManager clientAdvancementManager = clientPlayer.networkHandler.getAdvancementHandler();
            AdvancementManager advancementManager = clientAdvancementManager.getManager();
            return advancementManager.getAdvancements().stream().filter(placedAdvancement ->
                    instructionAdvancements.contains(placedAdvancement.getAdvancementEntry().id())).toList();
        }

        return List.of();
    }

    public void removeToast(Identifier id) {
        if (this.toasts.containsKey(id)) {
            this.toasts.get(id).hide();
            this.toasts.remove(id);
        }

        this.waitingToasts.remove(id);
    }

    public void queueToast(Identifier id, HideableToast toast) {
        this.queueTargetMs = Util.getMeasuringTimeMs() + QUEUE_DELAY_MS;
        this.waitingToasts.put(id, toast);
    }

    public void addToast(PlacedAdvancement placedAdvancement) {
        MinecraftClient client = MinecraftClient.getInstance();
        AdvancementEntry advancementEntry = placedAdvancement.getAdvancementEntry();
        Identifier id = advancementEntry.id();
        Advancement advancement = advancementEntry.value();
        Optional<AdvancementDisplay> optionalAdvancementDisplay = advancement.display();

        removeToast(id);

        optionalAdvancementDisplay.ifPresent(advancementDisplay -> {
            @Nullable Text description = advancementDisplay.getDescription();

            if (description instanceof PlainTextContent plainTextContent) {
                if (plainTextContent.string().isEmpty()) {
                    description = null;
                }
            }

            queueToast(id, new InstructionToast(
                    client.textRenderer,
                    advancementDisplay.getIcon(),
                    advancementDisplay.getTitle(),
                    description
            ));
        });

        resetCompleteToast();
    }

    private void resetCompleteToast() {
        this.complete = false;

        HideableToast toast = this.toasts.get(INSTRUCTIONS_COMPLETE_TOAST_ID);

        if (toast != null) {
            toast.hide();
        }

        this.toasts.remove(INSTRUCTIONS_COMPLETE_TOAST_ID);
        this.waitingToasts.remove(INSTRUCTIONS_COMPLETE_TOAST_ID);
    }

    public void refreshInstructionToasts() {
        List<PlacedAdvancement> instructionList = getInstructionAdvancements();

        for (PlacedAdvancement placed : instructionList) {
            Identifier id = placed.getAdvancementEntry().id();
            Optional<AdvancementProgress> optionalAdvancementProgress = getAdvancementProgress(placed);

            if (optionalAdvancementProgress.isPresent()) {
                AdvancementProgress progress = optionalAdvancementProgress.get();

                if (progress.isDone()) {
                    removeToast(id);
                } else if (!this.toasts.containsKey(id)) {
                    if (isParentComplete(placed)) {
                        addToast(placed);
                    }
                }
            }
        }

        if (!instructionList.isEmpty() && (this.toasts.isEmpty() && this.waitingToasts.isEmpty())) {
            if (!this.complete) {
                this.complete = true;
                queueToast(INSTRUCTIONS_COMPLETE_TOAST_ID, new InstructionsCompleteToast());
            }
        }
    }

    private Optional<AdvancementProgress> getAdvancementProgress(PlacedAdvancement placedAdvancement) {
        return getAdvancementProgress(placedAdvancement.getAdvancementEntry());
    }

    private Optional<AdvancementProgress> getAdvancementProgress(AdvancementEntry advancementEntry) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayer = client.player;

        if (clientPlayer != null) {
            ClientAdvancementManager clientAdvancementManager = clientPlayer.networkHandler.getAdvancementHandler();
            Map<AdvancementEntry, AdvancementProgress> advancementProgresses = (
                    (ClientAdvancementManagerAccessor) clientAdvancementManager
            ).genesis$advancementProgresses();

            if (advancementProgresses.containsKey(advancementEntry)) {
                return Optional.of(advancementProgresses.get(advancementEntry));
            }
        }

        return Optional.empty();
    }

    private boolean isParentComplete(PlacedAdvancement advancement) {
        PlacedAdvancement parent = advancement.getParent();

        if (parent == null) {
            return true;
        }

        Optional<AdvancementProgress> optionalParentProgress = getAdvancementProgress(parent);

        if (optionalParentProgress.isPresent()) {
            AdvancementProgress parentProgress = optionalParentProgress.get();
            if (!parentProgress.isDone()) {
                return false;
            }
        }

        return isParentComplete(parent);
    }
}
