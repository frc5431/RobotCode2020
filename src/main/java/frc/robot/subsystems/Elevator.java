package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.states.ClimberState;

public class Elevator extends SubsystemBase {

    private WPI_TalonFX elevator;

    public Elevator() {
        elevator = new WPI_TalonFX(Constants.CLIMBER_ELEVATOR_ID);
        elevator.setInverted(Constants.CLIMBER_ELEVATOR_REVERSE);
        elevator.setNeutralMode(NeutralMode.Brake);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Elevator Position", elevator.getSelectedSensorPosition());
    }

    public void setPosition(ClimberState position) {
        elevator.set(ControlMode.Position, position.getPosition());
    }

    public void setSpeed(double speed) {
        elevator.set(ControlMode.PercentOutput, speed);
    }

    public double getEncoderPosition() {
        return elevator.getSelectedSensorPosition();
    }

    public double getRotations() {
        return getEncoderPosition() / 2048;
    }
}