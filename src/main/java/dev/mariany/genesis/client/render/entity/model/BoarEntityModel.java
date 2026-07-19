package dev.mariany.genesis.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

@Environment(EnvType.CLIENT)
public class BoarEntityModel extends QuadrupedModel<LivingEntityRenderState> {
    public BoarEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static LayerDefinition getTexturedModelData() {
        return getTexturedModelData(CubeDeformation.NONE);
    }

    public static LayerDefinition getTexturedModelData(CubeDeformation dilation) {
        return LayerDefinition.create(getModelData(dilation), 64, 64);
    }

    protected static MeshDefinition getModelData(CubeDeformation dilation) {
        MeshDefinition modelData = QuadrupedModel.createBodyMesh(6, true, false, dilation);
        PartDefinition root = modelData.getRoot();

        PartDefinition head = root.addOrReplaceChild(
                PartNames.HEAD,
                CubeListBuilder.create()
                               .texOffs(0, 0)
                               .addBox(-4F, -4F, -8F, 8F, 8F, 8F, dilation)
                               .texOffs(16, 16)
                               .addBox(-2F, 0F, -9F, 4F, 3F, 1F, dilation)
                ,
                PartPose.offset(0F, 12F, -6F)
        );

        head.addOrReplaceChild(
                "tusk",
                CubeListBuilder.create()
                               .texOffs(12, 17)
                               .addBox(1F, -11F, -16F, 1F, 2F, 1F, dilation)
                               .addBox(6F, -11F, -16F, 1F, 2F, 1F, dilation)
                ,
                PartPose.offset(-4F, 12F, 7F)
        );

        head.addOrReplaceChild(
                "hair",
                CubeListBuilder.create()
                               .texOffs(54, 32)
                               .addBox(-1F, 0, 2F, 2F, 7F, 1F, dilation),
                PartPose.offsetAndRotation(0F, -2, -7F, 1.5708F, 0F, 0F)
        );

        root.addOrReplaceChild(
                "hair",
                CubeListBuilder.create()
                               .texOffs(54, 41)
                               .addBox(-1F, 0, 2F, 2F, 12F, 1F, dilation),
                PartPose.offsetAndRotation(0F, 12F, -6, 1.5708F, 0F, 0F)
        );

        return modelData;
    }
}
