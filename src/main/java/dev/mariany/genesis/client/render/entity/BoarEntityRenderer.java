package dev.mariany.genesis.client.render.entity;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.client.render.entity.model.GenesisModelLayers;
import dev.mariany.genesis.client.render.entity.model.BoarEntityModel;
import dev.mariany.genesis.entity.custom.mob.BoarEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BoarEntityRenderer extends MobEntityRenderer<BoarEntity, LivingEntityRenderState, BoarEntityModel> {
    private static final Identifier TEXTURE = Genesis.id("textures/entity/boar.png");

    public BoarEntityRenderer(EntityRendererFactory.Context context) {
        this(context, GenesisModelLayers.BOAR);
    }

    public BoarEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx, new BoarEntityModel(ctx.getPart(layer)), 0.7F);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }
}
