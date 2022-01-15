// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team1678.frc2022;

import java.util.Optional;

import com.team1678.frc2022.auto.AutoModeExecutor;
import com.team1678.frc2022.auto.AutoModeSelector;
import com.team1678.frc2022.auto.modes.AutoModeBase;
import com.team1678.frc2022.controlboard.ControlBoard;
import com.team1678.frc2022.controlboard.ControlBoard.SwerveCardinal;
import com.team1678.frc2022.loops.CrashTracker;
import com.team1678.frc2022.loops.Looper;
import com.team1678.frc2022.subsystems.Climber;
import com.team1678.frc2022.subsystems.Limelight;
import com.team1678.frc2022.subsystems.Swerve;
import com.team1678.frc2022.subsystems.Climber.WantedAction;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.TimedRobot;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */

  /* Declare necessary class objects */
  private ShuffleBoardInteractions mShuffleBoardInteractions;
	public static CTREConfigs ctreConfigs;

  // subsystem instances
  private final ControlBoard mControlBoard = ControlBoard.getInstance();
  private final Climber mClimber = Climber.getInstance();
  
  private final SubsystemManager mSubsystemManager = SubsystemManager.getInstance();
  private final Swerve mSwerve = Swerve.getInstance();
  private final Limelight mLimelight = Limelight.getInstance(); 

  // instantiate enabled and disabled loopers
  private final Looper mEnabledLooper = new Looper();
  private final Looper mDisabledLooper = new Looper();

  // auto instances
  private AutoModeExecutor mAutoModeExecutor;
  private AutoModeSelector mAutoModeSelector = new AutoModeSelector();


  public Robot() {
    CrashTracker.logRobotConstruction();
  }

  @Override
  public void robotInit() {
      ctreConfigs = new CTREConfigs();
      mShuffleBoardInteractions = ShuffleBoardInteractions.getInstance();

      try {      
        CrashTracker.logRobotInit();

        mSubsystemManager.setSubsystems(
            mSwerve,
            mClimber
        );

        mSubsystemManager.registerEnabledLoops(mEnabledLooper);
        mSubsystemManager.registerDisabledLoops(mDisabledLooper);

        mSwerve.resetOdometry(new Pose2d());            
      } catch (Throwable t) {
        CrashTracker.logThrowableCrash(t);
        throw t;
      }
  }

  @Override
  public void robotPeriodic() {
    mShuffleBoardInteractions.update();
  }

  @Override
  public void autonomousInit() {
      CrashTracker.logAutoInit();

      try {
        
        mEnabledLooper.start();
        mAutoModeExecutor.start();

      } catch (Throwable t) {
          CrashTracker.logThrowableCrash(t);
          throw t;
      }
      
  }

  @Override
  public void autonomousPeriodic() {
    mSwerve.updateSwerveOdometry();
  }

  @Override
  public void teleopInit() {
      try {

        mDisabledLooper.stop();
        mEnabledLooper.start();

    } catch (Throwable t) {
        CrashTracker.logThrowableCrash(t);
        throw t;
    }
  }

  @Override
  public void teleopPeriodic() {
      try {
          /* SWERVE DRIVE */
          if (mControlBoard.zeroGyro()) {
              mSwerve.zeroGyro();
          }

          mSwerve.updateSwerveOdometry();

          if (mControlBoard.getSwerveSnap() != SwerveCardinal.NONE) {
              mSwerve.startSnap(mControlBoard.getSwerveSnap().degrees);
          }

          Translation2d swerveTranslation = new Translation2d(mControlBoard.getSwerveTranslation().x(), mControlBoard.getSwerveTranslation().y());
          double swerveRotation = mControlBoard.getSwerveRotation();
          mSwerve.teleopDrive(swerveTranslation, swerveRotation, true, true);

          /* CLIMBER */
          if(mControlBoard.getClimberJog() ==  -1) {
            mClimber.setState(WantedAction.RETRACT);
          } else if(mControlBoard.getClimberJog() == 1) {
            mClimber.setState(WantedAction.EXTEND);
          } else if(mControlBoard.getDeploySolenoid()) {
            mClimber.setState(WantedAction.DEPLOY);
          } else if(mControlBoard.getUndeploySolenoid()) {
            mClimber.setState(WantedAction.UNDEPLOY);
          } else {
            mClimber.setState(WantedAction.NONE);
          }
      } catch (Throwable t) {
        CrashTracker.logThrowableCrash(t);
        throw t;
      }
  }

  @Override
  public void disabledInit() {
    try {

      CrashTracker.logDisabledInit();
      mEnabledLooper.stop();
      mDisabledLooper.start();
    
    } catch (Throwable t) {
        CrashTracker.logThrowableCrash(t);
        throw t;
    }

    if (mAutoModeExecutor != null) {
        mAutoModeExecutor.stop();
    }

    // Reset all auto mode state.
    mAutoModeSelector.reset();
    mAutoModeSelector.updateModeCreator();
    mAutoModeExecutor = new AutoModeExecutor();    

  }

  @Override
  public void disabledPeriodic() {
    try {

        mAutoModeSelector.updateModeCreator();
        // [mSwerve.resetAnglesToAbsolute();

        mLimelight.setLed(Limelight.LedMode.OFF);
        mLimelight.writePeriodicOutputs();

        Optional<AutoModeBase> autoMode = mAutoModeSelector.getAutoMode();
        if (autoMode.isPresent() && autoMode.get() != mAutoModeExecutor.getAutoMode()) {
            System.out.println("Set auto mode to: " + autoMode.get().getClass().toString());
            mAutoModeExecutor.setAutoMode(autoMode.get());
        }
        
    } catch (Throwable t) {
        CrashTracker.logThrowableCrash(t);
        throw t;
    }
  }

  @Override
  public void testInit() {
      try {
        mDisabledLooper.stop();
        mEnabledLooper.stop();			
      } catch (Throwable t) {
        CrashTracker.logThrowableCrash(t);
        throw t;
      }
  }

  @Override
  public void testPeriodic() {}
}
