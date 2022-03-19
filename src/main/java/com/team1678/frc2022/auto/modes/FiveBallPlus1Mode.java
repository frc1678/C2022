package com.team1678.frc2022.auto.modes;

import com.team1678.frc2022.Constants;
import com.team1678.frc2022.auto.AutoModeEndedException;
import com.team1678.frc2022.auto.AutoTrajectoryReader;
import com.team1678.frc2022.auto.actions.LambdaAction;
import com.team1678.frc2022.auto.actions.SwerveTrajectoryAction;
import com.team1678.frc2022.auto.actions.WaitAction;
import com.team1678.frc2022.subsystems.Superstructure;
import com.team1678.frc2022.subsystems.Swerve;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FiveBallPlus1Mode extends AutoModeBase {
    
    // Swerve instance 
    private final Swerve mSwerve = Swerve.getInstance();
    private final Superstructure mSuperstructure = Superstructure.getInstance();

    // required PathWeaver file paths
    String file_path_a = "paths/FiveBallPaths/5 Ball + 1 a.path";
    String file_path_b = "paths/FiveBallPaths/5 Ball + 1 b.path";
    String file_path_c = "paths/FiveBallPaths/5 Ball + 1 c.path";
    String file_path_d = "paths/FiveBallPaths/5 Ball + 1 d.path";
    String file_path_e = "paths/FiveBallPaths/5 Ball + 1 e.path";
    String file_path_f = "paths/FiveBallPaths/5 Ball + 1 f.path";
    
	// trajectory actions
	SwerveTrajectoryAction driveToIntakeSecondShotCargo;
    SwerveTrajectoryAction driveToThirdShotCargo;
    SwerveTrajectoryAction driveToIntakeThirdShotCargo;
	SwerveTrajectoryAction driveToIntakeAtTerminal;
	SwerveTrajectoryAction driveToFourthShotPose;
    SwerveTrajectoryAction driveToEjectCargo;

    public FiveBallPlus1Mode() {

        SmartDashboard.putBoolean("Auto Finished", false);

    // define theta controller for robot heading
    var thetaController = new ProfiledPIDController(Constants.AutoConstants.kPThetaController, 0, 0,
                                                    Constants.AutoConstants.kThetaControllerConstraints);
    thetaController.enableContinuousInput(-Math.PI, Math.PI);

    // read trajectories from PathWeaver and generate trajectory actions
    Trajectory traj_path_a = AutoTrajectoryReader.generateTrajectoryFromFile(file_path_a, Constants.AutoConstants.zeroToZeroSpeedConfig);
    driveToIntakeSecondShotCargo = new SwerveTrajectoryAction(traj_path_a,
                                                        mSwerve::getPose, Constants.SwerveConstants.swerveKinematics,
                                                        new PIDController(Constants.AutoConstants.kPXController, 0, 0),
                                                        new PIDController(Constants.AutoConstants.kPYController, 0, 0),
                                                        thetaController,
                                                        () -> Rotation2d.fromDegrees(270.0),
                                                        mSwerve::getWantAutoVisionAim,
                                                        mSwerve::setModuleStates);

    Trajectory traj_path_b = AutoTrajectoryReader.generateTrajectoryFromFile(file_path_b, Constants.AutoConstants.zeroToDefaultSpeedConfig);
    driveToThirdShotCargo = new SwerveTrajectoryAction(traj_path_b,
                                                        mSwerve::getPose, Constants.SwerveConstants.swerveKinematics,
                                                        new PIDController(Constants.AutoConstants.kPXController, 0, 0),
                                                        new PIDController(Constants.AutoConstants.kPYController, 0, 0),
                                                        thetaController,
                                                        () -> Rotation2d.fromDegrees(195.0),
                                                        mSwerve::getWantAutoVisionAim,
                                                        mSwerve::setModuleStates);
    Trajectory traj_path_c = AutoTrajectoryReader.generateTrajectoryFromFile(file_path_c, Constants.AutoConstants.defaultToZeroSpeedConfig);
    driveToIntakeThirdShotCargo = new SwerveTrajectoryAction(traj_path_c,
                                                        mSwerve::getPose, Constants.SwerveConstants.swerveKinematics,
                                                        new PIDController(Constants.AutoConstants.kPXController, 0, 0),
                                                        new PIDController(Constants.AutoConstants.kPYController, 0, 0),
                                                        thetaController,
                                                        () -> Rotation2d.fromDegrees(195.0),
                                                        mSwerve::getWantAutoVisionAim,
                                                        mSwerve::setModuleStates);
    Trajectory traj_path_d = AutoTrajectoryReader.generateTrajectoryFromFile(file_path_d, Constants.AutoConstants.zeroToDefaultSpeedConfig);
    driveToIntakeAtTerminal = new SwerveTrajectoryAction(traj_path_d,
                                                        mSwerve::getPose, Constants.SwerveConstants.swerveKinematics,
                                                        new PIDController(Constants.AutoConstants.kPXController, 0, 0),
                                                        new PIDController(Constants.AutoConstants.kPYController, 0, 0),
                                                        thetaController,
                                                        () -> Rotation2d.fromDegrees(190.0),
                                                        mSwerve::getWantAutoVisionAim,
                                                        mSwerve::setModuleStates);
                                                    
    Trajectory traj_path_e = AutoTrajectoryReader.generateTrajectoryFromFile(file_path_e, Constants.AutoConstants.defaultToZeroSpeedConfig);
    driveToFourthShotPose = new SwerveTrajectoryAction(traj_path_e,
                                                        mSwerve::getPose, Constants.SwerveConstants.swerveKinematics,
                                                        new PIDController(Constants.AutoConstants.kPXController, 0, 0),
                                                        new PIDController(Constants.AutoConstants.kPYController, 0, 0),
                                                        thetaController,
                                                        () -> Rotation2d.fromDegrees(210.0),
                                                        mSwerve::getWantAutoVisionAim,
                                                        mSwerve::setModuleStates);

    Trajectory traj_path_f = AutoTrajectoryReader.generateTrajectoryFromFile(file_path_f, Constants.AutoConstants.zeroToDefaultSpeedConfig);
    driveToEjectCargo = new SwerveTrajectoryAction(traj_path_f,
                                                        mSwerve::getPose, Constants.SwerveConstants.swerveKinematics,
                                                        new PIDController(Constants.AutoConstants.kPXController, 0, 0),
                                                        new PIDController(Constants.AutoConstants.kPYController, 0, 0),
                                                        thetaController,
                                                        () -> Rotation2d.fromDegrees(310.0),
                                                        mSwerve::getWantAutoVisionAim,
                                                        mSwerve::setModuleStates);
    }

    @Override
    protected void routine() throws AutoModeEndedException {
    System.out.println("Running five ball mode a auto!");
    SmartDashboard.putBoolean("Auto Finished", false);

    // reset odometry at the start of the trajectory
    runAction(new LambdaAction(() -> mSwerve.resetOdometry(driveToIntakeSecondShotCargo.getInitialPose())));
   
    // start spinning up for shot
    runAction(new LambdaAction(() -> mSuperstructure.setWantPrep(true)));

    // start intaking
    runAction(new LambdaAction(() -> mSuperstructure.setWantIntake(true)));
    
    // start vision aiming to align drivetrain to target
    runAction(new LambdaAction(() -> mSwerve.setWantAutoVisionAim(true)));

    // run trajectory to intake second cargo
    runAction(driveToIntakeSecondShotCargo);

    // wait for 0.5 seconds to finish intaking
    runAction(new WaitAction(0.5));

    // shoot cargo
    runAction(new LambdaAction(() -> mSuperstructure.setWantShoot(true)));
    runAction(new WaitAction(2.0));
    runAction(new LambdaAction(() -> mSuperstructure.setWantShoot(false)));

    // stop vision aiming to control robot heading
    runAction(new LambdaAction(() -> mSwerve.setWantAutoVisionAim(false)));
    
    // run trajectory for third cargo
    runAction(driveToThirdShotCargo);

    //run trajectory to intake third shot cargo
    runAction(driveToIntakeThirdShotCargo);
    
    // wait for 0.5 seconds to finish intaking
    runAction(new WaitAction(0.5));

    // start vision aiming to align drivetrain to target
    runAction(new LambdaAction(() -> mSwerve.setWantAutoVisionAim(true)));

    // shoot cargo
    runAction(new LambdaAction(() -> mSuperstructure.setWantShoot(true)));
    runAction(new WaitAction(2.0));
    runAction(new LambdaAction(() -> mSuperstructure.setWantShoot(false)));

    // stop vision aiming to control robot heading
    runAction(new LambdaAction(() -> mSwerve.setWantAutoVisionAim(false)));

    // run trajectory for terminal
    runAction(driveToIntakeAtTerminal);

    // wait for 0.5 seconds to finish intaking
    runAction(new WaitAction(0.5));

    // start vision aiming to align drivetrain to target
    runAction(new LambdaAction(() -> mSwerve.setWantAutoVisionAim(true)));

    // run trajectory for fourth shot pose
    runAction(driveToFourthShotPose);

    // shoot cargo
    runAction(new LambdaAction(() -> mSuperstructure.setWantShoot(true)));
    runAction(new WaitAction(2.0));
    runAction(new LambdaAction(() -> mSuperstructure.setWantShoot(false)));

    // stop vision aiming to control robot heading
    runAction(new LambdaAction(() -> mSwerve.setWantAutoVisionAim(false)));

    // run trajectory for eject cargo
    runAction(driveToEjectCargo);

    // wait for 0.5 seconds to finish intaking
    runAction(new WaitAction(0.5));

    // start ejecting fifth cargo
    runAction(new LambdaAction(() -> mSuperstructure.setWantEject(true, true)));

    // wait to finish ejecting cargo
    runAction(new WaitAction(0.5));

    // stop ejecting cargo
    runAction(new LambdaAction(() -> mSuperstructure.setWantEject(false, false)));

    System.out.println("Finished auto!");
    SmartDashboard.putBoolean("Auto Finished", true);
}

}
