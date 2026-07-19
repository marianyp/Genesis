package dev.mariany.genesis.client.render.entity.model;

import dev.mariany.genesis.Genesis;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class GenesisModelLayers {
    public static final ModelLayerLocation BOAR = create("boar");

    private static ModelLayerLocation create(String id) {
        return new ModelLayerLocation(Genesis.id(id), "main");
    }

    public static void bootstrap() {
        ModelLayerRegistry.registerModelLayer(BOAR, BoarEntityModel::getTexturedModelData);
    }
}
