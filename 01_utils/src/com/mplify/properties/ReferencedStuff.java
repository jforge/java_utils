package com.mplify.properties;

import java.io.File;

import com.mplify.checkers.Check;

public class ReferencedStuff {

    public final File file;
    public final String resource;

    public ReferencedStuff(File file) {
        Check.notNull(file,"file");
        this.file = file;
        this.resource = null;
    }

    public ReferencedStuff(String resource) {
        Check.notNull(resource,"resource");
        this.resource = resource;
        this.file = null;
    }

}