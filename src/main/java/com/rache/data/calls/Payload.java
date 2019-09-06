package com.rache.data.calls;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Payload {
    private String to;
    private String start_time;
    private String from;
    private String call_control_id;
    private String connection_id;
    private String call_leg_id;
    private String call_session_id;
    private String client_state;
    private String direction;
    private String state;
    private String digits;
    private String recording_urls;
    private String public_recording_urls;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCall_control_id() {
        return call_control_id;
    }

    public void setCall_control_id(String call_control_id) {
        this.call_control_id = call_control_id;
    }

    public String getConnection_id() {
        return connection_id;
    }

    public void setConnection_id(String connection_id) {
        this.connection_id = connection_id;
    }

    public String getCall_leg_id() {
        return call_leg_id;
    }

    public void setCall_leg_id(String call_leg_id) {
        this.call_leg_id = call_leg_id;
    }

    public String getCall_session_id() {
        return call_session_id;
    }

    public void setCall_session_id(String call_session_id) {
        this.call_session_id = call_session_id;
    }

    public String getClient_state() {
        return client_state;
    }

    public void setClient_state(String client_state) {
        this.client_state = client_state;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDigits() {
        return digits;
    }

    public void setDigits(String digits) {
        this.digits = digits;
    }

    public String getRecording_urls() {
        return recording_urls;
    }

    public void setRecording_urls(String recording_urls) {
        this.recording_urls = recording_urls;
    }

    public String getPublic_recording_urls() {
        return public_recording_urls;
    }

    public void setPublic_recording_urls(String public_recording_urls) {
        this.public_recording_urls = public_recording_urls;
    }
}
