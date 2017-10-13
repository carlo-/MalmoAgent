package domain.fluents;

import domain.AtomicFluent;
import main.Observations;

/**
 * Created by Carlo on 9.10.2017.
 */
public class LookingAt implements AtomicFluent {

    private final float x;
    private final float z;
    private final float y;

    public LookingAt(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean test(Observations observations) {

        float xRel = x - observations.XPos;
        float yRel = y - observations.YPos;
        float zRel = z - observations.ZPos;

        float d = (float) Math.sqrt(Math.pow(xRel, 2) + Math.pow(zRel, 2) + Math.pow(yRel, 2));

        float d_xz = (float) Math.sqrt(Math.pow(xRel, 2) + Math.pow(zRel, 2));
        float phi = (float) Math.toDegrees(Math.acos(zRel / d_xz));
        if (!Float.isFinite(phi)) phi = 0;
        if (xRel < 0 && zRel < 0) phi += (180 - phi) * 2;
        if (xRel < 0 && zRel > 0) phi = 360 - phi;
        phi = 360 - phi;

        // float d_yz = d; //(float)Math.sqrt(Math.pow(yRel, 2)+Math.pow(zRel, 2));
        float theta = (float) Math.toDegrees(-Math.asin(yRel / d));

        // Margin
        float alpha_xz = (float) Math.toDegrees(Math.atan(1.0 / (2 * d - 1)));
        float alpha_yz = (float) Math.toDegrees(Math.atan(1.0 / (2 * d - 1)));

        float pitch = observations.Pitch;
        float yaw = observations.Yaw;
        float yawCW = yaw >= 0f ? yaw : 360f + yaw;

        boolean yawSat = Math.abs(yawCW - phi) <= 4 || (Math.abs(yawCW) == 360 && phi == 0) || (Math.abs(phi) == 360 && yawCW == 0);
        boolean pitchSat = Math.abs(pitch - theta) <= 4;

        /*
        System.err.println("yawCW: "+yawCW+", phi: "+phi+", alpha_xz: "+alpha_xz+", yawSat: "+yawSat);
        System.err.println("pitch: "+pitch+", theta: "+theta+", alpha_yz: "+alpha_yz+", pitchSat: "+pitchSat);
        System.err.println("-----------------------------");
        System.err.println("ob.x: "+observations.XPos+", ob.y: "+observations.YPos+", ob.z: "+observations.ZPos);
        */

        return yawSat && pitchSat;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
