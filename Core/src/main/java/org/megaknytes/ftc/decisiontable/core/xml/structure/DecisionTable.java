package org.megaknytes.ftc.decisiontable.core.xml.structure;

import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

import java.io.File;

public class DecisionTable {
    private final File file;
    private final OpModeMeta.Flavor flavor;

    public DecisionTable(File file, OpModeMeta.Flavor flavor) {
        this.file = file;
        this.flavor = flavor;
    }

    public File getFile() {
        return file;
    }

    public OpModeMeta.Flavor getFlavor() {
        return flavor;
    }
}
