package org.megaknytes.ftc.decisiontable.core.utilities;

import org.megaknytes.ftc.decisiontable.core.RobotConfiguration;
import com.bylazar.configurables.annotations.Configurable;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.controllable.MotorGroup;
import dev.nextftc.hardware.impl.MotorEx;

@Configurable
public class Flywheel implements Subsystem {
    public static final Flywheel INSTANCE = new Flywheel();

    // PID Coefficients for the flywheel controller
    // TODO: Still needs further tuning
    public static PIDCoefficients FLYWHEEL_VELOCITY_COEFFICIENTS = new PIDCoefficients(0.0006, 0, 0.000006);
    public static PIDCoefficients FLYWHEEL_POSITION_COEFFICIENTS = new PIDCoefficients(0, 0, 0);

    // Tolerance for the non-lazy flywheel velocity command & flywheel velocity check
    public static KineticState FLYWHEEL_VELOCITY_TOLERANCE = new KineticState(Double.POSITIVE_INFINITY,  rpmToTicks(50), Double.POSITIVE_INFINITY);
    public static KineticState FLYWHEEL_POSITION_TOLERANCE = new KineticState(3,  Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static KineticState FLYWHEEL_STOPPED_TOLERANCE = new KineticState(Double.POSITIVE_INFINITY,  rpmToTicks(5), Double.POSITIVE_INFINITY);

    // Velocity controller for the flywheel motors - uses the defined PID coefficients to achieve a target KineticState
    // All units for this velocity controller are in ticks / ticks per second / ticks per second squared
    private final ControlSystem flywheelController = ControlSystem.builder()
            .velPid(FLYWHEEL_VELOCITY_COEFFICIENTS)
            .posPid(FLYWHEEL_POSITION_COEFFICIENTS)
            .build();


    // MotorGroup representing the flywheel motors
    private final MotorGroup flywheelMotors = new MotorGroup(
            new MotorEx(RobotConfiguration.HARDWARE_MAP.MOTORS.FLYWHEEL_TWO).floatMode(),
            new MotorEx(RobotConfiguration.HARDWARE_MAP.MOTORS.FLYWHEEL_ONE).floatMode()
    );

    /**
     * Runs periodically to update the flywheel motors' power based on the velocity controller's output.
     */
    @Override
    public void periodic() {
        flywheelMotors.setPower(
                flywheelController.calculate(flywheelMotors.getState())
        );
    }

    /**
     * Creates a command to set the flywheel's target velocity lazily.
     * The command will finish immediately after setting the target velocity.
     *
     * @param targetVelocityRPM The target velocity in revolutions per minute (RPM).
     * @return A command that sets the flywheel's target velocity.
     */
    public Command setTargetVelocityLazy(double targetVelocityRPM) {
        return new InstantCommand(() -> flywheelController.setGoal(new KineticState(0, rpmToTicks(targetVelocityRPM))))
                .requires(this);
    }

    /**
     * Creates a command to set the flywheel's target velocity.
     * The command completes when the flywheel reaches the target velocity within the defined tolerance.
     *
     * @param targetVelocityRPM The target velocity in revolutions per minute (RPM).
     * @return A command that sets the flywheel's target velocity.
     */
    public Command setTargetVelocity(double targetVelocityRPM) {
        return new LambdaCommand()
                .setStart(() -> flywheelController.setGoal(new KineticState(0, rpmToTicks(targetVelocityRPM))))
                .setIsDone(() -> flywheelController.isWithinTolerance(FLYWHEEL_VELOCITY_TOLERANCE))
                .requires(this);
    }

    public double getVelocity() {
        return flywheelMotors.getVelocity();
    }

    public Command stopFlywheel() {
        return new LambdaCommand()
                .setStart(() -> flywheelController.setGoal(new KineticState(0, 0)))
                .setIsDone(() -> flywheelController.isWithinTolerance(FLYWHEEL_STOPPED_TOLERANCE))
                .requires(this);
    }

    public Command moveFlywheelByRevolutions(int revolutions) {
        return new LambdaCommand()
                .setStart(() -> flywheelController.setGoal(new KineticState(flywheelMotors.getState().getPosition() + (revolutions * 28), 0)))
                .setIsDone(() -> flywheelController.isWithinTolerance(FLYWHEEL_POSITION_TOLERANCE))
                .requires(this);
    }

    /**
     * Converts encoder ticks per second to revolutions per minute (RPM).
     * The GoBILDA 5000 Series motor has 28 ticks per revolution.
     *
     * @param ticksPerSecond The velocity in ticks per second.
     * @return The velocity in revolutions per minute (RPM).
     */
    private static double ticksToRPM(double ticksPerSecond) {
        return (ticksPerSecond / 28) * 60;
    }

    /**
     * Converts revolutions per minute (RPM) to encoder ticks per second.
     * The GoBILDA 5000 Series motor has 28 ticks per revolution.
     *
     * @param rpm The velocity in revolutions per minute (RPM).
     * @return The velocity in ticks per second.
     */
    private static double rpmToTicks(double rpm) {
        return (rpm / 60) * 28;
    }
}