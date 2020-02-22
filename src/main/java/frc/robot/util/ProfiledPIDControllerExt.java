package frc.robot.util;

import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;

/**
 * Profiled PID Controller Extension
 * @author Colin Wong
 */
public class ProfiledPIDControllerExt extends ProfiledPIDController {
	/**
	 * @param args The arguments for the controller. Accepts in array form. {kP, kI, kD, maxAcceleration, maxVelocity}
	 */
	public ProfiledPIDControllerExt(Double... args) {
		super(args[0], args[1], args[2], new TrapezoidProfile.Constraints(args[3], args[4]));
	}
}