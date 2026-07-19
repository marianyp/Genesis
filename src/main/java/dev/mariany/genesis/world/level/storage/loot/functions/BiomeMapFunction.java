package dev.mariany.genesis.world.level.storage.loot.functions;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

public class BiomeMapFunction extends LootItemConditionalFunction {
    public static final Holder<MapDecorationType> DEFAULT_DECORATION = MapDecorationTypes.RED_X;
    public static final byte DEFAULT_ZOOM = 2;
    public static final int DEFAULT_SEARCH_RADIUS = 6400;
    public static final int DEFAULT_SAMPLE_RESOLUTION_HORIZONTAL = 32;
    public static final int DEFAULT_SAMPLE_RESOLUTION_VERTICAL = 64;

    public static final MapCodec<BiomeMapFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(
            functionInstance -> commonFields(functionInstance)
                    .and(
                            functionInstance.group(
                                    TagKey.codec(Registries.BIOME)
                                          .fieldOf("destination")
                                          .forGetter(function -> function.destination),
                                    MapDecorationType.CODEC.optionalFieldOf("decoration", DEFAULT_DECORATION)
                                                           .forGetter(function -> function.mapDecoration),
                                    Codec.BYTE.optionalFieldOf("zoom", DEFAULT_ZOOM)
                                              .forGetter(function -> function.zoom),
                                    Codec.INT.optionalFieldOf("search_radius", DEFAULT_SEARCH_RADIUS)
                                             .forGetter(function -> function.searchRadius),
                                    Codec.INT.optionalFieldOf(
                                                 "sample_resolution_horizontal",
                                                 DEFAULT_SAMPLE_RESOLUTION_HORIZONTAL
                                         )
                                             .forGetter(function -> function.sampleResolutionHorizontal),
                                    Codec.INT.optionalFieldOf(
                                                 "sample_resolution_vertical",
                                                 DEFAULT_SAMPLE_RESOLUTION_VERTICAL
                                         )
                                             .forGetter(function -> function.sampleResolutionVertical)
                            )
                    )
                    .apply(functionInstance, BiomeMapFunction::new)
    );

    private final TagKey<Biome> destination;
    private final Holder<MapDecorationType> mapDecoration;
    private final byte zoom;
    private final int searchRadius;
    private final int sampleResolutionHorizontal;
    private final int sampleResolutionVertical;

    private BiomeMapFunction(
            List<LootItemCondition> predicates,
            TagKey<Biome> destination,
            Holder<MapDecorationType> mapDecoration,
            byte zoom,
            int searchRadius,
            int sampleResolutionHorizontal,
            int sampleResolutionVertical
    ) {
        super(predicates);
        this.destination = destination;
        this.mapDecoration = mapDecoration;
        this.zoom = zoom;
        this.searchRadius = searchRadius;
        this.sampleResolutionHorizontal = sampleResolutionHorizontal;
        this.sampleResolutionVertical = sampleResolutionVertical;
    }

    @Override
    public MapCodec<BiomeMapFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.ORIGIN);
    }

    @Override
    public ItemStack run(final ItemStack stack, final LootContext context) {
        if (!stack.is(Items.MAP)) {
            return stack;
        }

        Vec3 lootPos = context.getOptionalParameter(LootContextParams.ORIGIN);

        if (lootPos == null) {
            return stack;
        }

        ServerLevel level = context.getLevel();

        Pair<BlockPos, Holder<Biome>> searchResult = level.findClosestBiome3d(
                biome -> biome.is(this.destination),
                BlockPos.containing(lootPos),
                this.searchRadius,
                this.sampleResolutionHorizontal,
                this.sampleResolutionVertical
        );

        if (searchResult == null) {
            return stack;
        }

        BlockPos pos = searchResult.getFirst();

        ItemStack map = MapItem.create(
                level,
                pos.getX(),
                pos.getZ(),
                this.zoom,
                true,
                true
        );

        MapItem.renderBiomePreviewMap(level, map);

        MapItemSavedData.addTargetDecoration(map, pos, "+", this.mapDecoration);

        return map;
    }

    public static BiomeMapFunction.Builder makeBiomeMap() {
        return new BiomeMapFunction.Builder();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<BiomeMapFunction.Builder> {
        private TagKey<Biome> destination;
        private Holder<MapDecorationType> mapDecoration = BiomeMapFunction.DEFAULT_DECORATION;
        private byte zoom = DEFAULT_ZOOM;
        private int searchRadius = DEFAULT_SEARCH_RADIUS;
        private int sampleResolutionHorizontal = DEFAULT_SAMPLE_RESOLUTION_HORIZONTAL;
        private int sampleResolutionVertical = DEFAULT_SAMPLE_RESOLUTION_VERTICAL;

        protected BiomeMapFunction.Builder getThis() {
            return this;
        }

        public BiomeMapFunction.Builder setDestination(TagKey<Biome> destination) {
            this.destination = destination;
            return this;
        }

        public BiomeMapFunction.Builder setMapDecoration(Holder<MapDecorationType> mapDecoration) {
            this.mapDecoration = mapDecoration;
            return this;
        }

        public BiomeMapFunction.Builder setZoom(byte zoom) {
            this.zoom = zoom;
            return this;
        }

        public BiomeMapFunction.Builder setSearchRadius(int searchRadius) {
            this.searchRadius = searchRadius;
            return this;
        }

        public BiomeMapFunction.Builder setSampleResolutionHorizontal(int sampleResolutionHorizontal) {
            this.sampleResolutionHorizontal = sampleResolutionHorizontal;
            return this;
        }

        public BiomeMapFunction.Builder setSampleResolutionVertical(int sampleResolutionVertical) {
            this.sampleResolutionVertical = sampleResolutionVertical;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new BiomeMapFunction(
                    this.getConditions(),
                    this.destination,
                    this.mapDecoration,
                    this.zoom,
                    this.searchRadius,
                    this.sampleResolutionHorizontal,
                    this.sampleResolutionVertical
            );
        }
    }
}
