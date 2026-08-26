package org.megaknytes.decisiontable.ftc;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

import java.io.File;

public class DecisionTableFile {
    private final File file;
    private final File systemConfiguration;
    private final OpModeMeta.Flavor flavor;
    private final String transitionTarget;

    public DecisionTableFile(File file, File configuration, OpModeMeta.Flavor flavor, String transitionTarget) {
        this.file = file;
        this.systemConfiguration = configuration;
        this.flavor = flavor;
        this.transitionTarget = transitionTarget;
    }

    public File getFile() {
        return file;
    }

    public File getSystemConfiguration() {
        return systemConfiguration;
    }

    public OpModeMeta.Flavor getFlavor() {
        return flavor;
    }

    public String getTransitionTarget() {
        return transitionTarget;
    }
}