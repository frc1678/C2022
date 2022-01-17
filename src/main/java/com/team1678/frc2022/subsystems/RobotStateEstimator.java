package com.team1678.frc2022.subsystems;

import com.team1678.frc2022.Constants;
import com.team1678.frc2022.RobotState;
import com.team1678.frc2022.loops.ILooper;
import com.team1678.frc2022.loops.Loop;

import com.team254.lib.geometry.Pose2d;
import com.team254.lib.geometry.Rotation2d;
import com.team254.lib.geometry.Translation2d;

import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class RobotStateEstimator extends Subsystem {

    // required class instances
    static RobotStateEstimator mInstance = new RobotStateEstimator();
    private RobotState mRobotState = RobotState.getInstance();
    private Swerve mSwerve = Swerve.getInstance();

    // status variables
    private double prev_timestamp_ = -1.0;
    private Pose2d prev_swerve_pose_ = null;
    private Pose2d prev_swerve_velocity = new Pose2d();

    public static RobotStateEstimator getInstance() {
        if (mInstance == null) {
            mInstance = new RobotStateEstimator();
        }

        return mInstance;
    }

    private RobotStateEstimator() {
    }

    @Override
    public void registerEnabledLoops(ILooper looper) {
        looper.register(new EnabledLoop());
    }

    private class EnabledLoop implements Loop {
        @Override
        public synchronized void onStart(double timestamp) {
            prev_timestamp_ = timestamp;
        }

        @Override
        public synchronized void onLoop(double timestamp) {
            if (prev_swerve_pose_ == null) {
                prev_swerve_pose_ = mRobotState.getLatestFieldToVehicle().getValue();
            }

            Pose2d swerve_pose_ = new Pose2d(mSwerve.getPose());

            final double dt = timestamp - prev_timestamp_;
            final Translation2d latest_translational_displacement = new Translation2d(prev_swerve_pose_.getTranslation(), swerve_pose_.getTranslation());
            final Rotation2d latest_rotational_displacement = prev_swerve_pose_.getRotation().inverse().rotateBy(swerve_pose_.getRotation());

            ChassisSpeeds chassisVelocity = Constants.SwerveConstants.swerveKinematics.toChassisSpeeds(
                    mSwerve.mSwerveMods[0].getState(),
                    mSwerve.mSwerveMods[1].getState(),
                    mSwerve.mSwerveMods[2].getState(),
                    mSwerve.mSwerveMods[3].getState()
            );

            Pose2d odometry_delta;
            synchronized (mRobotState) {
                odometry_delta = new Pose2d(latest_translational_displacement, latest_rotational_displacement);
            }

            final Pose2d measured_velocity = odometry_delta.scaled(1.0 / dt);
            final Pose2d current_velocity = new Pose2d(chassisVelocity.vxMetersPerSecond,
                                                       chassisVelocity.vyMetersPerSecond,
                                                       new Rotation2d(chassisVelocity.omegaRadiansPerSecond)
                                                       );
            final Pose2d latest_velocity_acceleration = prev_swerve_velocity.inverse().transformBy(current_velocity).scaled(1.0 / dt);
            final Pose2d predicted_velocity = measured_velocity.transformBy(latest_velocity_acceleration.scaled(dt));

            mRobotState.addObservations(timestamp, odometry_delta, measured_velocity, predicted_velocity);

            prev_swerve_pose_ = swerve_pose_;
            prev_timestamp_ = timestamp;
        }

        @Override
        public void onStop(double timestamp) {
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean checkSystem() {
        return true;
    }
    
}
