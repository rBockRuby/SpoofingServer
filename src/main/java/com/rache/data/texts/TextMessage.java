package com.rache.data.texts;

import java.util.Date;

public class TextMessage {

    private int id;
    private String messageSid;
    private String fromNumber;
    private String toNumber;
    private String messageBody;
    private Boolean isReply;
    private Date dateSent;
    private Date dateReceived;

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageSid() {
        return messageSid;
    }

    public void setMessageSid(String messageSid) {
        this.messageSid = messageSid;
    }

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

    public Boolean getReply() {
        return isReply;
    }

    public void setReply(Boolean reply) {
        isReply = reply;
    }
}
