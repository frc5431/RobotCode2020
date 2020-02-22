package frc.robot.components;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.util.ComponentControlMode;
import frc.team5431.titan.core.misc.Toggle;
import frc.team5431.titan.core.robot.Component;

public class Hopper extends Component<Robot> {

    WPI_TalonSRX hopperLeft, hopperRight;

    Toggle hopperToggle, reverseToggle;
    double hopperSpeed = 1.0; // TODO: Fine-tune hopper speed

    private ComponentControlMode controlMode = ComponentControlMode.MANUAL;

    public Hopper() {
        hopperLeft = new WPI_TalonSRX(Constants.HOPPER_LEFT_ID);
        hopperRight = new WPI_TalonSRX(Constants.HOPPER_RIGHT_ID);

        hopperLeft.setInverted(Constants.HOPPER_REVERSE);
        hopperRight.setInverted(!Constants.HOPPER_REVERSE);

        hopperLeft.setNeutralMode(Constants.HOPPER_NEUTRALMODE);
        hopperRight.setNeutralMode(Constants.HOPPER_NEUTRALMODE);

        hopperToggle = new Toggle();
        hopperToggle.setState(false);

        reverseToggle = new Toggle();
        reverseToggle.setState(false);
    }

    @Override
    public void init(final Robot robot) {
    }

    @Override
    public void periodic(final Robot robot) {
        if (hopperToggle.getState()) {
            if (!reverseToggle.getState()) {
                hopperLeft.set(hopperSpeed+0.1);
                hopperRight.set(hopperSpeed-0.1);
            } else {
                hopperLeft.set(-hopperSpeed+0.1);
                hopperRight.set(-hopperSpeed-0.1);
            }
        } else {
            hopperLeft.set(0);
            hopperRight.set(0);
        }
    }

    @Override
    public void disabled(final Robot robot) {
    }

    public Toggle getHopperToggle() {
        return hopperToggle;
    }

    public double getHopperSpeed() {
        return hopperSpeed;
    }

    public void setHopperSpeed(double hopperSpeed) {
        this.hopperSpeed = hopperSpeed;
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
     * @return the reverseToggle
     */
    public Toggle getReverse() {
        return reverseToggle;
    }
}