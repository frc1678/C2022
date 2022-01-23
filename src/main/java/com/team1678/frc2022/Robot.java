// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team1678.frc2022;

import java.sql.Time;
import java.util.Optional;

import com.team1678.frc2022.auto.AutoModeExecutor;
import com.team1678.frc2022.auto.AutoModeSelector;
import com.team1678.frc2022.auto.modes.AutoModeBase;
import com.team1678.frc2022.controlboard.ControlBoard;
import com.team1678.frc2022.controlboard.ControlBoard.SwerveCardinal;
import com.team1678.frc2022.loops.CrashTracker;
import com.team1678.frc2022.loops.Looper;
import com.team1678.frc2022.subsystems.Climber;
import com.team1678.frc2022.subsystems.Climber.ControlState;
import com.team1678.frc2022.subsystems.Indexer;
import com.team1678.frc2022.subsystems.Infrastructure;
import com.team1678.frc2022.subsystems.Intake;
import com.team1678.frc2022.subsystems.Limelight;
import com.team1678.frc2022.subsystems.Shooter;
import com.team1678.frc2022.subsystems.Superstructure;
import com.team1678.frc2022.subsystems.Swerve;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;

import com.team254.lib.util.TimeDelayedBoolean;
import com.team254.lib.util.Util;
import com.team254.lib.wpilib.TimedRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
	/**
	 * This function is run when the robot is first started up and should be used
	 * for any
	 * initialization code.
	 */

	/* Declare necessary class objects */
	private ShuffleBoardInteractions mShuffleBoardInteractions;
	public static CTREConfigs ctreConfigs;

	// subsystem instances
	private final ControlBoard mControlBoard = ControlBoard.getInstance();

	private final SubsystemManager mSubsystemManager = SubsystemManager.getInstance();
	private final Superstructure mSuperstructure = Superstructure.getInstance();
	private final Shooter mShooter = Shooter.getInstance();
	private final Swerve mSwerve = Swerve.getInstance();
	private final Intake mIntake = Intake.getInstance();
	private final Limelight mLimelight = Limelight.getInstance();
	private final Infrastructure mInfrastructure = Infrastructure.getInstance();
	private final Indexer mIndexer = Indexer.getInstance();
	private final Climber mClimber = Climber.getInstance();

	// instantiate enabled and disabled loopers
	private final Looper mEnabledLooper = new Looper();
	private final Looper mDisabledLooper = new Looper();

	// auto instances
	private AutoModeExecutor mAutoModeExecutor;
	private AutoModeSelector mAutoModeSelector = new AutoModeSelector();

	private boolean mClimbMode = false;
	private boolean mTraversalClimb = false;

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
					mInfrastructure,
					mIntake,
					mIndexer,
					mShooter,
					mSuperstructure,
					mLimelight,
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

			mInfrastructure.setIsDuringAuto(true);

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

			mInfrastructure.setIsDuringAuto(false);

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
			Translation2d swerveTranslation = new Translation2d(mControlBoard.getSwerveTranslation().x(),
					mControlBoard.getSwerveTranslation().y());
			double swerveRotation = mControlBoard.getSwerveRotation();

			if (mControlBoard.getVisionAlign()) {
				mSwerve.visionAlignDrive(swerveTranslation, true, true);
			} else {
				mSwerve.drive(swerveTranslation, swerveRotation, true, true);
			}

			if (mControlBoard.operator.getController().getYButtonPressed()) {
				mSuperstructure.setWantShoot();
			}

			if (mControlBoard.operator.getController().getAButtonPressed()) {
				mSuperstructure.setShooterVelocity(1800);
				mSuperstructure.setWantSpinUp();
			}

			mClimbMode = mControlBoard.getClimbMode();
			mTraversalClimb = mControlBoard.getTrasversalClimb();
			if (mClimbMode) {
				if (mTraversalClimb) {
					TimeDelayedBoolean mSolenoidTimer = new TimeDelayedBoolean();
					//Extend first arm to climb onto first bar
					while (Util.inRange(mClimber.getClimberPosition(), Constants.ClimberConstants.kInitialExtensionHeight)) {
						mClimber.getInitialArmExtension();
					}
					//Extend to traversal bar
					while (Util.inRange(mClimber.getClimberPosition(), Constants.ClimberConstants.kTraversalExtentionHeight)) {
						mClimber.setClimberOpenLoop(mClimber.mPeriodicIO.climber_stator_current);
					}
					//Deploy solenoid
					while (mSolenoidTimer.update(mClimber.getClimberSolenoidDeployed(), Constants.ClimberConstants.kSolenoidDeployTime)) {
						mClimber.mPeriodicIO.deploy_solenoid = true;
						mClimber.mRightClimberSolenoid.set(mClimber.mPeriodicIO.deploy_solenoid);
        				mClimber.mLeftClimberSolenoid.set(mClimber.mPeriodicIO.deploy_solenoid);
					}
					//Undeploy solenoid
					while (mSolenoidTimer.update(mClimber.getClimberSolenoidDeployed(), Constants.ClimberConstants.kSolenoidUndeployTime)) {
						mClimber.mPeriodicIO.deploy_solenoid = false;
						mClimber.mRightClimberSolenoid.set(mClimber.mPeriodicIO.deploy_solenoid);
						mClimber.mLeftClimberSolenoid.set(mClimber.mPeriodicIO.deploy_solenoid);
					}
					//Other Subsystems
					mIntake.setState(Intake.WantedAction.STAY_OUT);
					mIndexer.setState(Indexer.WantedAction.NONE);

				} else {
					// Intake
					if (mControlBoard.getIntake()) {
						mIntake.setState(Intake.WantedAction.INTAKE);
					} else if (mControlBoard.getOuttake()) {
						mIntake.setState(Intake.WantedAction.REVERSE);
					} else if (mControlBoard.getSpitting()) {
						mIntake.setState(Intake.WantedAction.SPIT);
					} else {
						mIntake.setState(Intake.WantedAction.NONE);
				}
			}
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

			mLimelight.setLed(Limelight.LedMode.ON);
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
	public void testPeriodic() {
	}
}
