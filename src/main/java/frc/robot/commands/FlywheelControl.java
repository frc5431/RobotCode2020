package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Flywheel;

/**
 * @author Ryan Hirasaki
 */
public class FlywheelControl extends CommandBase {
    private final Flywheel flywheel;
    private final Flywheel.Speeds speed;    

    public FlywheelControl(Flywheel flywheel, Flywheel.Speeds speed) {
        this.flywheel = flywheel;
        this.speed = speed;

        addRequirements(flywheel);
    }

    @Override
    public void initialize() {
        flywheel.set(speed);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}