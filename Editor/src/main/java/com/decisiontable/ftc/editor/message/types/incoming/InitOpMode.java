package com.decisiontable.ftc.editor.message.types.incoming;

import com.decisiontable.ftc.editor.message.Message;

public class InitOpMode extends Message {
    private final String opModeName;

    public InitOpMode(String opModeName) {
        this.opModeName = opModeName;
    }

    public String getOpModeName() {
        return opModeName;
    }
}
