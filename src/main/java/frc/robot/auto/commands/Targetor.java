package frc.robot.auto.commands;

import frc.robot.Robot;
import frc.robot.auto.vision.TargetType;
import frc.team5431.titan.core.robot.Command;
import frc.team5431.titan.core.vision.LEDState;
import frc.team5431.titan.core.vision.Limelight;

public class Targetor extends Command<Robot> {

    private Limelight front;
    private final TargetType target;

    public Targetor(TargetType target) {
        this.target = target;

        this.name = "Targetor";
    }

    public Targetor(TargetType target, Limelight front) {
        this(target);

        this.front = front;
    }

    @Override
    public void init(Robot robot) {
        front = robot.getVision().getFrontLimelight();

        front.setLEDState(LEDState.ON);

        // TODO: add get and set pipeline in TitanUtil
        front.getTable().getEntry("pipeline").setNumber(target.getPipeline());
    }

    @Override
    public CommandResult update(Robot robot) {
        return robot.getVision().target(robot, target) ? CommandResult.COMPLETE : CommandResult.IN_PROGRESS;
    }

    @Override
    public void done(Robot robot) {
        front.setLEDState(LEDState.OFF);
    }
}