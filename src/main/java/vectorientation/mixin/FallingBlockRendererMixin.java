package vectorientation.mixin;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.FallingBlockEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.registry.Registries;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.Vec3d;
import vectorientation.access.FallingBlockEntityRenderStateAccess;
import vectorientation.main.Vectorientation;

@Mixin(value = FallingBlockEntityRenderer.class, priority = 1100)
public class FallingBlockRendererMixin {
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitMovingBlock(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/block/MovingBlockRenderState;)V"
			),
			method = "render(Lnet/minecraft/client/render/entity/state/FallingBlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"
			)
	public void addRotation(FallingBlockEntityRenderState fallingBlockEntityRenderState,
							MatrixStack matrixStack,
							OrderedRenderCommandQueue orderedRenderCommandQueue,
							CameraRenderState cameraRenderState,
							CallbackInfo ci) {
		Vec3d velD = ((FallingBlockEntityRenderStateAccess)fallingBlockEntityRenderState).getVelocity();
		Vector3f vel = new Vector3f((float) velD.getX(), (float) velD.getY(), (float) velD.getZ());
		float y = vel.y();
		y -= .04D * ((FallingBlockEntityRenderStateAccess)fallingBlockEntityRenderState).getGravity();
		y *= .98D;
		vel.y = y;
		boolean blacklisted = Vectorientation.BLACKLIST.contains(Registries.BLOCK.getId(fallingBlockEntityRenderState.movingBlockRenderState.blockState.getBlock()));
		float speed = (!blacklisted && Vectorientation.SQUETCH) ?
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
			method = "updateRenderState(Lnet/minecraft/entity/FallingBlockEntity;Lnet/minecraft/client/render/entity/state/FallingBlockEntityRenderState;F)V"
	)
	public void updateRenderState(FallingBlockEntity fallingBlockEntity, FallingBlockEntityRenderState fallingBlockEntityRenderState, float f, CallbackInfo ci){
		((FallingBlockEntityRenderStateAccess)fallingBlockEntityRenderState).setVelocity(fallingBlockEntity.getVelocity());
		((FallingBlockEntityRenderStateAccess)fallingBlockEntityRenderState).setGravity(fallingBlockEntity.getFinalGravity());
	}
}
