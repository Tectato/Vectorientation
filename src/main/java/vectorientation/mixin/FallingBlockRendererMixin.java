package vectorientation.mixin;

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
import vectorientation.main.Vectorientation;

@Mixin(FallingBlockEntityRenderer.class)
public class FallingBlockRendererMixin {
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/block/BlockModelRenderer;render("
							+ "Lnet/minecraft/world/BlockRenderView;"
							+ "Lnet/minecraft/client/render/model/BakedModel;"
							+ "Lnet/minecraft/block/BlockState;"
							+ "Lnet/minecraft/util/math/BlockPos;"
							+ "Lnet/minecraft/client/util/math/MatrixStack;"
							+ "Lnet/minecraft/client/render/VertexConsumer;"
							+ "Z"
							+ "Lnet/minecraft/util/math/random/Random;"
							+ "J"
							+ "I"
							+ ")V"
					),
			method = "render(Lnet/minecraft/entity/FallingBlockEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
			)
	public void addRotation(FallingBlockEntity fallingBlockEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
		Vec3d velD = fallingBlockEntity.getVelocity();
		Vector3f vel = new Vector3f((float) velD.getX(), (float) velD.getY(), (float) velD.getZ());
		float y = vel.y();
		y -= .04D * g;
		y *= .98D;
		float speed = Vectorientation.squetch ? 0.75f + (float) Math.sqrt(vel.x() * vel.x() + y * y + vel.z() * vel.z()) : 1;
		float angle = (float) Math.acos(y);
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
