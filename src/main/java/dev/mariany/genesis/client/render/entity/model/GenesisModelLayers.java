package dev.mariany.genesis.client.render.entity.model;

import dev.mariany.genesis.Genesis;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public class GenesisModelLayers {
    public static final EntityModelLayer BOAR = create("boar");

    private static EntityModelLayer create(String id) {
        return new EntityModelLayer(Genesis.id(id), "main");
    }

    public static void bootstrap() {
        EntityModelLayerRegistry.registerModelLayer(BOAR, BoarEntityModel::getTexturedModelData);
    }
}
