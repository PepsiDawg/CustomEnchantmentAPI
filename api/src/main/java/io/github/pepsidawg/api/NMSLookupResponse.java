package io.github.pepsidawg.api;

public class NMSLookupResponse {
    public boolean found;
    public int index;

    public NMSLookupResponse(boolean found, int index) {
        this.found = found;
        this.index = index;
    }
}
