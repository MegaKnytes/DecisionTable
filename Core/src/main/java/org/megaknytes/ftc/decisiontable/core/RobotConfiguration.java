package org.megaknytes.ftc.decisiontable.core;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class RobotConfiguration {
    public static final class HARDWARE_MAP {
        public static final class MOTORS {

            // ********** FLYWHEEL **********
            public static final String FLYWHEEL_ONE = "flywheelOne";
            public static final DcMotorSimple.Direction FLYWHEEL_ONE_DIRECTION = DcMotorSimple.Direction.FORWARD;

            public static final String FLYWHEEL_TWO = "flywheelTwo";
            public static final DcMotorSimple.Direction FLYWHEEL_TWO_DIRECTION = DcMotorSimple.Direction.FORWARD;

        }
    }

    public enum FLYWHEEL_SPEED {
        OFF(0);

        private final double velocity;

        FLYWHEEL_SPEED(double velocity) {
            this.velocity = velocity;
        }

        public double getVelocity() {
            return velocity;
        }

    }

    public static final class FLYWHEEL_PID {
        public static final double KP = 0.0006;

        public static final double KI = 0.0000000000;

        public static final double KD = 0.000006;
    }
}