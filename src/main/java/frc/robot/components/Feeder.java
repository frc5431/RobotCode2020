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
import frc.robot.util.states.FeederStateTeleop;
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

    FeederStateTeleop _state;

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

        _state = FeederStateTeleop.LOAD;
    }

    @Override
    public void periodic(Robot robot) {
        if (robot.getMode() != Mode.DISABLED) {
            ballUpdate();

            /*
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
            } else if (ballCount <= 3 && feedSpeed >= 0 && System.currentTimeMillis() < ballStopTime) { // LOADING
                feed.set(Constants.SHOOTER_FEEDER_DEFAULT_SPEED);
                controlMode = ComponentControlMode.AUTO;

            } else if (!shooting && ballCount >= 3) { // Initializes times.
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
            */

            switch (_state) {
                case LOAD:
                    // Run the feeder for a certain amount of time after it detects a ball entering.
                    if (System.currentTimeMillis() < ballStopTime) { 
                        if (ballCount < 3) {
                            feed.set(Constants.SHOOTER_FEEDER_DEFAULT_SPEED);
                            controlMode = ComponentControlMode.AUTO;
                        } else {
                            // Runs if there are three balls.
                            feed.set(feedSpeed);
                        }
                    } else {
                        // Waits for another ball to load.
                        feed.set(feedSpeed);
                        controlMode = ComponentControlMode.MANUAL;
                    }
                    // After it loads three balls, it will continue to the next stage.
                    if (ballCount >= 3) {
                        _state = FeederStateTeleop.COMPRESS;
                        upStopTime = System.currentTimeMillis() + Constants.SHOOTER_FEEDER_UP_DELAY;
                    }
                    break;
                case COMPRESS:
                    // Move on after UP_DELAY ms.
                    if (System.currentTimeMillis() < upStopTime) {
                        // Move the feeder up.
                        feed.set(Constants.SHOOTER_FEEDER_DEFAULT_SPEED);
                        controlMode = ComponentControlMode.AUTO;
                    } else {
                        _state = FeederStateTeleop.AUTO_REVERSE;
                        finalStopTime = System.currentTimeMillis() + Constants.SHOOTER_FEEDER_DOWN_DELAY;
                    }
                    break;
                case AUTO_REVERSE:
                    // Move on after DOWN_DELAY ms OR the balls clear the shoot sensor.
                    if (System.currentTimeMillis() < finalStopTime && shootSensor.getRange() < Constants.SHOOTER_SENSOR_DEFAULT) {
                        // Reverse the feeder.
                        feed.set(-Constants.SHOOTER_FEEDER_DEFAULT_SPEED);
                        controlMode = ComponentControlMode.AUTO;
                    } else {
                        _state = FeederStateTeleop.READY;
                    }
                    break;
                case READY:
                    // Ready to shoot.
                    // Will not load until all balls have been emptied.
                    if (ballCount > 0) {
                        feed.set(feedSpeed);
                        controlMode = ComponentControlMode.MANUAL;
                    } else {
                        _state = FeederStateTeleop.LOAD;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void disabled(Robot robot) {
    }

    public void ballUpdate() {
        // ballCount incrementer - only increment if you see the ball after not seeing it.
        if (!ballSeen && feedSensor.getRange() < Constants.FEEDER_SENSOR_DEFAULT) {
            ballCount++;
            ballSeen = true;
            ballStopTime = System.currentTimeMillis() + Constants.SHOOTER_FEEDER_BALL_DELAY;
        }

        if (ballSeen && feedSensor.getRange() > Constants.FEEDER_SENSOR_DEFAULT) {
            ballSeen = false;
        }

        // ballCount decrementer - only decrement if you see the ball after not seeing it.
        if (!shootSeen && shooting && shootSensor.getRange() < Constants.SHOOTER_SENSOR_DEFAULT) {
            ballCount--;
            shootSeen = true;
            // Quick ballCount fixer - ballCount should never be negative.
            if (ballCount < 0) ballCount = 0;
        }

        if (shootSeen && shooting && shootSensor.getRange() > Constants.SHOOTER_SENSOR_DEFAULT) {
            shootSeen = false;
        }
    }

    public Toggle getFeedToggle() {
        return feedToggle;
    }

    /**
     * Sets the feed speed to a preset.
     * @param feedSpeed The preset to use for the feed speed. 0 is stopped, and any number can be passed. The method will check for the feed state and adjust to a preset accordingly, so only the polarity of the number can be changed.
     */
    public void setFeedSpeedPreset(double feedSpeed) {
        if (feedSpeed == 0) {
            this.feedSpeed = 0;
            return;
        }

        boolean isNegative = Math.abs(feedSpeed) != feedSpeed;
        boolean isReady = _state == FeederStateTeleop.READY;
        double tempSpeed = isReady ? Constants.SHOOTER_FEEDER_SHOOT_SPEED : Constants.SHOOTER_FEEDER_DEFAULT_SPEED;
        
        this.feedSpeed = isNegative ? -tempSpeed : tempSpeed;
    }

    /**
     * Sets the feed speed directly. Is used in teleop mode where manual is allowed.
     * @deprecated
     * @param feedSpeed The feed speed to use.
     */
    public void setFeedSpeedOverride(double feedSpeed) {
        this.feedSpeed = feedSpeed;
    }

    /**
     * Runs the feed. MAY HAVE UNINTENDED RESULTS
     * @param feedSpeed The speed of the motor to set.
     */
    public void runFeed(double feedSpeed) {
        feed.set(feedSpeed);
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

    public FeederStateTeleop getState() {
        return _state;
    }
}