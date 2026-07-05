package vectorientation.mixin;

import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorientation.main.Vectorientation;

@Mixin(MinecartRenderer.class)
public class MinecartRendererMixin<T extends AbstractMinecart> {
    /*@Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;getContainedBlock()Lnet/minecraft/block/BlockState;"
            ),
            method = "Lnet/minecraft/client/render/entity/MinecartEntityRenderer;render(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
    )
    public void addRotation(T minecart, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
        if(!Vectorientation.MINECARTS) return;
        if(isOnRail(minecart)) return;
        Vec3d velD = minecart.getVelocity();
        Vector3f vel = new Vector3f((float) velD.getX(), (float) velD.getY(), (float) velD.getZ());
        if(minecart.isOnGround() || vel.length() < .03f) return;
        float y = vel.y();
        if(Math.abs(y) > 0.01f) {
            y -= .04D * g;
            y *= .98D;
        }
        vel.y = y;
        boolean xMajor = Math.abs(vel.x()) > Math.abs(vel.z());
        float angle = (float) (Math.asin(vel.normalize().y));
        Vector3f axis = new Vector3f(0,1,0).cross(xMajor ? new Vector3f(0,0,1) : new Vector3f(1,0,0));
        Quaternionf rot = new Quaternionf();
        if(axis.length() > .01f){
            axis.normalize();
            rot = new Quaternionf(new AxisAngle4f(-angle, axis));
        }
        boolean translate = minecart.hasPassengers();
        if (translate) matrixStack.translate(0.0D, 0.5D, 0.0D);
        matrixStack.multiply(rot);
        if (translate) matrixStack.translate(-0.0D, -0.5D, -0.0D);
    }*/

    /*private boolean isOnRail(T minecart){
        int posX = MathHelper.floor(minecart.getX());
        int posY = MathHelper.floor(minecart.getY());
        int posZ = MathHelper.floor(minecart.getZ());
        if (minecart.getEntityWorld().getBlockState(new BlockPos(posX, posY - 1, posZ)).isIn(BlockTags.RAILS)) {
            --posZ;
        }

        BlockState blockState = minecart.getEntityWorld().getBlockState(new BlockPos(posX, posY, posZ));
        if (AbstractRailBlock.isRail(blockState)) {
            return true;
        }
        return false;
    }*/
}
