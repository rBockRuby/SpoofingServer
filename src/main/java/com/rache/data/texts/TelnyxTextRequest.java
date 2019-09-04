package com.rache.data.texts;

import javax.validation.constraints.NotNull;

public class TelnyxTextRequest {
    @NotNull
    private String from;
    @NotNull
    private String to;
    @NotNull
    private String body;
}
