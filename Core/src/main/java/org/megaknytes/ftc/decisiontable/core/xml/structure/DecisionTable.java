package org.megaknytes.ftc.decisiontable.core.xml.structure;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

import java.io.File;

public class DecisionTable {
    private final File file;
    private final OpModeMeta.Flavor flavor;
    private final String transitionTarget;

    public DecisionTable(File file, OpModeMeta.Flavor flavor, String transitionTarget) {
        this.file = file;
        this.flavor = flavor;
        this.transitionTarget = transitionTarget;
    }

    public File getFile() {
        return file;
    }

    public OpModeMeta.Flavor getFlavor() {
        return flavor;
    }

    public String getTransitionTarget() {
        return transitionTarget;
    }
}
