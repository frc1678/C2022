package com.team1678.frc2022;

public class Ports {

    /*** SWERVE MODULE PORTS ***/

    /*  
    Swerve Modules go:
        1 2
        3 4
    */

    public static final int FL_DRIVE = 0; 
    public static final int FL_ROTATION = 1;
    public static final int FL_CANCODER = 0; 

    public static final int FR_DRIVE = 2; 
    public static final int FR_ROTATION = 3;
    public static final int FR_CANCODER = 1; 

    public static final int BL_DRIVE = 4;
    public static final int BL_ROTATION = 5;
    public static final int BL_CANCODER = 2; 

    public static final int BR_DRIVE = 6;
    public static final int BR_ROTATION = 7;
    public static final int BR_CANCODER = 3; 

    // Pigeon
    public static final int PIGEON = 20;

    /*** SUBSYSTEM IDS ***/
    public static final int INTAKE_ID = 8;
    public static final int DEPLOY_SOLENOID_ID = 7;

    public static final int SINGULATOR = 9;
    public static final int HOPPER = 10;
    public static final int ELEVATOR_ID = 11;
    public static final int TOP_BEAM_BREAK = 0;
    public static final int BOTTOM_BEAM_BREAK = 1;

    public static final int FLYWHEEL_MASTER_ID = 12;
    public static final int FLYWHEEL_SLAVE_ID = 16;
    public static final int HOOD_ID = 13;

    public static final int CLIMBER_MASTER_ID = 14;
    public static final int CLIMBER_SLAVE_ID = 15;
    public static final int CLIMBER_CHOPSTICK_SOLENOID = 6;
    public static final int CLIMBER_HOOK_RELEASE_SOLENOID = 17;

    // Infrastucture
    public static final int PCM = 21;


}
