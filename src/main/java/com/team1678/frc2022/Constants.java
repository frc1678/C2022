package com.team1678.frc2022;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.lib.util.SwerveModuleConstants;
import com.team1678.frc2022.subsystems.Limelight.LimelightConstants;
import com.team254.lib.geometry.Pose2d;
import com.team254.lib.geometry.Rotation2d;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.Solenoid;

public class Constants {
	
	// robot loop time
	public static final double kLooperDt = 0.02;

	/* Control Board */
	public static final double kJoystickThreshold = 0.2;
	public static final int kButtonGamepadPort = 1;
	
	/* 364 IMPORTED CONSTANTS */
	public static final double stickDeadband = 0.15;

	public static final class SwerveConstants {	
        public static final boolean invertGyro = false; // Always ensure Gyro is CCW+ CW-

        /* Drivetrain Constants */
        public static final double trackWidth = Units.inchesToMeters(20.75); // TODO: Check value
		public static final double wheelBase = Units.inchesToMeters(20.75); // TODO: Check value

        public static final double wheelDiameter = Units.inchesToMeters(4.0);
        public static final double wheelCircumference = wheelDiameter * Math.PI;

        public static final double openLoopRamp = 0.25;
        public static final double closedLoopRamp = 0.0;

        public static final double driveGearRatio = 6.92307; // TODO: Check value
        public static final double angleGearRatio = 11.57142; // TODO: Check value

        public static final SwerveDriveKinematics swerveKinematics = new SwerveDriveKinematics(
                new edu.wpi.first.math.geometry.Translation2d(wheelBase / 2.0, trackWidth / 2.0),
                new edu.wpi.first.math.geometry.Translation2d(wheelBase / 2.0, -trackWidth / 2.0),
                new edu.wpi.first.math.geometry.Translation2d(-wheelBase / 2.0, trackWidth / 2.0),
                new edu.wpi.first.math.geometry.Translation2d(-wheelBase / 2.0, -trackWidth / 2.0));

        /* Swerve Current Limiting */
        public static final int angleContinuousCurrentLimit = 25;
        public static final int anglePeakCurrentLimit = 40;
        public static final double anglePeakCurrentDuration = 0.1;
        public static final boolean angleEnableCurrentLimit = true;

        public static final int driveContinuousCurrentLimit = 35;
        public static final int drivePeakCurrentLimit = 60;
        public static final double drivePeakCurrentDuration = 0.1;
        public static final boolean driveEnableCurrentLimit = true;

        /* Angle Motor PID Values */
        public static final double angleKP = 0.3; // TODO: Check value
        public static final double angleKI = 0.0;
        public static final double angleKD = 0.0;
        public static final double angleKF = 0.0;

        /* Drive Motor PID Values */
        public static final double driveKP = 0.05; // TODO: Check value
        public static final double driveKI = 0.0;
        public static final double driveKD = 0.0;
        public static final double driveKF = 0.0;

        /* Drive Motor Characterization Values */
        public static final double driveKS = (0.667 / 12); // divide by 12 to convert from volts to percent output for CTRE
        public static final double driveKV = (2.44 / 12);
        public static final double driveKA = (0.27 / 12);

        /* Swerve Profiling Values */
        public static final double maxSpeed = 4.5; // meters per second
        public static final double maxAngularVelocity = 11.5;

        /* Neutral Modes */
        public static final NeutralMode angleNeutralMode = NeutralMode.Coast;
        public static final NeutralMode driveNeutralMode = NeutralMode.Brake;

        /* Motor Inverts */
        public static final boolean driveMotorInvert = false; // TODO: Check value
        public static final boolean angleMotorInvert = true;

        /* Angle Encoder Invert */
        public static final boolean canCoderInvert = false;

        /* Controller Invert */
        public static final boolean invertXAxis = false; // TODO: Check value 
        public static final boolean invertYAxis = false; // TODO: Check value
        public static final boolean invertRAxis = false; // TODO: Check value


        /*** MODULE SPECIFIC CONSTANTS ***/
        
		/* Front Left Module - Module 0 */
        public static final class Mod0 {
            public static final double angleOffset = 334; // 126; // 234
            public static final SwerveModuleConstants constants =
                new SwerveModuleConstants(Ports.FL_DRIVE, Ports.FL_ROTATION, Ports.FL_CANCODER, angleOffset);
        }

        /* Front Right Module - Module 1 */
        public static final class Mod1 {
            public static final double angleOffset = 216; // 357; // 3
            public static final SwerveModuleConstants constants =
                new SwerveModuleConstants(Ports.FR_DRIVE, Ports.FR_ROTATION, Ports.FR_CANCODER, angleOffset);
        }

        /* Back Left Module - Module 2 */
        public static final class Mod2 {
            public static final double angleOffset = 183; // 323; // 37
            public static final SwerveModuleConstants constants =
                new SwerveModuleConstants(Ports.BL_DRIVE, Ports.BL_ROTATION, Ports.BL_CANCODER, angleOffset);
        }

        /* Back Right Module - Module 3 */
        public static final class Mod3 {
            public static final double angleOffset = 53; // 204; // 155
            public static final SwerveModuleConstants constants =
                new SwerveModuleConstants(Ports.BR_DRIVE, Ports.BR_ROTATION, Ports.BR_CANCODER, angleOffset);
        }

	}
	
	public static final class SnapConstants {
        public static final double snapKP = 1.0;
        public static final double snapKI = 0;
        public static final double snapKD = 0.0;
        public static final double snapTimeout = 0.25;
        public static final double snapEpsilon = 1.0;

