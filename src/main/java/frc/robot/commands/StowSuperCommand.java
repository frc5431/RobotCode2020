package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.*;
import frc.robot.util.states.ClimberState;

public class StowSuperCommand extends SequentialCommandGroup {
	public StowSuperCommand(Intake intake, Hopper hopper, Feeder feeder, Flywheel flywheel, Elevator elevator, Balancer balancer) {
		addCommands(
			new PivotCommand(intake, Intake.POSITION.UP) //,
			// new ElevatorCommand(elevator, ClimberState.TOP) // FIXME
		);

		addRequirements(intake, hopper, feeder, flywheel, balancer);
	}
}