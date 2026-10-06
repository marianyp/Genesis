package dev.mariany.genesis.world.level.saveddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mariany.genesis.Genesis;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CelebrationState extends SavedData {
    private static final Codec<CelebrationState> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.list(UUIDUtil.CODEC)
                         .optionalFieldOf("participants", List.of())
                         .forGetter(CelebrationState::getParticipants),
                    Codec.intRange(0, Integer.MAX_VALUE)
                         .optionalFieldOf("tick_progress", 0)
                         .forGetter(CelebrationState::getTickProgress),
                    Codec.list(UUIDUtil.CODEC)
                         .<Set<UUID>>xmap(LinkedHashSet::new, List::copyOf)
                         .optionalFieldOf("ender_dragons", Set.of())
                         .forGetter(state -> state.enderDragonUuids)
            ).apply(instance, CelebrationState::new)
    );

    private static final SavedDataType<CelebrationState> TYPE = new SavedDataType<>(
            Genesis.id("celebration"),
            CelebrationState::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    private List<UUID> participants;
    private int tickProgress;
    private final Set<UUID> enderDragonUuids;

    public CelebrationState() {
        this(List.of(), 0, Set.of());
    }

    private CelebrationState(List<UUID> participants, int tickProgress, Set<UUID> enderDragonUuids) {
        this.participants = List.copyOf(participants);
        this.tickProgress = tickProgress;
        this.enderDragonUuids = new LinkedHashSet<>(enderDragonUuids);
    }

    public static CelebrationState get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public List<UUID> getParticipants() {
        return this.participants;
    }

    public int getTickProgress() {
        return this.tickProgress;
    }

    public boolean isTracking(EnderDragon enderDragon) {
        return this.enderDragonUuids.contains(enderDragon.getUUID());
    }

    public void restart(List<UUID> participants, EnderDragon enderDragon) {
        LinkedHashSet<UUID> combinedParticipants = new LinkedHashSet<>(this.participants);

        combinedParticipants.addAll(participants);

        this.participants = List.copyOf(combinedParticipants);
        this.tickProgress = 0;
        this.enderDragonUuids.add(enderDragon.getUUID());
        this.setDirty();
    }

    public void stopTracking(EnderDragon enderDragon) {
        if (this.enderDragonUuids.remove(enderDragon.getUUID())) {
            this.setDirty();
        }
    }

    public void advance() {
        this.tickProgress++;
        this.setDirty();
    }

    public void stop() {
        this.participants = List.of();
        this.tickProgress = 0;
        this.setDirty();
    }
}
