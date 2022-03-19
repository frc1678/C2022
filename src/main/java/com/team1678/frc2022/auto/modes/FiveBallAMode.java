package com.team1678.frc2022.auto.modes;

import java.util.List;

import com.team1678.frc2022.Constants;
import com.team1678.frc2022.ShuffleBoardInteractions;
import com.team1678.frc2022.auto.AutoModeEndedException;
import com.team1678.frc2022.auto.AutoTrajectoryReader;
import com.team1678.frc2022.auto.actions.LambdaAction;
import com.team1678.frc2022.auto.actions.RaceAction;
import com.team1678.frc2022.auto.actions.SeriesAction;
import com.team1678.frc2022.auto.actions.SwerveTrajectoryAction;
import com.team1678.frc2022.auto.actions.WaitAction;
import com.team1678.frc2022.subsystems.Superstructure;
import com.team1678.frc2022.subsystems.Swerve;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FiveBallAMode extends AutoModeBase {

    // Swerve instance
    private final Swerve mSwerve = Swerve.getInstance();
    private final Superstructure mSuperstructure = Superstructure.getInstance();

    // required PathWeaver file paths
    String file_path_a = "paths/FiveBallPaths/5 Ball A-A.path";
    String file_path_b = "paths/FiveBallPaths/5 Ball A-B.path";
    String file_path_c = "paths/FiveBallPaths/5 Ball A-C.path";
    String file_path_d = "paths/FiveBallPaths/5 Ball A-D.path";
    String file_path_e = "paths/FiveBallPaths/5 Ball A-E.path";


    // trajectories
    private Trajectory traj_path_a;
    private Trajectory traj_path_b;
    private Trajectory traj_path_c;
    private Trajectory traj_path_d;
    private Trajectory traj_path_e;

    // trajectory actions
    SwerveTrajectoryAction driveToIntakeSecondShotCargo;
    SwerveTrajectoryAction driveToThirdShotCargo;
    SwerveTrajectoryAction driveToIntakeThirdShotCargo;
    SwerveTrajectoryAction driveToIntakeAtTerminal;
    SwerveTrajectoryAction driveToSecondShotPose;

    public FiveBallAMode() {

        SmartDashboard.putBoolean("Auto Finished", false);

        // define theta controller for robot heading
        var thetaController = new ProfiledPIDController(Constants.AutoConstants.kPThetaController, 0, 0,
                Constants.AutoConstants.kThetaControllerConstraints);
        thetaController.enableContinuousInput(-Math.PI, Math.PI);

        /* CREATE TRAJECTORIES FROM FILES */

        // Intake second cargo
        traj_path_a = AutoTrajectoryReader.generateTrajectoryFromFile(file_path_a,
                Constants.AutoConstants.createConfig(
                        Constants.AutoConstants.kMaxSpeedMetersPerSecond, 
                        Constants.AutoConstants.kMaxAccelerationMetersPerSecondSquared,
                        0.0, 
                        0.0)
                );

        driveToIntakeSecondShotCargo = new SwerveTrajectoryAction(traj_path_a,
                mSwerve::getPose, Constants.SwerveConstants.swerveKinematics,
                new PIDController(Constants.AutoConstants.kPXController, 0, 0),
                new PIDController(Constants.AutoConstants.kPYController, 0, 0),
                thetaController,
                () -> Rotation2d.fromDegrees(265.0),
                mSwerve::getWantAutoVisionAim,
                mSwerve::setModuleStates);

        // Drive to lineup to third cargo 
        traj_path_b = AutoTrajectoryReader.generateTrajectoryFromFile(file_path_b,
                Constants.AutoConstants.createConfig(
                        Constants.AutoConstants.kMaxSpeedMetersPerSecond, 
                        Constants.AutoConstants.kMaxAccelerationMetersPerSecondSquared, 
                        0.0, 
                        Constants.AutoConstants.kMaxSpeedMetersPerSecond
                )
        );

        driveToThirdShotCargo = new SwerveTrajectoryAction(traj_path_b,
                mSwerve::getPose, Constants.SwerveConstants.swerveKinematics,
                new PIDController(Constants.AutoConstants.kPXController, 0, 0),
                new PIDController(Constants.AutoConstants.kPYController, 0, 0),
                thetaController,
                () -> Rotation2d.fromDegrees(200.0),
                mSwerve::getWantAutoVisionAim,
                mSwerve::setModuleStates);

        // Intake third cargo
        traj_path_c = AutoTrajectoryReader.generateTrajectoryFromFile(file_path_c,
                Constants.AutoConstants.createConfig(
                        Constants.AutoConstants.kMaxSpeedMetersPerSecond,
                        Constants.AutoConstants.kMaxAccelerationMetersPerSecondSquared,
                        Constants.AutoConstants.kMaxSpeedMetersPerSecond,
                        0.0
                )
        );

        driveToIntakeThirdShotCargo = new SwerveTrajectoryAction(traj_path_c,
                mSwerve::getPose, Constants.SwerveConstants.swerveKinematics,
                new PIDController(Constants.AutoConstants.kPXController, 0, 0),
                new PIDController(Constants.AutoConstants.kPYController, 0, 0),
                thetaController,
                () -> Rotation2d.fromDegrees(200.0),
                mSwerve::getWantAutoVisionAim,
                mSwerve::setModuleStates);

        // Drive to intake 4th cargo at terminal
        traj_path_d = AutoTrajectoryReader.generateTrajectoryFromFile(file_path_d,
                Constants.AutoConstants.createConfig(
                        4.0, 
                        Constants.AutoConstants.kMaxAccelerationMetersPerSecondSquared, 
                        0.0, 
                        0.0)
                );

        driveToIntakeAtTerminal = new SwerveTrajectoryAction(traj_path_d,
                mSwerve::getPose, Constants.SwerveConstants.swerveKinematics,
                new PIDController(Constants.AutoConstants.kPXController, 0, 0),
                new PIDController(Constants.AutoConstants.kPYController, 0, 0),
                thetaController,
                () -> Rotation2d.fromDegrees(225.0),
                mSwerve::getWantAutoVisionAim,
                mSwerve::setModuleStates);

        // Drive to second shot
        traj_path_e = AutoTrajectoryReader.generateTrajectoryFromFile(file_path_e,
                Constants.AutoConstants.createConfig(
                        4.0, 
                        Constants.AutoConstants.kSlowAccelerationMetersPerSecondSquared, 
                        0.0, 
                        0.0)        
                );

        driveToSecondShotPose = new SwerveTrajectoryAction(traj_path_e,
                mSwerve::getPose, Constants.SwerveConstants.swerveKinematics,
                new PIDController(Constants.AutoConstants.kPXController, 0, 0),
                new PIDController(Constants.AutoConstants.kPYController, 0, 0),
                thetaController,
                () -> Rotation2d.fromDegrees(210.0),
                mSwerve::getWantAutoVisionAim,
                mSwerve::setModuleStates);
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        System.out.println("Running five ball mode a auto!");
        SmartDashboard.putBoolean("Auto Finished", false);

        // reset odometry at the start of the trajectory
        runAction(new LambdaAction(() -> mSwerve.resetOdometry(driveToIntakeSecondShotCargo.getInitialPose())));

        // start vision aiming to align drivetrain to target
        runAction(new LambdaAction(() -> mSwerve.setWantAutoVisionAim(true)));

        runAction(new RaceAction(
                new SeriesAction(List.of(
                                        new WaitAction(0.1),
                                        driveToIntakeSecondShotCargo
                                )),
                
                new SeriesAction(List.of(
                        new WaitAction(0.2),
                        new LambdaAction(() -> mSuperstructure.setWantPrep(true)),
                        new LambdaAction(() -> mSuperstructure.setWantIntake(true))
                )
        )));

        // shoot first & second cargo
        runAction(new LambdaAction(() -> mSuperstructure.setWantShoot(true)));
        runAction(new WaitAction(1.0));
        runAction(new LambdaAction(() -> mSuperstructure.setWantShoot(false)));

        // stop vision aiming to control robot heading
        runAction(new LambdaAction(() -> mSwerve.setWantAutoVisionAim(false)));

        // run trajectory for third cargo
        runAction(driveToThirdShotCargo);

        // vision aim when intaking third cargo
        runAction(new LambdaAction(() -> mSwerve.setWantAutoVisionAim(true)));

        // run trajectory to intake third shot cargo
        runAction(driveToIntakeThirdShotCargo);

        // shoot third cargo
        runAction(new LambdaAction(() -> mSuperstructure.setWantShoot(true)));
        runAction(new WaitAction(0.5));
        runAction(new LambdaAction(() -> mSuperstructure.setWantShoot(false)));

        // stop vision aiming to control robot heading
        runAction(new LambdaAction(() -> mSwerve.setWantAutoVisionAim(false)));

        // run trajectory for terminal
        runAction(driveToIntakeAtTerminal);

        // start vision aiming when driving to shot pose
        runAction(new LambdaAction(() -> mSwerve.setWantAutoVisionAim(true)));

        // run trajectory to drive to second shot pose
        runAction(driveToSecondShotPose);

        // shoot cargo
        runAction(new LambdaAction(() -> mSuperstructure.setWantShoot(true)));
        runAction(new WaitAction(1.0));
        runAction(new LambdaAction(() -> mSuperstructure.setWantShoot(false)));

        // stop vision aiming to control robot heading
        runAction(new LambdaAction(() -> mSwerve.setWantAutoVisionAim(false)));

        System.out.println("Finished auto!");
        SmartDashboard.putBoolean("Auto Finished", true);
    }

    public void plotTrajectories() {
        ShuffleBoardInteractions.getInstance().addTrajectory(traj_path_a, "Traj A");
        ShuffleBoardInteractions.getInstance().addTrajectory(traj_path_b, "Traj B");
        ShuffleBoardInteractions.getInstance().addTrajectory(traj_path_c, "Traj C");
        ShuffleBoardInteractions.getInstance().addTrajectory(traj_path_d, "Traj D");
        ShuffleBoardInteractions.getInstance().addTrajectory(traj_path_e, "Traj E");
    }

}
