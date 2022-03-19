package com.team1678.frc2022.subsystems;

import com.team1678.frc2022.Constants;
import com.team1678.frc2022.Ports;
import com.team1678.frc2022.lib.drivers.PicoColorSensor;
import com.team1678.frc2022.lib.drivers.PicoColorSensor.RawColor;
import com.team1678.frc2022.loops.ILooper;
import com.team1678.frc2022.loops.Loop;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;

public class ColorSensor extends Subsystem {

    private static ColorSensor mInstance;
    public static synchronized ColorSensor getInstance() {
        if (mInstance == null) {
            mInstance = new ColorSensor();
        }
        return mInstance;
    }

    public PeriodicIO mPeriodicIO = new PeriodicIO();
    private PicoColorSensor mPico;

    private final DigitalInput mForwardBreak;

    private Timer mHasBallTimer = new Timer();
    private Timer mEjectorTimer = new Timer();

    public ColorChoices mAllianceColor = ColorChoices.NONE;
    public ColorChoices mMatchedColor;

    public enum ColorChoices {
        RED, BLUE, OTHER, NONE  
    }

    private ColorSensor() {
        mMatchedColor = ColorChoices.NONE;
        mPico = new PicoColorSensor();

        mForwardBreak = new DigitalInput(Ports.FORWARD_BEAM_BREAK);
    }

    @Override
    public void registerEnabledLoops(ILooper enabledLooper) {
        enabledLooper.register(new Loop() {
            @Override
            public void onStart(double timestamp) {
                mPico.start();
            }
 
            @Override
            public void onLoop(double timestamp) {
            }
 
            @Override
            public void onStop(double timestamp) {
            }
        });
    }

    protected void start() {
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean checkSystem() {
        // TODO Auto-generated method stub
        return false;
    }

    // check if we see a ball
    public boolean seesBall() {
        return getFowrardBeamBreak();
    }

    // check if we have the right color
    public boolean hasCorrectColor() {
        return mMatchedColor == mAllianceColor;
    }

    // check if we have the opposite color
    public boolean hasOppositeColor() {
        return !hasCorrectColor()
                    && (mMatchedColor != ColorChoices.OTHER)
                    && (mMatchedColor != ColorChoices.NONE);
    }

    // check if we have a ball
    public void updateHasBall() {
        if (seesBall()) {
            // reset the timer if we see another ball
            if (mPeriodicIO.has_ball) {
                mHasBallTimer.reset();
            }

            mPeriodicIO.has_ball = true;
            mHasBallTimer.start();
        }

        // if we don't see a ball and the threshold time for having a ball has passed, we don't have a ball anymore
        if (!seesBall() && mHasBallTimer.hasElapsed(Constants.ColorSensorConstants.kTimeWithBall)) {
            mHasBallTimer.reset();
            mPeriodicIO.has_ball = false;
        }
    }

    // update our alliance color
    // only should be updated in disabled periodic
    public void updateAllianceColor() {
        if (DriverStation.isDSAttached()) {
            if (edu.wpi.first.wpilibj.DriverStation.getAlliance() == Alliance.Red) {
                mAllianceColor = ColorChoices.RED;
            } else if (edu.wpi.first.wpilibj.DriverStation.getAlliance() == Alliance.Blue){
                mAllianceColor = ColorChoices.BLUE;
            }
        } else {
            DriverStation.reportError("No Alliance Color Detected", true);
        }
    }

    // update the color of the cargo we see
    public void updateMatchedColor() {
        if (!seesBall()) { 
            mMatchedColor = ColorChoices.NONE;
        } else {
            if (mPeriodicIO.adjustedRed > mPeriodicIO.adjustedBlue) {
                mMatchedColor = ColorChoices.RED;
            } else if (mPeriodicIO.adjustedBlue > mPeriodicIO.adjustedRed) {
                mMatchedColor = ColorChoices.BLUE;
            } else {
                mMatchedColor = ColorChoices.OTHER;
            }
        }
    }

    // update whether we want to eject or not
    public void updateWantsEject() {
        if (hasOppositeColor() && hasBall()) {
            mPeriodicIO.eject = true;
            mEjectorTimer.start();
        }

        if (mEjectorTimer.hasElapsed(Constants.IndexerConstants.kEjectDelay) || (hasCorrectColor() && hasBall())) {
            mPeriodicIO.eject = false;
            mEjectorTimer.reset();
        }
    }

    public boolean hasBall() {
        return mPeriodicIO.has_ball;
    }

    public boolean wantsEject() {
        return mPeriodicIO.eject;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        mPeriodicIO.sensor0Connected = mPico.isSensor0Connected();
        mPeriodicIO.raw_color = mPico.getRawColor0();
        
        mPeriodicIO.adjustedRed = mPeriodicIO.raw_color.red * 1.66;
        mPeriodicIO.adjustedBlue = mPeriodicIO.raw_color.blue * 1.11;

        mPeriodicIO.timestamp = mPico.getLastReadTimestampSeconds();

        updateHasBall();
        updateMatchedColor();
        updateWantsEject();
    }

    @Override 
    public synchronized void writePeriodicOutputs() {
        
    }

    //subystem getters
    public double getDetectedRValue() {
        if (mPeriodicIO.raw_color == null) {
            return 0;
        }
        return mPeriodicIO.raw_color.red;
    }
    public double getDetectedGValue() {
        if (mPeriodicIO.raw_color == null) {
            return 0;
        }
        return mPeriodicIO.raw_color.green;
    }
    public double getDetectedBValue() {
        if (mPeriodicIO.raw_color == null) {
            return 0;
        }
        return mPeriodicIO.raw_color.blue;
    }
    public String getAllianceColor() {
        return mAllianceColor.toString();
    }
    public String getMatchedColor() {
        return mMatchedColor.toString();
    }    
    public boolean getFowrardBeamBreak() {
        return !mForwardBreak.get();
    }

    public double getAdjustedRed() {
        return mPeriodicIO.adjustedRed;
    }

    public double getAdjustedBlue() {
        return mPeriodicIO.adjustedBlue;
    }

    public boolean getSensor0() {
        return mPeriodicIO.sensor0Connected;
    }

    public double getTimestamp() {
        return mPeriodicIO.timestamp;
    }

    public static class PeriodicIO {
        // INPUTS
        public RawColor raw_color;
        public double adjustedRed; // 1.66
        public double adjustedBlue; // 1.11
        public boolean sensor0Connected;

        // OUTPUTS
        public boolean has_ball;
        public boolean eject;
        public double timestamp;
    }
    
}
