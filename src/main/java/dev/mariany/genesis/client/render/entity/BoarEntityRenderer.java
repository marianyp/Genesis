package dev.mariany.genesis.client.render.entity;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.client.render.entity.layer.BoarEyesLayer;
import dev.mariany.genesis.client.render.entity.model.BoarEntityModel;
import dev.mariany.genesis.client.render.entity.model.GenesisModelLayers;
import dev.mariany.genesis.entity.custom.mob.BoarEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class BoarEntityRenderer extends MobRenderer<BoarEntity, LivingEntityRenderState, BoarEntityModel> {
    private static final Identifier TEXTURE = Genesis.id("textures/entity/boar/boar.png");

    public BoarEntityRenderer(EntityRendererProvider.Context context) {
        this(context, GenesisModelLayers.BOAR);
    }

    public BoarEntityRenderer(EntityRendererProvider.Context ctx, ModelLayerLocation layer) {
        super(ctx, new BoarEntityModel(ctx.bakeLayer(layer)), 0.7F);
        this.addLayer(new BoarEyesLayer<>(this));
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }
}
