package pccth.code.review.Backend.DTO.Request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class RepositoryDTO {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Url is required")
    private String url;
    @NotBlank(message = "Type is required")
    private String type;
    @NotBlank(message = "Cost is required")
    private BigDecimal costPerDay;

    public BigDecimal getCostPerDay() {
        return costPerDay;
    }

    public void setCostPerDay(BigDecimal costPerDay) {
        this.costPerDay = costPerDay;
    }

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

}
