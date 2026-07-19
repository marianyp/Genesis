package dev.mariany.genesis.client.render.entity.layer;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.client.render.entity.model.BoarEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

@Environment(EnvType.CLIENT)
public class BoarEyesLayer<M extends BoarEntityModel> extends EyesLayer<LivingEntityRenderState, M> {
    private static final RenderType SPIDER_EYES = RenderTypes.eyes(
            Genesis.id("textures/entity/boar/boar_eyes.png")
    );

    public BoarEyesLayer(RenderLayerParent<LivingEntityRenderState, M> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return SPIDER_EYES;
    }
}

