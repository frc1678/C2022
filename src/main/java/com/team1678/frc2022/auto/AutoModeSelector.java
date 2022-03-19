package com.team1678.frc2022.auto;

import com.team1678.frc2022.ShuffleBoardInteractions;
import com.team1678.frc2022.auto.modes.*;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Optional;

public class AutoModeSelector {
    enum DesiredMode {
        DO_NOTHING, 
        TEST_PATH_AUTO,
        TWO_BALL_LEFT_AUTO,
        TWO_BALL_RIGHT_AUTO,
        TWO_BY_TWO_AUTO,
        FIVE_BALL_PLUS_1_AUTO,
        FIVE_BALL_B_AUTO,
        SIX_BALL_AUTO,    }

    private DesiredMode mCachedDesiredMode = DesiredMode.DO_NOTHING;

    private Optional<AutoModeBase> mAutoMode = Optional.empty();

    private SendableChooser<DesiredMode> mModeChooser;

    public AutoModeSelector() {
        mModeChooser = new SendableChooser<>();
        mModeChooser.setDefaultOption("Do Nothing", DesiredMode.DO_NOTHING);
        mModeChooser.addOption("Test Path Mode", DesiredMode.TEST_PATH_AUTO);
        mModeChooser.addOption("Two Ball Left Mode", DesiredMode.TWO_BALL_LEFT_AUTO);
        mModeChooser.addOption("Two Ball Right Mode", DesiredMode.TWO_BALL_RIGHT_AUTO);
        mModeChooser.addOption("Two by Two Mode", DesiredMode.TWO_BY_TWO_AUTO);
        mModeChooser.addOption("Five Ball A Mode", DesiredMode.FIVE_BALL_PLUS_1_AUTO);
        mModeChooser.addOption("Five Ball B Mode", DesiredMode.FIVE_BALL_B_AUTO);
        mModeChooser.addOption("Six Ball Mode", DesiredMode.SIX_BALL_AUTO);
        ShuffleBoardInteractions.getInstance().getOperatorTab().add("Auto Mode", mModeChooser).withSize(2, 1);
    }

    public void updateModeCreator() {
        DesiredMode desiredMode = mModeChooser.getSelected();
        if (desiredMode == null) {
            desiredMode = DesiredMode.DO_NOTHING;
        }
        if (mCachedDesiredMode != desiredMode) {
            System.out.println("Auto selection changed, updating creator: desiredMode->" + desiredMode.name());
            mAutoMode = getAutoModeForParams(desiredMode);
        }
        mCachedDesiredMode = desiredMode;
    }

    private Optional<AutoModeBase> getAutoModeForParams(DesiredMode mode) {
        switch (mode) {
        case DO_NOTHING:
            return Optional.of(new DoNothingMode());
        case TEST_PATH_AUTO:
            return Optional.of(new TestPathMode());

        case TWO_BALL_LEFT_AUTO:
            return Optional.of(new TwoBallLeftMode());

        case TWO_BALL_RIGHT_AUTO:
            return Optional.of(new TwoBallRightMode());

        case TWO_BY_TWO_AUTO:
            return Optional.of(new TwobyTwoMode());

        case FIVE_BALL_PLUS_1_AUTO:
            return Optional.of(new FiveBallPlus1Mode());

        case FIVE_BALL_B_AUTO:
            return Optional.of(new FiveBallBMode());
            
        case SIX_BALL_AUTO:
            return Optional.of(new SixBallMode());
            
        default:
            System.out.println("ERROR: unexpected auto mode: " + mode);
            break;
        }

        System.err.println("No valid auto mode found for  " + mode);
        return Optional.empty();
    }

    public void reset() {
        mAutoMode = Optional.empty();
        mCachedDesiredMode = null;
    }

    public void outputToSmartDashboard() {
        SmartDashboard.putString("AutoModeSelected", mCachedDesiredMode.name());
    }

    public Optional<AutoModeBase> getAutoMode() {
        if (!mAutoMode.isPresent()) {
            return Optional.empty();
        }
        return mAutoMode;
    }

    public boolean isDriveByCamera() {
        return mCachedDesiredMode == DesiredMode.DO_NOTHING;
    }
}
