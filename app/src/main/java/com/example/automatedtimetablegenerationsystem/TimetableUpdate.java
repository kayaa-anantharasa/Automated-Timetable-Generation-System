package com.example.automatedtimetablegenerationsystem;

public class TimetableUpdate {
    private String timetableKey;
    private String updateMessage;
    private String timestamp;

    public String getTimetableKey() {
        return timetableKey;
    }

    public void setTimetableKey(String timetableKey) {
        this.timetableKey = timetableKey;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public TimetableUpdate(String timetableKey, String updateMessage, String timestamp) {
        this.timetableKey = timetableKey;
        this.updateMessage = updateMessage;
        this.timestamp = timestamp;
    }
}
