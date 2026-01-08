package pccth.code.review.Backend.DTO.Request;


import pccth.code.review.Backend.EnumType.EmailType;

public class EmailRequestDTO {

    private String email;
    private EmailType type;   // ใช้ enum ตรงนี้
    private String link;
    private String username;

    // getter / setter

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmailType getType() {
        return type;
    }

    public void setType(EmailType type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
