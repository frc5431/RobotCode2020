package frc.robot.util;

import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;

public class ProfiledPIDControllerExt extends ProfiledPIDController {
	public ProfiledPIDControllerExt(Double... args) {
		super(args[0], args[1], args[2], new TrapezoidProfile.Constraints(args[3], args[4]));
	}
}