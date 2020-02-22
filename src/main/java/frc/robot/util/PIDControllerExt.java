package frc.robot.util;

import edu.wpi.first.wpilibj.controller.PIDController;

/**
 * PID Controller Extension
 */
public class PIDControllerExt extends PIDController {
	/**
	 * @param constants The arguments for the PID Controller. Accepts in array form. {kP, kI, kD}
	 */
	public PIDControllerExt(Double... constants) {
		super(constants[0], constants[1], constants[2]);
	}
}