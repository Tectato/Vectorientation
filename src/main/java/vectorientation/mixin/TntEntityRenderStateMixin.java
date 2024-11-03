package vectorientation.mixin;

import net.minecraft.client.render.entity.state.TntEntityRenderState;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import vectorientation.access.TntEntityRenderStateAccess;

@Mixin(TntEntityRenderState.class)
public class TntEntityRenderStateMixin implements TntEntityRenderStateAccess {
    @Unique
    Vec3d velocity = Vec3d.ZERO;
    @Unique
    double gravity = 9.81f / 20.0f;
    @Unique
    boolean onGround = false;

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

    @Override
    public void setOnGround(boolean value) {
        onGround = value;
    }

    @Override
    public boolean isOnGround() {
        return onGround;
    }
}
