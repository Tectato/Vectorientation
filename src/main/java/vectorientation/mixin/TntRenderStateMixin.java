package vectorientation.mixin;

import net.minecraft.client.renderer.entity.state.TntRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import vectorientation.access.TntEntityRenderStateAccess;

@Mixin(TntRenderState.class)
public class TntRenderStateMixin implements TntEntityRenderStateAccess {
    @Unique
    Vec3 velocity = Vec3.ZERO;
    @Unique
    double gravity = 9.81f / 20.0f;
    @Unique
    boolean onGround = false;

    @Override
    public void setVelocity(Vec3 velocity) {
        this.velocity = velocity;
    }

    @Override
    public Vec3 getVelocity(){
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
