package frc.robot;

import java.util.List;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.commands.*;
import frc.robot.subsystems.*;
import frc.team5431.titan.core.joysticks.Joystick;
import frc.team5431.titan.core.joysticks.LogitechExtreme3D;
import frc.team5431.titan.core.joysticks.Xbox;
import frc.team5431.titan.core.vision.LEDState;
import frc.team5431.titan.core.vision.Limelight;

public class RobotMap {
    private final Balancer balancer = new Balancer();
    private final Drivebase drivebase = new Drivebase();
    private final Elevator elevator = new Elevator();
    private final Feeder feeder = new Feeder();
    private final Flywheel flywheel = new Flywheel();
    private final Hopper hopper = new Hopper();
    private final Intake intake = new Intake();
    private final Pivot pivot = new Pivot();

    private final Xbox driver = new Xbox(0);
    private final Joystick buttonBoard = new Joystick(1);
    private final LogitechExtreme3D operator = new LogitechExtreme3D(3);

    private final Limelight limelight = new Limelight(Constants.VISION_FRONT_LIMELIGHT);

    private static enum StartPosition {

    };

    SendableChooser<StartPosition> chooser = new SendableChooser<>();

    public RobotMap() {
        limelight.setLEDState(LEDState.OFF);
        bindKeys();

    }

    private void bindKeys() {
        // Driver Controls
        {
            // Target the Upper Hatch
            new JoystickButton(driver, Xbox.Button.B.ordinal() + 1).whenPressed(new Targetor(drivebase, limelight));

            // Calibrate the Limelight
            new JoystickButton(driver, Xbox.Button.X.ordinal() + 1);

            // Intake
            new JoystickButton(driver, Xbox.Button.A.ordinal() + 1);

            // // Run the Intake when pressed
            // // TODO: have a sequence to bring down the intake automatically.
            // new JoystickButton(driver, Xbox.Button.Y.ordinal() + 1).toggleWhenPressed(new IntakeCommand(intake, false));
        }

        // Operator Controls
        {
            // // When Pressed, will stop when complete
            // new JoystickButton(operator, 7).whenPressed(new Targetor(drivebase,
            // limelight));

            // // Toggle Hopper
            // new JoystickButton(operator, LogitechExtreme3D.Button.THREE.ordinal() + 1)
            // .toggleWhenPressed(new HopperCommand(hopper, false));

            // // Feeder while held
            // new JoystickButton(operator, LogitechExtreme3D.Button.TEN.ordinal() + 1)
            // .whenPressed(new FeederCommand(feeder, false));

            new JoystickButton(buttonBoard, 2).toggleWhenPressed(new FloorIntakeSuperCommand(intake, hopper, flywheel));

            // All Out
            new JoystickButton(buttonBoard, 4);

            // Pivot Down
            new JoystickButton(buttonBoard, 6);

            // Pivot Up
            new JoystickButton(buttonBoard, 5);

            // Vision
            new JoystickButton(buttonBoard, 7);

            // In
            new JoystickButton(buttonBoard, 11);

            // Shoot
            new JoystickButton(buttonBoard, 1);

            // Intake
            new JoystickButton(buttonBoard, 14);

            /*
             * Not used as it is bound to triggers for the driver but here for historical
             * purposes
             * 
             * // Climb
             * new JoystickButton(buttonBoard, 8);
             */

            // Stuck On Side.
            // Run Hopper and pivot up with intake on
            new JoystickButton(buttonBoard, 13);

            new JoystickButton(buttonBoard, 12)
                    .whenPressed(new StowSuperCommand(intake, hopper, feeder, flywheel, elevator, balancer, pivot));

            // Auto Switch
            new JoystickButton(buttonBoard, 9);
        }

        // Operator Logitech
        {
            // Indexer Up
            new POVButton(operator, 0);

            // Indexer Down
            new POVButton(operator, 180);

            // Trigger Flywheel
            new JoystickButton(operator, LogitechExtreme3D.Button.TRIGGER.ordinal() + 1);

            // Three Hopper Out
            new JoystickButton(operator, LogitechExtreme3D.Button.THREE.ordinal() + 1);

            // Five Hopper In
            new JoystickButton(operator, LogitechExtreme3D.Button.FIVE.ordinal() + 1);

            // Six Intake Pivot Down
            new JoystickButton(operator, LogitechExtreme3D.Button.SIX.ordinal() + 1);

            // Four Intake Pivot Up
            new JoystickButton(operator, LogitechExtreme3D.Button.FOUR.ordinal() + 1);

            // Two Intake
            new JoystickButton(operator, LogitechExtreme3D.Button.TWO.ordinal() + 1);
        }

        // Default Commands
        {
            driver.setDeadzone(Constants.DRIVER_XBOX_DEADZONE);

            drivebase.setDefaultCommand(new DefaultDrive(drivebase, () -> -driver.getRawAxis(Xbox.Axis.LEFT_Y),
                    () -> -driver.getRawAxis(Xbox.Axis.LEFT_X)));

            elevator.setDefaultCommand(new DefaultElevator(elevator,
                    () -> driver.getRawAxis(Xbox.Axis.TRIGGER_RIGHT) - driver.getRawAxis(Xbox.Axis.TRIGGER_LEFT)));
        }
    }

    public Command getAutonomousCommand() {

        final var autoVoltageConstraint = new DifferentialDriveVoltageConstraint(
                new SimpleMotorFeedforward(DriveConstants.ksVolts, DriveConstants.kvVoltSecondsPerMeter,
                        DriveConstants.kaVoltSecondsSquaredPerMeter),
                DriveConstants.kDriveKinematics, 10);

        final TrajectoryConfig config = new TrajectoryConfig(AutoConstants.kMaxSpeedMetersPerSecond,
                AutoConstants.kMaxAccelerationMetersPerSecondSquared)
                        // Add kinematics to ensure max speed is actually obeyed
                        .setKinematics(DriveConstants.kDriveKinematics)
                        // Apply the voltage constraint
                        .addConstraint(autoVoltageConstraint);

        Trajectory trajectory = null;

        switch (chooser.getSelected()) {
        default:
            trajectory = TrajectoryGenerator.generateTrajectory(
                    // Start at the origin facing the +X direction
                    new Pose2d(0, 0, new Rotation2d(0)),
                    // Pass through these two interior waypoints, making an 's' curve path
                    List.of(new Translation2d(1, 1), new Translation2d(2, -1)),
                    // End 3 meters straight ahead of where we started, facing forward
                    new Pose2d(3, 0, new Rotation2d(0)),
                    // Pass config
                    config);
        }

        assert (trajectory != null);

        final RamseteCommand ramseteCommand = new RamseteCommand(trajectory, drivebase::getPose,
                new RamseteController(AutoConstants.kRamseteB, AutoConstants.kRamseteZeta),
                new SimpleMotorFeedforward(DriveConstants.ksVolts, DriveConstants.kvVoltSecondsPerMeter,
                        DriveConstants.kaVoltSecondsSquaredPerMeter),
                DriveConstants.kDriveKinematics, drivebase::getWheelSpeeds,
                new PIDController(DriveConstants.kPDriveVel, 0, 0), new PIDController(DriveConstants.kPDriveVel, 0, 0),
                // RamseteCommand passes volts to the callback
                drivebase::tankDriveVolts, drivebase);

        return ramseteCommand.andThen(() -> drivebase.tankDriveVolts(0, 0));

        // return chooser.getSelected();
    }
}