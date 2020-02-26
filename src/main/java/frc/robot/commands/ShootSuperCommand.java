package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.*;
import frc.robot.subsystems.Flywheel.Speeds;

public class ShootSuperCommand extends SequentialCommandGroup {
    public ShootSuperCommand(Intake intake, Hopper hopper, Feeder feeder, Flywheel flywheel) {
        addCommands(
            new FlywheelControl(flywheel, Speeds.FULL),
            new FeederCommand(feeder, false),
            new HopperCommand(hopper, false),
            new IntakeCommand(intake, false)
        );
    }
}