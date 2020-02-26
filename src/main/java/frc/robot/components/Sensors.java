package frc.robot.components;

import java.util.List;

import com.revrobotics.Rev2mDistanceSensor;
import com.revrobotics.Rev2mDistanceSensor.RangeProfile;
import com.revrobotics.Rev2mDistanceSensor.Unit;

import edu.wpi.first.wpilibj.DigitalInput;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.team5431.titan.core.robot.Component;

public class Sensors extends Component<Robot> {
    private static List<DigitalInput> dioSensors;
    // private Rev2mDistanceSensor feedSensor;
    // private Rev2mDistanceSensor shootSensor;

    public Sensors() {
        dioSensors = List.of(
            new DigitalInput(6),
            new DigitalInput(7),
            new DigitalInput(8),
            new DigitalInput(9)
        );

        // feedSensor = new Rev2mDistanceSensor(Constants.FEEDER_SENSOR_ID, Unit.kInches, RangeProfile.kHighSpeed);
        // shootSensor = new Rev2mDistanceSensor(Constants.SHOOTER_SENSOR_ID, Unit.kInches, RangeProfile.kHighSpeed);
    }

    @Override
    public void init(final Robot robot) {
        // feedSensor = new Rev2mDistanceSensor(Constants.FEEDER_SENSOR_ID, Unit.kInches, RangeProfile.kHighSpeed);
        // shootSensor = new Rev2mDistanceSensor(Constants.SHOOTER_SENSOR_ID, Unit.kInches, RangeProfile.kHighSpeed);

        // feedSensor.setAutomaticMode(true);
        // shootSensor.setAutomaticMode(true);
    }

    @Override
    public void periodic(final Robot robot) {
    }

    @Override
    public void disabled(final Robot robot) {
        dioSensors.forEach((sensor) -> sensor.close());
    }

    /**
     * @return the DIO Sensors
     */
    public static List<DigitalInput> getDioSensors() {
        return dioSensors;
    }

}