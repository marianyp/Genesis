package dev.mariany.genesis.age;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.advancement.AdvancementHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class AgeShareManager extends PersistentState {
    private static final PersistentStateType<AgeShareManager> STATE_TYPE = new PersistentStateType<>(
            "ages",
            context -> new AgeShareManager(),
            context -> AgeShareManager.Packed.CODEC.xmap(AgeShareManager::new, AgeShareManager::pack),
            null
    );

    private final Set<Identifier> globalAges = new HashSet<>();
    private final Map<String, Set<Identifier>> teamAges = new HashMap<>();

    public AgeShareManager() {
        this.markDirty();
    }

    public AgeShareManager(Packed packed) {
        this.unpack(packed);
    }

    public static AgeShareManager getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getOverworld().getPersistentStateManager();

        AgeShareManager state = persistentStateManager.getOrCreate(STATE_TYPE);

        state.markDirty();

        return state;
    }

    public int clear(boolean global) {
        int cleared;

        if (global) {
            cleared = this.globalAges.size();
            this.globalAges.clear();
        } else {
            cleared = this.teamAges.values().stream()
                    .mapToInt(Set::size)
                    .sum();

            this.teamAges.clear();
        }

        this.markDirty();

        return cleared;
    }

    public void applySharedAges(ServerPlayerEntity serverPlayer) {
        AgeManager ageManager = AgeManager.getInstance();
        Team team = serverPlayer.getScoreboardTeam();
        Set<Identifier> agesToApply = new HashSet<>(this.globalAges);

        if (team != null) {
            Set<Identifier> teamAges = this.teamAges.getOrDefault(team.getName(), new HashSet<>());
            agesToApply.addAll(teamAges);
        }

        for (Identifier ageId : agesToApply) {
            ageManager.get(ageId).ifPresent(ageEntry -> shareWithPlayer(serverPlayer, ageEntry));
        }
    }

    private static List<ServerPlayerEntity> getSharingPlayers(MinecraftServer server) {
        return getSharingPlayers(server, null);
    }

    private static List<ServerPlayerEntity> getSharingPlayers(MinecraftServer server, @Nullable Team team) {
        if (team != null) {
            PlayerManager playerManager = server.getPlayerManager();
            List<ServerPlayerEntity> players = playerManager.getPlayerList();

            return players.stream()
                    .filter(serverPlayer -> serverPlayer.getScoreboardTeam() == team)
                    .toList();
        }

        return server.getPlayerManager().getPlayerList();
    }

    public void shareWithServer(MinecraftServer server, AgeEntry ageEntry) {
        this.globalAges.add(ageEntry.getId());
        this.markDirty();

        List<ServerPlayerEntity> sharingPlayers = getSharingPlayers(server);

        Genesis.LOGGER.info("Preparing to share ages with {} players", sharingPlayers.size());

        shareWithPlayers(sharingPlayers, ageEntry);
    }

    public void shareWithTeam(ServerPlayerEntity player, AgeEntry ageEntry) {
        Team team = player.getScoreboardTeam();

        if (team != null) {
            String teamName = team.getName();
            Set<Identifier> ages = teamAges.getOrDefault(teamName, new HashSet<>());
            ages.add(ageEntry.getId());

            teamAges.put(teamName, ages);
            this.markDirty();

            List<ServerPlayerEntity> sharingPlayers = getSharingPlayers(player.getServer(), team);

            Genesis.LOGGER.info("Preparing to shared ages with {} players on team {}", sharingPlayers.size(), teamName);

            shareWithPlayers(sharingPlayers, ageEntry);
        }
    }

    public static void shareWithPlayer(ServerPlayerEntity player, AgeEntry ageEntry) {
        shareWithPlayers(List.of(player), ageEntry);
    }

    private static void shareWithPlayers(Collection<ServerPlayerEntity> players, AgeEntry ageEntry) {
        Optional<Identifier> parentAgeId = ageEntry.getAge().parent();

        if (parentAgeId.isPresent()) {
            AgeManager ageManager = AgeManager.getInstance();
            Optional<AgeEntry> optionalParentAgeEntry = ageManager.get(parentAgeId.get());
            optionalParentAgeEntry.ifPresent(entry -> shareWithPlayers(players, entry));
        }

        for (ServerPlayerEntity player : players) {
            AdvancementHelper.giveAdvancement(player, ageEntry.getAdvancementEntry());
        }
    }

    public void unpack(AgeShareManager.Packed packed) {
        this.globalAges.addAll(packed.globalAges());
        packed.teamAges().forEach((team, ages) -> this.teamAges.put(team, new HashSet<>(ages)));
    }

    public AgeShareManager.Packed pack() {
        List<Identifier> globalAges = this.globalAges.stream().toList();

        Map<String, List<Identifier>> teamAges = this.teamAges.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue())
                ));


        return new AgeShareManager.Packed(
                globalAges,
                teamAges
        );
    }

    public record Packed(
            List<Identifier> globalAges,
            Map<String, List<Identifier>> teamAges
    ) {
        public static final Codec<AgeShareManager.Packed> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                Identifier.CODEC.listOf()
                                        .optionalFieldOf("GlobalAges", List.of())
                                        .forGetter(AgeShareManager.Packed::globalAges),
                                Codec.unboundedMap(Codec.STRING, Identifier.CODEC.listOf())
                                        .optionalFieldOf("TeamAges", Map.of())
                                        .forGetter(AgeShareManager.Packed::teamAges)
                        )
                        .apply(instance, AgeShareManager.Packed::new)
        );
    }
}
