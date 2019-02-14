package com.thedeanda.ajaxproxy.ui.update;

import lombok.Data;

@Data
public class ReleaseEntry implements Comparable<ReleaseEntry> {
    private String id;
    private String link;
    private ReleaseVersion version;

    @Override
    public int compareTo(ReleaseEntry o) {
        // we flip the compare here to get newest first
        return o.version.compareTo(version);
    }
}
