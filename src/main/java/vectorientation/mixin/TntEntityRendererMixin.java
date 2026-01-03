package vectorientation.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.client.render.entity.state.TntEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.TntEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorientation.access.TntEntityRenderStateAccess;
import vectorientation.main.Vectorientation;

@Mixin(TntEntityRenderer.class)
public class TntEntityRendererMixin {
    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/TntMinecartEntityRenderer;renderFlashingBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;IZI)V"
            ),
            method = "render(Lnet/minecraft/client/render/entity/state/TntEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"
    )
    public void addRotation(TntEntityRenderState tntEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if(!Vectorientation.TNT) return;
        Vec3d velD = ((TntEntityRenderStateAccess)tntEntityRenderState).getVelocity();
        Vector3f vel = new Vector3f((float) velD.getX(), (float) velD.getY(), (float) velD.getZ());
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
        matrixStack.multiply(rot);
        matrixStack.scale(1/speed, speed, 1/speed);
        matrixStack.translate(-0.5D, -0.5D, -0.5D);
    }

    @Inject(
            at = @At("HEAD"),
            method = "updateRenderState(Lnet/minecraft/entity/TntEntity;Lnet/minecraft/client/render/entity/state/TntEntityRenderState;F)V"
    )
    public void updateRenderState(TntEntity tntEntity, TntEntityRenderState tntEntityRenderState, float f, CallbackInfo ci){
        ((TntEntityRenderStateAccess)tntEntityRenderState).setVelocity(tntEntity.getVelocity());
        ((TntEntityRenderStateAccess)tntEntityRenderState).setGravity(tntEntity.getFinalGravity());
        ((TntEntityRenderStateAccess)tntEntityRenderState).setOnGround(tntEntity.isOnGround());
    }
}

