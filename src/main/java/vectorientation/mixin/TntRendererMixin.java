package vectorientation.mixin;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.entity.state.TntRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorientation.access.TntEntityRenderStateAccess;
import vectorientation.main.Vectorientation;

@Mixin(TntRenderer.class)
public class TntRendererMixin {
    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/TntMinecartRenderer;submitWhiteSolidBlock(Lnet/minecraft/client/renderer/block/BlockModelRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IZI)V"
            ),
            method = "submit(Lnet/minecraft/client/renderer/entity/state/TntRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"
    )
    public void addRotation(TntRenderState tntEntityRenderState, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if(!Vectorientation.TNT) return;
        Vec3 velD = ((TntEntityRenderStateAccess)tntEntityRenderState).getVelocity();
        Vector3f vel = new Vector3f((float) velD.x(), (float) velD.y(), (float) velD.z());
        float y = vel.y();
        boolean moving = !((TntEntityRenderStateAccess)tntEntityRenderState).isOnGround();
        if(moving) {
            y -= .04D * ((TntEntityRenderStateAccess)tntEntityRenderState).getGravity();
            y *= .98D;
        }
        vel.y = y;
        boolean blacklisted = Vectorientation.BLACKLIST.contains(Vectorientation.TNT_ID);
        float speed = (!blacklisted && moving && Vectorientation.SQUETCH) ?
                (float) (Vectorientation.MIN_WARP + Vectorientation.WARP_FACTOR * vel.length())
                : 1.0f;
        float angle = (float) Math.acos(vel.normalize().y);
        Vector3f axis = new Vector3f(-1 * vel.z(), 0, vel.x());
        Quaternionf rot = new Quaternionf();
        if(axis.length() > .01f){
            axis.normalize();
            rot = new Quaternionf(new AxisAngle4f(-angle, axis));
        }
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        matrixStack.mulPose(rot);
        matrixStack.scale(1/speed, speed, 1/speed);
        matrixStack.translate(-0.5D, -0.5D, -0.5D);
    }

    @Inject(
            at = @At("HEAD"),
            method = "extractRenderState(Lnet/minecraft/world/entity/item/PrimedTnt;Lnet/minecraft/client/renderer/entity/state/TntRenderState;F)V"
    )
    public void updateRenderState(PrimedTnt tntEntity, TntRenderState tntEntityRenderState, float f, CallbackInfo ci){
        ((TntEntityRenderStateAccess)tntEntityRenderState).setVelocity(tntEntity.getDeltaMovement());
        ((TntEntityRenderStateAccess)tntEntityRenderState).setGravity(tntEntity.getGravity());
        ((TntEntityRenderStateAccess)tntEntityRenderState).setOnGround(tntEntity.onGround());
    }
}

