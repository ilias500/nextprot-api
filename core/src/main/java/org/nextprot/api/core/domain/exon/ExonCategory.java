package org.nextprot.api.core.domain.exon;

/**
 * Created by fnikitin on 21/07/15.
 */
public enum ExonCategory {

    START("START", true),
    CODING("CODING", true),
    MONO("MONO", true),
    STOP("STOP", true),
    NOT_CODING("NOT_CODING", false),
    STOP_ONLY("STOP_ONLY", false)
    ;

    private final String typeString;
    private final boolean coding;

    ExonCategory(String typeString, boolean coding) {

        this.typeString = typeString;
        this.coding = coding;
    }

    public String getTypeString() {
        return typeString;
    }

    public boolean isCoding() {
        return coding;
    }
}
