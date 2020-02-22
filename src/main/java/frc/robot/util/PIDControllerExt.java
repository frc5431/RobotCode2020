package frc.robot.util;

import edu.wpi.first.wpilibj.controller.PIDController;

public class PIDControllerExt extends PIDController {
	public PIDControllerExt(Double... constants) {
		super(constants[0], constants[1], constants[2]);
	}
}