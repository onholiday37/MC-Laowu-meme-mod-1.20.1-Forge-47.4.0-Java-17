package com.rogic.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rogic.client.ClientMemeState;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.world.entity.animal.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 在原版猫 renderer 的缩放阶段追加 1.25 倍，仅改变视觉，不改变碰撞箱。 */
@Mixin(CatRenderer.class)
public abstract class CatRendererMixin {
    @Inject(method = "scale", at = @At("TAIL"))
    private void laowu$scale(Cat cat, PoseStack poseStack, float partialTick, CallbackInfo ci) {
        if (ClientMemeState.get().isActive(cat.getId())) {
            poseStack.scale(1.25F, 1.25F, 1.25F);
        }
    }
}
