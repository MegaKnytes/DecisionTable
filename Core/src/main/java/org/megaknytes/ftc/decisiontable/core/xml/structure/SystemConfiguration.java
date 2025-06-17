package org.megaknytes.ftc.decisiontable.core.xml.structure;

import java.io.File;

public class SystemConfiguration {
    private final File file;

    public SystemConfiguration(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}