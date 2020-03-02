package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.*;
import frc.robot.subsystems.Flywheel.Speeds;
import frc.robot.subsystems.Flywheel.Velocity;
import frc.team5431.titan.core.vision.Limelight;

/**
 * @author Colin Wong
 * @author Ryan Hirasaki
 */
public class ShootSuperCommandClose extends SequentialCommandGroup {
    public ShootSuperCommandClose(Intake intake, Hopper hopper, Feeder feeder, Flywheel flywheel, Drivebase drivebase, Limelight limelight) {
        addCommands(
            // Target
            new Targetor(drivebase, limelight),
             // Bring up to speed
            new FlywheelCommand(flywheel, Velocity.HALF), //Waits till up to speed
            // Push Balls. Keep running until current command is interuppted
            new PushBallsUpSubCommand(intake, hopper, feeder) 
        );
    }
}