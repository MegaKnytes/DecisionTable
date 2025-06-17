package org.megaknytes.ftc.decisiontable.core.xml.structure;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

import java.io.File;

public class Ruleset {
    private final File file;
    private final SystemConfiguration configuration;
    private final OpModeMeta.Flavor flavor;
    private final String transitionTarget;

    public Ruleset(File file, SystemConfiguration configuration, OpModeMeta.Flavor flavor, String transitionTarget) {
        this.file = file;
        this.configuration = configuration;
        this.flavor = flavor;
        this.transitionTarget = transitionTarget;
    }

    public File getFile() {
        return file;
    }

    public SystemConfiguration getConfiguration() {
        return configuration;
    }

    public OpModeMeta.Flavor getFlavor() {
        return flavor;
    }

    public String getTransitionTarget() {
        return transitionTarget;
    }
}