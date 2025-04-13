package com.megaknytes.ftc.utilities.gamepad;

public class ButtonToggle {
    public enum states{
        NOT_BEGUN,
        PRESSED,
        FINISHED
    }

    private states _state = states.NOT_BEGUN;
    private boolean returnedValue = false;

    public boolean getToggled(boolean buttonState){
        if(buttonState && _state == states.NOT_BEGUN){
            _state = states.PRESSED;
            returnedValue = false;
        } else if (!buttonState && _state == states.PRESSED){
            _state = states.FINISHED;
            returnedValue = true;
        } else if (_state == states.FINISHED){
            _state = states.NOT_BEGUN;
            returnedValue = false;
        }
        return returnedValue;
    }
}