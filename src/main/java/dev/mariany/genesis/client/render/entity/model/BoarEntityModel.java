package dev.mariany.genesis.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

@Environment(EnvType.CLIENT)
public class BoarEntityModel extends QuadrupedEntityModel<LivingEntityRenderState> {
    public BoarEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        return getTexturedModelData(Dilation.NONE);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        return TexturedModelData.of(getModelData(dilation), 64, 64);
    }

    protected static ModelData getModelData(Dilation dilation) {
        ModelData modelData = QuadrupedEntityModel.getModelData(6, true, false, dilation);
        ModelPartData root = modelData.getRoot();

        ModelPartData head = root.addChild(
                EntityModelPartNames.HEAD,
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-4F, -4F, -8F, 8F, 8F, 8F, dilation)
                        .uv(16, 16)
                        .cuboid(-2F, 0F, -9F, 4F, 3F, 1F, dilation)
                ,
                ModelTransform.origin(0F, 12F, -6F)
        );

        head.addChild("tusk", ModelPartBuilder.create()
                        .uv(12, 17)
                        .cuboid(1F, -11F, -16F, 1F, 2F, 1F, dilation)
                        .cuboid(6F, -11F, -16F, 1F, 2F, 1F, dilation)
                ,
                ModelTransform.origin(-4F, 12F, 7F)
        );

        head.addChild("hair", ModelPartBuilder.create()
                        .uv(54, 32)
                        .cuboid(-1F, 0, 2F, 2F, 7F, 1F, dilation),
                ModelTransform.of(0F, -2, -7F, 1.5708F, 0F, 0F)
        );

        root.addChild("hair", ModelPartBuilder.create()
                        .uv(54, 41)
                        .cuboid(-1F, 0, 2F, 2F, 12F, 1F, dilation),
                ModelTransform.of(0F, 12F, -6, 1.5708F, 0F, 0F)
        );

        return modelData;
    }
}
