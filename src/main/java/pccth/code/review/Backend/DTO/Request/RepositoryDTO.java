package pccth.code.review.Backend.DTO.Request;

import jakarta.validation.constraints.NotBlank;

public class RepositoryDTO {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Url is required")
    private String url;
    @NotBlank(message = "Type is required")
    private String type;
    @NotBlank(message = "SonarKey is required")
    private String sonarKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSonarKey() {
        return sonarKey;
    }

    public void setSonarKey(String sonarKey) {
        this.sonarKey = sonarKey;
    }

}
