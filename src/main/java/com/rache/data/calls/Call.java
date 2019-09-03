package com.rache.data.calls;

import java.util.Date;

public class Call {
    private int id;
    private Date callDate;
    private String fromNumber;
    private String toNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCallDate() {
        return callDate;
    }

    public void setCallDate(Date callDate) {
        this.callDate = callDate;
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
}
