package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.*;

public class StowSuperCommand extends SequentialCommandGroup {
	public StowSuperCommand(Intake intake, Hopper hopper, Feeder feeder, Flywheel flywheel, Elevator elevator, Balancer balancer) {
		addCommands(
			new PivotCommand(intake, Intake.POSITION.UP),
			new FlywheelControl(flywheel, Flywheel.Speeds.OFF, true)
		);

		// Requirements are not needed
		// addRequirements(intake, hopper, feeder, flywheel, balancer);
	}
}