package frc.robot.commands;

import java.util.Set;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.subsystems.*;

public class PauseCommand extends CommandBase {
    Set<Subsystem> requirements;

    public PauseCommand(CommandBase command) {
        this.requirements = command.getRequirements();

        m_requirements.addAll(requirements); // Manual addition of requirements due to Set
    }

    @Override
    public void initialize() {
        requirements.forEach((system) -> {
            system.getCurrentCommand().cancel();
            switch (system.getClass().getName()) {
                case "Balancer":
                    new BalancerCommand((Balancer) system, false).end(false);
                    break;
                // case "Elevator":
                //     new ElevatorCommand((Elevator) system, false);
                //     break;
                case "Feeder":
                    new FeederCommand((Feeder) system, 0);
                    break;
                case "Flywheel":
                    new FlywheelControl((Flywheel) system, Flywheel.Speeds.OFF);
                    break;
                case "Hopper":
                    new HopperCommand((Hopper) system, 0);
                    break;
                case "Intake":
                    new IntakeCommand((Intake) system, 0);
                    break;
                default:
                    break;
            }
        });
    }
}