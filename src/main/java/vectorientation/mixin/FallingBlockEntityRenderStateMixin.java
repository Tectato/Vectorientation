package vectorientation.mixin;

import net.minecraft.client.render.entity.state.FallingBlockEntityRenderState;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import vectorientation.access.FallingBlockEntityRenderStateAccess;

@Mixin(FallingBlockEntityRenderState.class)
public class FallingBlockEntityRenderStateMixin implements FallingBlockEntityRenderStateAccess {
    @Unique
    Vec3d velocity = Vec3d.ZERO;
    @Unique
    double gravity = 9.81f / 20.0f;

    @Override
    public void setVelocity(Vec3d velocity) {
        this.velocity = velocity;
    }

    @Override
    public Vec3d getVelocity(){
        return this.velocity;
    }

    @Override
    public void setGravity(double mag) {
        gravity = mag;
    }

    @Override
    public double getGravity() {
        return gravity;
    }
}
