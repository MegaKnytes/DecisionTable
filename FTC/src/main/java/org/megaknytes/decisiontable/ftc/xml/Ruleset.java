package org.megaknytes.decisiontable.ftc.xml;

import org.megaknytes.decisiontable.core.utils.DecisionTableOpModeFlavor;

import java.io.File;

public class Ruleset {
    private final File file;
    private final SystemConfiguration configuration;
    private final DecisionTableOpModeFlavor flavor;
    private final String transitionTarget;

    public Ruleset(File file, SystemConfiguration configuration, DecisionTableOpModeFlavor flavor, String transitionTarget) {
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

    public DecisionTableOpModeFlavor getFlavor() {
        return flavor;
    }

    public String getTransitionTarget() {
        return transitionTarget;
    }
}