        // Constraints for the profiled angle controller
        public static final double kMaxAngularSpeedRadiansPerSecond = 2.0*Math.PI;
        public static final double kMaxAngularSpeedRadiansPerSecondSquared = Math.pow(kMaxAngularSpeedRadiansPerSecond, 2);

        public static final TrapezoidProfile.Constraints kThetaControllerConstraints =
        new TrapezoidProfile.Constraints(
            kMaxAngularSpeedRadiansPerSecond, kMaxAngularSpeedRadiansPerSecondSquared);
    }

    public static final class AutoConstants {
        public static final double kMaxSpeedMetersPerSecond = 2.5; // TODO: Revise this
        public static final double kMaxAccelerationMetersPerSecondSquared = 3.0 // TODO: Revise this
        ; // TODO: Revise this
        public static final double kMaxAngularSpeedRadiansPerSecond = 2.0*Math.PI; // TODO: Revise this
        public static final double kMaxAngularSpeedRadiansPerSecondSquared = Math.pow(kMaxAngularSpeedRadiansPerSecond, 2); // TODO: Revise this
		
        public static final double kPXController = 1;
        public static final double kPYController = 1;
        public static final double kPThetaController = 5;
    
        // Constraint for the motion profilied robot angle controller
        public static final TrapezoidProfile.Constraints kThetaControllerConstraints =
            new TrapezoidProfile.Constraints(
				kMaxAngularSpeedRadiansPerSecond, kMaxAngularSpeedRadiansPerSecondSquared);
				
		// Trajectory Speed Configs
		public static final TrajectoryConfig defaultSpeedConfig = 
            new TrajectoryConfig(
                    Constants.AutoConstants.kMaxSpeedMetersPerSecond,
                    Constants.AutoConstants.kMaxAccelerationMetersPerSecondSquared)
                .setKinematics(Constants.SwerveConstants.swerveKinematics);
        public static final TrajectoryConfig zeroToDefaultSpeedConfig =
            new TrajectoryConfig(
                    Constants.AutoConstants.kMaxSpeedMetersPerSecond,
                    Constants.AutoConstants.kMaxAccelerationMetersPerSecondSquared)
                .setKinematics(Constants.SwerveConstants.swerveKinematics)
                .setStartVelocity(0)
                .setEndVelocity(Constants.AutoConstants.kMaxSpeedMetersPerSecond);
        public static final TrajectoryConfig defaultToZeroSpeedConfig =
            new TrajectoryConfig(
                    Constants.AutoConstants.kMaxSpeedMetersPerSecond,
                    Constants.AutoConstants.kMaxAccelerationMetersPerSecondSquared)
                .setKinematics(Constants.SwerveConstants.swerveKinematics)
                .setStartVelocity(0)
                .setEndVelocity(Constants.AutoConstants.kMaxSpeedMetersPerSecond);
    public static final TrajectoryConfig constantSpeedConfig =
            new TrajectoryConfig(
                    Constants.AutoConstants.kMaxSpeedMetersPerSecond,
                    Constants.AutoConstants.kMaxAccelerationMetersPerSecondSquared)
                .setKinematics(Constants.SwerveConstants.swerveKinematics)
                .setStartVelocity(kMaxSpeedMetersPerSecond)
                .setEndVelocity(Constants.AutoConstants.kMaxSpeedMetersPerSecond);
            

	  }

	  public static final class VisionConstants {
		public static final LimelightConstants kLimelightConstants = new LimelightConstants();
		static {
			kLimelightConstants.kName = "Limelight";
			kLimelightConstants.kTableName = "limelight";
			kLimelightConstants.kHeight = 24.5; // inches
			kLimelightConstants.kTurretToLens = Pose2d.identity();
			kLimelightConstants.kHorizontalPlaneToLens = Rotation2d.fromDegrees(0.0);
		}

		public static final double kHorizontalFOV = 59.6; // degrees
		public static final double kVerticalFOV = 49.7; // degrees
		public static final double kVPW = 2.0 * Math.tan(Math.toRadians(kHorizontalFOV / 2.0));
		public static final double kVPH = 2.0 * Math.tan(Math.toRadians(kVerticalFOV / 2.0));
		public static final double kImageCaptureLatency = 11.0 / 1000.0; // seconds
	}

        /*** SUBSYSTEM CONSTANTS ***/
        public static final class IntakeConstants {
            public static double kIntakeVoltage = 5;
            public static double kIdleVoltage = 0;
            public static double kSpittingVoltage = -7;
        }
    
        public static final class HopperConstants {
            
        }
    
        public static final class ElevatorConstants {
            public static final int kIndexerMotorId = 32;

            //TODO: find actual values
            public static final double kIndexerKp = 0.2;
            public static final double kIndexerKi = 0.;
            public static final double kIndexerKd = 0.;
            public static final double kIndexerKf = .05;
            public static final double kIndexerVelocityKp = 0.05;
            public static final double kIndexerVelocityKi = 0.;
            public static final double kIndexerVelocityKd = 0.;
            public static final double kIndexerVelocityKf = .05;
            public static final int kIndexerMaxVelocity = 20000;
            public static final int kIndexerMaxAcceleration = 40000;
            
            public static final int kBottomBeamBreak = 1;
            public static final int kTopBeamBreak = 0;

            public static final double kIndexingVoltage = 7.0;
            public static final double kReversingVoltage = -7.0;
            
        }
    
        public static final class ShooterConstants {
            
        }

    public static final class ClimberConstants {
        public static final double kExtendingVoltage = -8.0;
        public static final double kRetractingVoltage = 8.0;
        public static final double kIdleVoltage = 0.0;
    }

	// Timeout constants
	public static final int kLongCANTimeoutMs = 100;
	public static final int kCANTimeoutMs = 10;
}
