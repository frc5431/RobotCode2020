package frc.robot.components;

import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.util.*;
import frc.robot.util.states.FeederStateTeleop;
import frc.team5431.titan.core.robot.Component;

import static edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putNumber;
import static edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putString;

import java.util.List;

import edu.wpi.first.cameraserver.CameraServer;

import static edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.putData;
import static edu.wpi.first.wpilibj.smartdashboard.SmartDashboard.getNumber;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 * @author Ryan Hirasaki
 * @author Daniel Brubaker
 * @author Colin Wong
 */
public class Dashboard extends Component<Robot> {

    private final SendableChooser<DriveTypeSelector> driveType = new SendableChooser<>();

    public Dashboard() {

        /*
         * Add selection for which drive code to use as there are differences in
         * preference
         */

        CameraServer.getInstance().startAutomaticCapture();

        driveType.setDefaultOption("Tank Drive", DriveTypeSelector.ARCADE);
        driveType.addOption("Arcade Drive", DriveTypeSelector.ARCADE);
        putData("Drive Type", driveType);
        putString("Feeder State", FeederStateTeleop.LOAD.toString());
        putNumber("Shooter Speed", Constants.SHOOTER_FLYWHEEL_DEFAULT_SPEED);
        putNumber("Shooter RPM", 0.0);
        putNumber("Shooter Guessed Speed", 0.0);
        putNumber("Feeder Speed", Constants.SHOOTER_FEEDER_DEFAULT_SPEED);
        putNumber("Elevator Position", 0.0);
        putNumber("Drivebase Ramping", Constants.DRIVEBASE_DEFAULT_RAMPING);
        putNumber("Hopper Speed", Constants.HOPPER_DEFAULT_SPEED);
        putNumber("Intake Speed", Constants.INTAKE_DEFAULT_SPEED);
        putNumber("Feeder Sensor Range", 0);
        putNumber("Feeder Sensor Timestamp", 0);
        putNumber("Shooter Sensor Range", 0);
        putNumber("Shooter Sensor Timestamp", 0);
        putNumber("Feed Encoder Position", 0);
        putNumber("Ball Count", 0);
    }

    @Override
    public void init(Robot robot) {
    }

    @Override
    public void periodic(Robot robot) {
        // Setting Data from dashboard
        robot.getFlywheel().setShooterSpeed(getNumber("Shooter Speed", Constants.SHOOTER_FLYWHEEL_DEFAULT_SPEED));
        robot.getFeeder().setFeedSpeedPreset(getNumber("Feeder Speed", Constants.SHOOTER_FEEDER_DEFAULT_SPEED));
        robot.getHopper().setHopperSpeed(getNumber("Hopper Speed", Constants.HOPPER_DEFAULT_SPEED));
        robot.getDrivebase().setRamping(getNumber("Drivebase Ramping", Constants.DRIVEBASE_DEFAULT_RAMPING));
        robot.getIntake().setIntakeSpeed(getNumber("Intake Speed", Constants.INTAKE_DEFAULT_SPEED));

        // Push data to dashboard
        putString("Mode", robot.getMode().toString());
        putString("Feeder State", robot.getFeeder().getState().toString());
        putNumber("Drivebase Heading", robot.getDrivebase().getHeading());
        putNumber("Elevator Position", robot.getElevator().getRotations());
        putNumber("Shooter RPM", robot.getFlywheel().getEncoderPosition());
        putNumber("Shooter Guessed Speed", robot.getFlywheel().getFlywheelSpeed());
        putNumber("Limelight X", robot.getVision().getFrontLimelight().getX());
        putNumber("Limelight Y", robot.getVision().getFrontLimelight().getTable().getEntry("ty").getDouble(0.0));
        putNumber("Feed Encoder Position", robot.getFeeder().getFeedEncoderCount());
        putNumber("Ball Count", robot.getFeeder().getBallCount());

        Sensors.getDioSensors().forEach((sensor) -> putString("DIO Sensor " + sensor.getChannel(), sensor.get() ? "true" : "false"));
    }

    @Override
    public void disabled(Robot robot) {
    }

    @Override
    public void tick(Robot robot) {
    }

    public DriveTypeSelector getSelectedDriveType() {
        return driveType.getSelected();
    }
}