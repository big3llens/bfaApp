package ru.bfad.bfaApp.webComponent.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class MessageFrom {
    private String from;
    private String subject;
    private String text;
    private Integer mailslistId;
    private Integer deliveryMethod;
    private Integer sendBodyMethod;
    private MultipartFile file;
    private MultipartFile[] attachment;

    public MessageFrom(Integer deliveryMethod, Integer sendBodyMethod) {
        this.deliveryMethod = deliveryMethod;
        this.sendBodyMethod = sendBodyMethod;
    }

    @Override
    public String toString() {
        return "MessageFrom{" +
                "from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                ", mailslistId=" + mailslistId +
                ", deliveryMethod=" + deliveryMethod +
                ", file=" + file.getOriginalFilename() +
                '}';
    }
}
