package pccth.code.review.Backend.DTO;

import java.util.Date;

public class AnalysisLogEntry {
    private Date timestamp;
    private String message;

    public AnalysisLogEntry() {
    }

    public AnalysisLogEntry(String message) {
        this.timestamp = new Date();
        this.message = message;
    }

    public AnalysisLogEntry(Date timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

//    @Override
//    public String toString() {
//        return "AnalysisLogEntry{" +
//                "timestamp=" + timestamp +
//                ", message='" + message + '\'' +
//                '}';
//    }
}
