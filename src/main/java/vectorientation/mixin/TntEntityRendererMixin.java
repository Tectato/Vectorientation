package vectorientation.mixin;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.TntEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorientation.main.Vectorientation;

@Mixin(TntEntityRenderer.class)
public class TntEntityRendererMixin {
    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/TntMinecartEntityRenderer;renderFlashingBlock(Lnet/minecraft/client/render/block/BlockRenderManager;Lnet/minecraft/block/BlockState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IZ)V"
            ),
            method = "render(Lnet/minecraft/entity/TntEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
    )
    public void addRotation(TntEntity tntEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
        if(!Vectorientation.TNT) return;
        Vec3d velD = tntEntity.getVelocity();
        Vector3f vel = new Vector3f((float) velD.getX(), (float) velD.getY(), (float) velD.getZ());
        float y = vel.y();
        boolean moving = !tntEntity.isOnGround();
        if(moving) {
            y -= .04D * g;
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
}

