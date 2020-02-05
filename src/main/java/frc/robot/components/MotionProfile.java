package frc.robot.components;

import com.ctre.phoenix.motion.*;
import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.util.PathArrays;
import frc.team5431.titan.core.robot.Component;

public class MotionProfile extends Component<Robot> {

    int _state = 1;

    /** new class type in 2019 for holding MP buffer. */
    BufferedTrajectoryPointStream _bufferedStreamLeft = new BufferedTrajectoryPointStream();
    BufferedTrajectoryPointStream _bufferedStreamRight = new BufferedTrajectoryPointStream();
    
    @Override
    public void init(final Robot robot) {
        initBuffer(PathArrays.path_full_vLeft(), PathArrays.path_full_vRight(), PathArrays.numPoints);
    }

    @Override
    public void periodic(final Robot robot) {
        switch (_state) {
            case 1:
            /* wait for 10 points to buffer in firmware, then transition to MP */
                robot.getDrivebase().startMotionProfile(_bufferedStreamLeft, _bufferedStreamRight, 10, ControlMode.MotionProfile);
                _state = 2;
                System.out.println("MP started");
                break;

            /* wait for MP to finish */
            case 2:
                if (robot.getDrivebase().isMotionProfileFinished()) {
                    System.out.println("MP finished");
                    _state = 3;
                }
                break;

            /* MP is finished, nothing to do */
            default:
                break;
        }
    }

    @Override
    public void disabled(final Robot robot) {
    }

    /**
     * Fill _bufferedStreams with points from csv/generated-table.
     *
     * @param profileLeft  generated array for the left side
     * @param profileRight  generated array for the right side
     * @param totalCnt number of points in profile
     */
    private void initBuffer(double[][] profileLeft, double[][] profileRight, int totalCnt) {

        boolean forward = true; // set to false to drive in opposite direction of profile (not really needed
                                // since you can use negative numbers in profile).

        TrajectoryPoint pointLeft = new TrajectoryPoint();
        TrajectoryPoint pointRight = new TrajectoryPoint();

        /* clear the buffer, in case it was used elsewhere */
        _bufferedStreamLeft.Clear();
        _bufferedStreamRight.Clear();

        /* Insert every point into buffer, no limit on size */
        for (int i = 0; i < totalCnt; ++i) {

            double direction = forward ? +1 : -1;
            double positionLeft = profileLeft[i][0];
            double positionRight = profileRight[i][0];
            double velocityLeft = profileLeft[i][1];
            double velocityRight = profileRight[i][1];
            int durationMillisecondsLeft = (int) profileLeft[i][2];
            int durationMillisecondsRight = (int) profileRight[i][2];

            /* for each point, fill our structure and pass it to API */
            pointLeft.timeDur = durationMillisecondsLeft;
            pointLeft.position = direction * positionLeft;
            pointLeft.velocity = direction * velocityLeft / 600.0;
            pointLeft.auxiliaryPos = 0;
            pointLeft.auxiliaryVel = 0;
            pointLeft.profileSlotSelect0 = Constants.DRIVEBASE_MOTIONMAGIC_DRIVE_SLOT;
            pointLeft.profileSlotSelect1 = 0; /* auxiliary PID [0,1], leave zero */
            pointLeft.zeroPos = (i == 0); /* set this to true on the first point */
            pointLeft.isLastPoint = ((i + 1) == totalCnt); /* set this to true on the last point */
            pointLeft.arbFeedFwd = 0; /* you can add a constant offset to add to PID[0] output here */

            pointRight.timeDur = durationMillisecondsRight;
            pointRight.position = direction * positionRight;
            pointRight.velocity = direction * velocityRight / 600.0;
            pointRight.auxiliaryPos = 0;
            pointRight.auxiliaryVel = 0;
            pointRight.profileSlotSelect0 = Constants.DRIVEBASE_MOTIONMAGIC_DRIVE_SLOT;
            pointRight.profileSlotSelect1 = 0; /* auxiliary PID [0,1], leave zero */
            pointRight.zeroPos = (i == 0); /* set this to true on the first point */
            pointRight.isLastPoint = ((i + 1) == totalCnt); /* set this to true on the last point */
            pointRight.arbFeedFwd = 0; /* you can add a constant offset to add to PID[0] output here */

            _bufferedStreamLeft.Write(pointLeft);
            _bufferedStreamRight.Write(pointRight);
        }
    }
}