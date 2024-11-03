package vectorientation.access;

import net.minecraft.util.math.Vec3d;

public interface FallingBlockEntityRenderStateAccess {
    void setVelocity(Vec3d velocity);
    Vec3d getVelocity();

    void setGravity(double mag);
    double getGravity();
}