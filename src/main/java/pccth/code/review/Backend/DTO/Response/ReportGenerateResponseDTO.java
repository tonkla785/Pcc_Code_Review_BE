package pccth.code.review.Backend.DTO.Response;

import java.time.Instant;

public class ReportGenerateResponseDTO {

    private String fileName;
    private String mimeType;
    private String base64;
    private long fileSizeBytes;
    private Instant generatedAt;

    public ReportGenerateResponseDTO() {
    }

    public ReportGenerateResponseDTO(String fileName, String mimeType, String base64, long fileSizeBytes, Instant generatedAt) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.base64 = base64;
        this.fileSizeBytes = fileSizeBytes;
        this.generatedAt = generatedAt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }
}
