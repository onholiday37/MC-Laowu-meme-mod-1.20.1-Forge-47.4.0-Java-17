package com.rogic.client.mixin;

import com.rogic.client.ClientMemeState;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 在原版 1.20.1 猫模型动画完成后叠加歪头和弓背姿势。 */
@Mixin(OcelotModel.class)
public abstract class OcelotModelMixin {
    private static final float HEAD_ROLL = (float) (Math.PI / 4.0);
    private static final float HEAD_DIP = 0.30F;
    private static final float BODY_PITCH = 0.10F;
    private static final float TAIL_LIFT = 0.90F;
    private static final float HIND_SCALE = 1.40F;
    private static final float FRONT_SCALE = 0.85F;

    @Shadow @Final protected ModelPart head;
    @Shadow @Final protected ModelPart body;
    @Shadow @Final protected ModelPart tail2;
    @Shadow @Final protected ModelPart leftHindLeg;
    @Shadow @Final protected ModelPart rightHindLeg;
    @Shadow @Final protected ModelPart leftFrontLeg;
    @Shadow @Final protected ModelPart rightFrontLeg;

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void laowu$applyPose(Entity entity, float limbSwing, float limbSwingAmount,
                                 float ageInTicks, float netHeadYaw, float headPitch,
                                 CallbackInfo ci) {
        leftHindLeg.yScale = 1.0F;
        rightHindLeg.yScale = 1.0F;
        leftFrontLeg.yScale = 1.0F;
        rightFrontLeg.yScale = 1.0F;

        if (!(entity instanceof Cat cat) || !ClientMemeState.get().isActive(cat.getId())) return;

        head.zRot = ClientMemeState.get().getRollSign(cat.getId()) * HEAD_ROLL;
        head.xRot += HEAD_DIP;
        body.xRot += BODY_PITCH;
        tail2.xRot += TAIL_LIFT;
        leftHindLeg.yScale = HIND_SCALE;
        rightHindLeg.yScale = HIND_SCALE;
        leftFrontLeg.yScale = FRONT_SCALE;
        rightFrontLeg.yScale = FRONT_SCALE;
    }
}
