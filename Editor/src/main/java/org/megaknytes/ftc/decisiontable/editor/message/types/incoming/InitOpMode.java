package org.megaknytes.ftc.decisiontable.editor.message.types.incoming;

import org.megaknytes.ftc.decisiontable.editor.message.Message;

public class InitOpMode extends Message {
    private final String opModeName;

    public InitOpMode(String opModeName) {
        this.opModeName = opModeName;
    }

    public String getOpModeName() {
        return opModeName;
    }
}
