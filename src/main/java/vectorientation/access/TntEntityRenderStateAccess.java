package vectorientation.access;

import net.minecraft.world.phys.Vec3;

public interface TntEntityRenderStateAccess {
    void setVelocity(Vec3 velocity);
    Vec3 getVelocity();

    void setGravity(double mag);
    double getGravity();

    void setOnGround(boolean value);
    boolean isOnGround();
}
