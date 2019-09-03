package com.rache.data.texts;

import javax.validation.constraints.NotNull;

public class TextRequest {
    @NotNull
    private String fromNumber;
    @NotNull
    private String toNumber;
    @NotNull
    private String messageBody;

    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getToNumber() {
        return toNumber;
    }

    public void setToNumber(String toNumber) {
        this.toNumber = toNumber;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
