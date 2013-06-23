package com.mplify.properties;

public class ReferencedStuffAsNeeded {

    public final boolean use;
    public final ReferencedStuff stuff; // can be null in all cases

    public ReferencedStuffAsNeeded(boolean use, ReferencedStuff stuff) {
        this.use = use;
        this.stuff = stuff;
    }
}