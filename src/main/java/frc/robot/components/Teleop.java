package frc.robot.components;

import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.Robot.Mode;
import frc.team5431.titan.core.joysticks.LogitechExtreme3D;
import frc.team5431.titan.core.joysticks.Xbox;
import frc.team5431.titan.core.misc.Logger;
import frc.team5431.titan.core.misc.Toggle;
import frc.team5431.titan.core.robot.Component;

public class Teleop extends Component<Robot> {

    private Xbox driver;
    private LogitechExtreme3D operator;

    private Toggle pivot = new Toggle();

    private boolean warnDriver = false, warnOperator = false;

    public Teleop() {
        //Init driver controllers
        driver = new Xbox(Constants.DRIVER_XBOX_ID);
        driver.setDeadzone(Constants.DRIVER_XBOX_DEADZONE);
        
        //Init operator controller
        operator = new LogitechExtreme3D(Constants.OPERATOR_LOGITECH_ID);
        operator.setDeadzone(Constants.OPERATOR_LOGITECH_DEADZONE);

        pivot.setState(false);
    }

    @Override
    public void init(Robot robot) {
    }

    @Override
    public void periodic(Robot robot) {
        //Only run if in teleop
        if (robot.getMode() != Mode.TELEOP)
            return;

        // Run the respective controller code
        driver(robot);
        operator(robot);
    }

    @Override
    public void disabled(Robot robot) {
        //do nothing
    }

    private void driver(Robot robot) {
        //get drivebase/dashboard
        final Drivebase drivebase = robot.getDrivebase();
        final Dashboard dashboard = robot.getDashboard();

        //extra check for correct driver and constants setup
        final String driverName = driver.getName().toLowerCase();

        if (driverName.contains(Constants.DRIVER_XBOX_NAME.toLowerCase())) {

            /*
             * This code will allow the end user to chose which drive controls to use 
             * from the smart dashboard as there is a difference in preference.
             */
            switch (dashboard.getSelectedDriveType()) {
                case ARCADE:
                    double power = 0;
                    double turn = 0;
    
                    //get the current joystick values, input them into the Arcade Drive function
                    power = driver.getRawAxis(Xbox.Axis.LEFT_Y) * -1; // Set negative as xbox down is up                                                                       // foward is negative
                    turn = driver.getRawAxis(Xbox.Axis.LEFT_X) * -1; // Set negative as right is negative

                    drivebase.drivePercentageArcade(power, turn);
                    break;
                case TANK:
                    double left = 0;
                    double right = 0;
    
                    //get current joystick values, put it in the Tank Drive function
                    left = driver.getRawAxis(Xbox.Axis.LEFT_Y) * -1;
                    right = driver.getRawAxis(Xbox.Axis.RIGHT_Y) * -1;

                    drivebase.drivePercentageTank(left, right);
                    break;
                default:
                    break;
            }

            //Set intake feeder & flywheel toggles to respective buttons (X, A, B)
            //off until pressed, on until pressed again
           
            robot.getFlywheel().getFlywheelToggle().isToggled(driver.getRawButton(Xbox.Button.B));
            
            //set elevator speed to right trigger for up, left trigger for down
            //If right or left is zero it will be (Right - 0 = positive) or (0 - Left = negative)
            //This removes the need for if statements that check which is active, and simplifies up and down motion
            robot.getBalancer().setSpeed((driver.getRawButton(Xbox.Button.BUMPER_R) ? 1 : 0) - (driver.getRawButton(Xbox.Button.BUMPER_L) ? 1 : 0));
            robot.getElevator().setSpeed(driver.getRawAxis(Xbox.Axis.TRIGGER_RIGHT) - driver.getRawAxis(Xbox.Axis.TRIGGER_LEFT));
            
            //the Y button toggles the input and feeder into reverse input mode, (up is down, left is right, etc.)
            //robot.getIntake().getIntakeToggle().isToggled(driver.getRawButton(Xbox.Button.Y));
            Logger.l("Xbox Y: %b", driver.getRawButton(Xbox.Button.Y));
            // robot.getFeeder().getReverse().isToggled(driver.getRawButton(Xbox.Button.Y));

            //Sets the vision toggles to the bumpers
            robot.getVision().getTargetToggle().isToggled(operator.getRawButton(LogitechExtreme3D.Button.ELEVEN));
            // robot.getVision().getVisionLightToggle().isToggled(driver.getRawButton(Xbox.Button.BUMPER_L));

        } else {
            //warn driver if controller is not connected
            if (!warnDriver)
                Logger.e("Driver Controller Not Connected");
            warnDriver = true;
        }
    }

    private void operator(Robot robot) { 
        final String operatorName = operator.getName().toLowerCase();

        //extra check for correct operator and co nstants setup
        if (operatorName.contains(Constants.OPERATOR_LOGITECH_NAME.toLowerCase())) {

            //get the X axis and the Slider
           // double elevatorSpeed = operator.getRawAxis(LogitechExtreme3D.Axis.Y);
            //double balancerSpeed = operator.getRawAxis(LogitechExtreme3D.Axis.X);

            //set elevator and balancer speeds
            //robot.getElevator().setSpeed(elevatorSpeed);
            //robot.getBalancer().setSpeed(balancerSpeed);
            
            //set flywheel run when joystick trigger
            robot.getFlywheel().getFlywheelToggle().setState(operator.getRawButton(LogitechExtreme3D.Button.TRIGGER));
            
            robot.getIntake().setPivotSpeed((operator.getRawButton(8) ? 0.2 : 0) - (operator.getRawButton(7) ? 0.1 : 0));

            robot.getFeeder().setFeedSpeed((operator.getPOV() == 0 ? 1.0 : 0) - (operator.getPOV() == 180 ? 1.0 : 0));

            robot.getHopper().getHopperToggle().setState(operator.getRawButton(LogitechExtreme3D.Button.THREE));

            robot.getHopper().getReverse().setState(operator.getRawButton(LogitechExtreme3D.Button.FIVE));

            if (operator.getRawButton(LogitechExtreme3D.Button.TEN))
            {
                robot.getIntake().setIntakeSpeed(Constants.INTAKE_DEFAULT_SPEED);
            } else {
                robot.getIntake().setIntakeSpeed(0);
            }
            // if (driver.getRawButton(Xbox.Button.X))
            // {
            //     robot.getIntake().setIntakeSpeed(Constants.INTAKE_DEFAULT_SPEED);
            // }

           

            // robot.getIntake().getReverse().setState(operator.getRawButton(LogitechExtreme3D.Button.TEN));

        } else {
            //warn operator if controller is not connected
            if (!warnOperator)
                Logger.e("Operator Controller Not Connected");
            warnOperator = true;
        }
    }


	public Xbox getDriver() {
		return driver;
	}
}