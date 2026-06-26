package com.bankrolldiscipline.web.dui;

import java.util.List;
import java.util.Set;

/** Validated response consumed by the browser-side safe renderer. */
public record DuiIntentResponse(
    String version,
    String screen,
    Set<String> whitelist,
    List<DuiComponent> components,
    boolean fallback,
    List<String> messages) {
}
