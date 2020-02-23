package frc.robot.components;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.revrobotics.Rev2mDistanceSensor;
import com.revrobotics.Rev2mDistanceSensor.Port;
import com.revrobotics.Rev2mDistanceSensor.RangeProfile;
import com.revrobotics.Rev2mDistanceSensor.Unit;

import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.Robot.Mode;
import frc.robot.util.ComponentControlMode;
import frc.team5431.titan.core.misc.Toggle;
import frc.team5431.titan.core.robot.Component;

public class Feeder extends Component<Robot> {

    WPI_TalonFX feed;
    Rev2mDistanceSensor feedSensor;
    Rev2mDistanceSensor shootSensor;

    Toggle feedToggle;

    double feedSpeed;
    int stopCount;
    boolean ballSeen, shootSeen;

    /**
     * Ball stop time = time for when the feeder should stop moving after it sees a
     * ball Up stop time = time for when the feeder should stop moving three balls
     * up Final stop time = time for when the feeder should
     */
    long finalStopTime, upStopTime, ballStopTime;

    int _state;

    int ballCount = 0;
    boolean shooting = false;

    private ComponentControlMode controlMode = ComponentControlMode.MANUAL;

    public Feeder() {

        feed = new WPI_TalonFX(Constants.SHOOTER_FEEDER_ID);

        feed.setInverted(Constants.SHOOTER_FEEDER_REVERSE);
        feed.setNeutralMode(Constants.SHOOTER_FEEDER_NEUTRALMODE);

        feedToggle = new Toggle();
        feedToggle.setState(false);

        // reverse = new Toggle();
        // reverse.setState(false);
        feedSensor = new Rev2mDistanceSensor(Port.kOnboard, Unit.kInches, RangeProfile.kHighSpeed);
        shootSensor = new Rev2mDistanceSensor(Port.kMXP, Unit.kInches, RangeProfile.kHighSpeed);
    }

    @Override
    public void init(Robot robot) {

        feedSensor = new Rev2mDistanceSensor(Port.kOnboard, Unit.kInches, RangeProfile.kHighSpeed);
        shootSensor = new Rev2mDistanceSensor(Port.kMXP, Unit.kInches, RangeProfile.kHighSpeed);

        feedSensor.setAutomaticMode(true);
        shootSensor.setAutomaticMode(true);
        resetFeedEncoder();
        ballCount = 0;
        ballSeen = true;

        _state = 0;
    }

    @Override
    public void periodic(Robot robot) {
        if (robot.getMode() != Mode.DISABLED) {
            ballUpdate();
            if (System.currentTimeMillis() < upStopTime && feedSpeed >= 0) {
                feed.set(Constants.SHOOTER_FEEDER_DEFAULT_SPEED);
                controlMode = ComponentControlMode.AUTO;
            } else if (System.currentTimeMillis() < finalStopTime) {
                if (shootSensor.getRange() < Constants.SHOOTER_SENSOR_DEFAULT) {
                    feed.set(-Constants.SHOOTER_FEEDER_DEFAULT_SPEED);
                    controlMode = ComponentControlMode.AUTO;
                } else {
                    feed.set(feedSpeed);
                    shooting = true;
                    controlMode = ComponentControlMode.MANUAL;

                }
            } else if (ballCount <= 3 && feedSpeed >= 0 && System.currentTimeMillis() < ballStopTime) {
                feed.set(Constants.SHOOTER_FEEDER_DEFAULT_SPEED);
                controlMode = ComponentControlMode.AUTO;

            } else if (!shooting && ballCount >= 3) {
                if (ballCount == 3 && upStopTime < System.currentTimeMillis()) {
                    finalStopTime = System.currentTimeMillis() + (Constants.SHOOTER_FEEDER_DOWN_DELAY);
                    upStopTime = System.currentTimeMillis() + Constants.SHOOTER_FEEDER_UP_DELAY;
                    // ballCount = 0;
                }
            } else if (ballCount <= 3 && ballCount > 0) {
                feed.set(feedSpeed);
                controlMode = ComponentControlMode.MANUAL;

            } else if (ballCount == 0) {
                shooting = false;
            } else {
                // Something went very wrong.
                feed.set(feedSpeed);
                // So set it back to manual.
                controlMode = ComponentControlMode.MANUAL;
            }
        }
    }

    @Override
    public void disabled(Robot robot) {
    }

    public void ballUpdate() {
        // ballToggle.isToggled(feedSensor.getRange() <
        // Constants.FEEDER_SENSOR_DEFAULT);
        // if (ballToggle.getState()) {
        // ballCount++;
        // }
        if (!ballSeen && feedSensor.getRange() < Constants.FEEDER_SENSOR_DEFAULT) {
            ballCount++;
            ballSeen = true;
            ballStopTime = System.currentTimeMillis() + Constants.SHOOTER_FEEDER_BALL_DELAY;
        }

        if (ballSeen && feedSensor.getRange() > Constants.FEEDER_SENSOR_DEFAULT) {
            ballSeen = false;
        }

        if (!shootSeen && shooting && shootSensor.getRange() < Constants.SHOOTER_SENSOR_DEFAULT) {
            ballCount--;
            shootSeen = true;
        }

        if (shootSeen && shooting && shootSensor.getRange() > Constants.SHOOTER_SENSOR_DEFAULT) {
            shootSeen = false;
        }
    }

    public Toggle getFeedToggle() {
        return feedToggle;
    }

    public void setFeedSpeed(double feedSpeed) {
        this.feedSpeed = feedSpeed;
    }

    public double getFeedSpeed() {
        return feedSpeed;
    }

    /**
     * @return the controlMode
     */
    public ComponentControlMode getControlMode() {
        return controlMode;
    }

    /**
     * @param controlMode the controlMode to set
     */
    public void setControlMode(ComponentControlMode controlMode) {
        this.controlMode = controlMode;
    }

    /**
     * @return the distanceSensor
     */
    public Rev2mDistanceSensor getFeedSensor() {
        return feedSensor;
    }

    /**
     * @return the shootSensor
     */
    public Rev2mDistanceSensor getShootSensor() {
        return shootSensor;
    }

    // /**
    // * @return the reverse
    // */
    // public Toggle getReverse() {
    // return reverse;
    // }

    public int getFeedEncoderCount() {
        return feed.getSelectedSensorPosition();
    }

    public void resetFeedEncoder() {
        feed.setSelectedSensorPosition(0);
    }

    public int getBallCount() {
        return ballCount;
    }
}