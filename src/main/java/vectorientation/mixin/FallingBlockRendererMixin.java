package vectorientation.mixin;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.phys.Vec3;
import vectorientation.access.FallingBlockEntityRenderStateAccess;
import vectorientation.main.Vectorientation;

@Mixin(value = FallingBlockRenderer.class, priority = 1100)
public class FallingBlockRendererMixin {
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitMovingBlock(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/block/MovingBlockRenderState;)V"
			),
			method = "submit(Lnet/minecraft/client/renderer/entity/state/FallingBlockRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"
			)
	public void addRotation(FallingBlockRenderState fallingBlockEntityRenderState,
	                        PoseStack matrixStack,
	                        SubmitNodeCollector orderedRenderCommandQueue,
	                        CameraRenderState cameraRenderState,
	                        CallbackInfo ci) {
		Vec3 velD = ((FallingBlockEntityRenderStateAccess)fallingBlockEntityRenderState).getVelocity();
		Vector3f vel = new Vector3f((float) velD.x(), (float) velD.y(), (float) velD.z());
		float y = vel.y();
		y -= .04D * ((FallingBlockEntityRenderStateAccess)fallingBlockEntityRenderState).getGravity();
		y *= .98D;
		vel.y = y;
		boolean blacklisted = Vectorientation.BLACKLIST.contains(BuiltInRegistries.BLOCK.getKey(fallingBlockEntityRenderState.movingBlockRenderState.blockState.getBlock()));
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
		matrixStack.mulPose(rot);
		matrixStack.scale(1/speed, speed, 1/speed);
		matrixStack.translate(-0.5D, -0.5D, -0.5D);
	}

	@Inject(
			at = @At("HEAD"),
			method = "extractRenderState(Lnet/minecraft/world/entity/item/FallingBlockEntity;Lnet/minecraft/client/renderer/entity/state/FallingBlockRenderState;F)V"
	)
	public void updateRenderState(FallingBlockEntity fallingBlockEntity, FallingBlockRenderState fallingBlockEntityRenderState, float f, CallbackInfo ci){
		((FallingBlockEntityRenderStateAccess)fallingBlockEntityRenderState).setVelocity(fallingBlockEntity.getDeltaMovement());
		((FallingBlockEntityRenderStateAccess)fallingBlockEntityRenderState).setGravity(fallingBlockEntity.getGravity());
	}
}
