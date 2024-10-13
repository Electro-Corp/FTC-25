package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;

import org.firstinspires.ftc.teamcode.roadrunner.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;

/**
    Makes sure trajectories are linked together + provide a convenient way to
    use roadrunner
 */
public class RoadRunnerHelper<T>{

    public static final int DEFAULT_VEL = 25;
    public static final int DEFAULT_ANG_VEL = 2;
    public static final int REVERSE_FAST = 60;


    private SampleMecanumDrive drive;
    private Pose2d pose;

    private TrajectoryVelocityConstraint trajectoryVelocityConstraint;
    public RoadRunnerHelper(SampleMecanumDrive drive){
        this.drive = drive;
        pose = new Pose2d();

    }

    // [NOTE] All functions return RoadRunnerHelper so its possible
    // to chain functions together
    // (im assuming thats how roadrunner does it internally idk i havent checked)

    // Move functions

    /**
     * I dare you to guess what this does
     * @param dist
     */
    public RoadRunnerHelper forward(double dist){
        forward(dist, DEFAULT_VEL);
        return this;
    }

    public RoadRunnerHelper forward(int dist){
        forward(dist, DEFAULT_VEL);
        return this;
    }
    

    /**
     * Forward with speed
     * @param dist
     * @param speed
     */
    public RoadRunnerHelper forward(double dist, double speed){
        trajectoryVelocityConstraint = SampleMecanumDrive.getVelocityConstraint(
                speed,
                speed,
            DriveConstants.TRACK_WIDTH
        );
        TrajectorySequence traj = drive.trajectorySequenceBuilder(pose).forward(dist, trajectoryVelocityConstraint, SampleMecanumDrive.getAccelerationConstraint(speed * 2)).build();
        drive.followTrajectorySequence(traj);
        pose = traj.end();
        return this;
    }



    public RoadRunnerHelper reverse(double dist){
        reverse(dist, DEFAULT_VEL);
        return this;
    }

    public RoadRunnerHelper reverse(double dist, double speed){
        trajectoryVelocityConstraint = SampleMecanumDrive.getVelocityConstraint(
                speed,
                speed,
                DriveConstants.TRACK_WIDTH
        );
        TrajectorySequence traj = drive.trajectorySequenceBuilder(pose).back(dist, trajectoryVelocityConstraint, SampleMecanumDrive.getAccelerationConstraint(speed * 2)).build();
        drive.followTrajectorySequence(traj);
        pose = traj.end();
        return this;
    }

    /**
     * Turn to angle (positive is turning left)
     * @param angle Turn angle
     */
    public RoadRunnerHelper turn(double angle)  {
        turn(angle, DEFAULT_ANG_VEL, DEFAULT_ANG_VEL);
        return this;
    }

    /**
     * Turn to angle with speed (positive is turning left)
     * @param angle Turn angle (in degrees)
     * @param speed Rotation speed
     * @param angAcc Max angular acceleration
     */
    public RoadRunnerHelper turn(double angle, double speed, double angAcc)  {
        TrajectorySequence traj = drive.trajectorySequenceBuilder(pose).turn(Math.toRadians(angle), speed, angAcc).build();
        drive.followTrajectorySequence(traj);
        pose = traj.end();
        return this;
    }

    // Strafe
    public RoadRunnerHelper strafeRight(double dist){
        Trajectory traj = drive.trajectoryBuilder(pose).strafeRight(dist).build();
        drive.followTrajectory(traj);
        pose = traj.end();
        return this;
    }

    public RoadRunnerHelper strafeLeft(double dist){
        Trajectory traj = drive.trajectoryBuilder(pose).strafeLeft(dist).build();
        drive.followTrajectory(traj);
        pose = traj.end();
        return this;
    }

    /**
     * Spline to location
     * @param x X Location (wow)
     * @param y Y Location (crazy)
     * @param ang Angle (degrees) (wack)
     * @return
     */
    public RoadRunnerHelper splineToLinearHeading(double x, double y, double ang){
        Trajectory traj = drive.trajectoryBuilder(pose).splineToLinearHeading(new Pose2d(x, y, Math.toRadians(ang)), 0).build();
        drive.followTrajectory(traj);
        pose = traj.end();
        return this;
    }

    /**
     * Waits for seconds
     * @param seconds wait for seconds
     * @return Not important, dont worry about it
     * @throws InterruptedException oops
     */
    public RoadRunnerHelper waitSeconds(double seconds) throws InterruptedException {
        drive.wait((long) seconds);
        return this;
    }


    /**
     * Clears the current path to start a new one
     * Useful if you take manual control of the motors
     * and then want to use RoadRunner again
     */
    public RoadRunnerHelper resetPath(){
        pose = new Pose2d();
        drive.setPoseEstimate(pose);
        return this;
    }

}

