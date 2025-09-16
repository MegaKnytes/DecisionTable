package org.megaknytes.ftc.decisiontable.core.drivers;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.megaknytes.ftc.decisiontable.core.utilities.UtilButtonToggle;


public class GamepadDriver {
    int numGamePads;

    Gamepad gp1, gp2;

    // this util is being added to handle buttons used for more than one purpose
    // in the Into The Deep game.  In the future, it may be needed for more buttons.
    UtilButtonToggle gp1RightBumper = new UtilButtonToggle();
    UtilButtonToggle gp1LeftBumper = new UtilButtonToggle();
    UtilButtonToggle gp1DpadLeft = new UtilButtonToggle();
    UtilButtonToggle gp1DpadRight = new UtilButtonToggle();
    UtilButtonToggle gp1DpadUp = new UtilButtonToggle();
    UtilButtonToggle gp1DpadDown = new UtilButtonToggle();
    UtilButtonToggle gp1Back = new UtilButtonToggle();
    UtilButtonToggle gp1y = new UtilButtonToggle();
    UtilButtonToggle gp1a = new UtilButtonToggle();
    UtilButtonToggle gp1x = new UtilButtonToggle();
    UtilButtonToggle gp1b = new UtilButtonToggle();
    UtilButtonToggle gp2DpadLeft = new UtilButtonToggle();
    UtilButtonToggle gp2DpadRight = new UtilButtonToggle();
    UtilButtonToggle gp2DpadUp = new UtilButtonToggle();
    UtilButtonToggle gp2DpadDown = new UtilButtonToggle();
    UtilButtonToggle gp2LeftBumper = new UtilButtonToggle();
    UtilButtonToggle gp2RightBumper = new UtilButtonToggle();
    UtilButtonToggle gp2y = new UtilButtonToggle();
    UtilButtonToggle gp2a = new UtilButtonToggle();
    UtilButtonToggle gp2x = new UtilButtonToggle();
    UtilButtonToggle gp2b = new UtilButtonToggle();
    UtilButtonToggle gp2Back = new UtilButtonToggle();


    public GamepadDriver(int numGamePads) {
        int i;
        this.numGamePads = numGamePads;
    }

    public double get(int channel) {
        Gamepad gamepad = null;
        // channel # range indicates which gamepad it is
        // gamepad1 is channels 0-18
        // gamepad2 is channels 20-38
        switch (channel) {
// Gamepad 1
            case 0:  // A button
                if (gp1a.status(gp1.a)) return (1.0);
                break;
            case 1: // X button
                if (gp1x.status(gp1.x)) return (1.0);
                break;
            case 2: // Y button
                if (gp1y.status(gp1.y)) return (1.0);
                break;
            case 3: // B button
                if (gp1b.status(gp1.b)) return (1.0);
                break;
            case 4: // dpad_left button
                if (gp1DpadLeft.status(gp1.dpad_left)) return (1.0);
                break;
            case 5: // dpad_up button
                if (gp1DpadUp.status(gp1.dpad_up)) return (1.0);
                break;
            case 6: // dpad_down button
                if (gp1DpadDown.status(gp1.dpad_down)) return (1.0);
                break;
            case 7: // dpad_right button
                if (gp1DpadRight.status(gp1.dpad_right)) return (1.0);
                break;
            case 8: // left_bumper button
                if (gp1LeftBumper.status(gp1.left_bumper)) return(1.0);
                break;
            case 9: // right_bumper button
                if (gp1RightBumper.status(gp1.right_bumper)) return(1.0);
                break;
            case 10: // right_stick_button button
                if (gp1.right_stick_button) return (1.0);
                break;
            case 11: // left_stick_button button
                if (gp1.left_stick_button) return (1.0);
                break;
            case 12: // back
                if (gp1Back.status(gp1.back)) return (1.0);
                break;
            case 13: // left_stick_x
                return (double) gp1.left_stick_x;
            case 14: // left_stick_y
                return (double) gp1.left_stick_y;
            case 15: // right_stick_x
                return (double) gp1.right_stick_x;
            case 16: // right_stick_y
                return (double) gp1.right_stick_y;
            case 17: // left_trigger
                return (double) gp1.left_trigger;
            case 18: // right_trigger
                return (double) gp1.right_trigger;

// Gamepad 2
            case 20:  // A button
                if (gp2a.status(gp2.a)) return (1.0);
                break;
            case 21: // X button
                if (gp2x.status(gp2.x)) return (1.0);
                break;
            case 22: // Y button
                if (gp2y.status(gp2.y)) return (1.0);
                break;
            case 23: // B button
                if (gp2b.status(gp2.b)) return (1.0);
                break;
            case 24: // dpad_left button
                if (gp2DpadLeft.status(gp2.dpad_left)) return (1.0);
                break;
            case 25: // dpad_up button
                if (gp2DpadUp.status(gp2.dpad_up)) return (1.0);
                break;
            case 26: // dpad_down button
                if (gp2DpadDown.status(gp2.dpad_down)) return (1.0);
                break;
            case 27: // dpad_right button
                if (gp2DpadRight.status(gp2.dpad_right)) return (1.0);
                break;
            case 28: // left_bumper button
                if (gp2LeftBumper.status(gp2.left_bumper)) return (1.0);
                break;
            case 29: // right_bumper button
                if (gp2RightBumper.status(gp2.right_bumper)) return (1.0);
                break;
            case 30: // right_stick_button button
                if (gp2.right_stick_button) return (1.0);
                break;
            case 31: // left_stick_button button
                if (gp2.left_stick_button) return (1.0);
                break;
            case 32: // back
                if (gp2Back.status(gp2.back)) return (1.0);
                break;
            case 33: // left_stick_x
                return (double) gp2.left_stick_x;
            case 34: // left_stick_y
                return (double) gp2.left_stick_y;
            case 35: // right_stick_x
                return (double) gp2.right_stick_x;
            case 36: // right_stick_y
                return (double) gp2.right_stick_y;
            case 37: // left_trigger
                return (double) gp2.left_trigger;
            case 38: // right_trigger
                return (double) gp2.right_trigger;
        }
        return (0.0);
    }

    public void set(int channel, double value) {
    }

    public void init(String IOName, int channel, double initVal, Gamepad gp1, Gamepad gp2) {
        this.gp1 = gp1;
        this.gp2 = gp2;
    }
}