package ru.bfad.bfaApp.webComponent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageTo {
    private String from;
    private String subject;
    private String text;
    private Collection<String> to;
    private Integer deliveryMethod;
    private Integer sendBodyMethod;
    private File file;
    private MultipartFile[] attachment;

    public MessageTo(String from, String subject, String text, Collection<String> to, Integer deliveryMethod, Integer sendBodyMethod) {
        this.from = from;
        this.subject = subject;
        this.text = text;
        this.to = to;
        this.deliveryMethod = deliveryMethod;
        this.sendBodyMethod = sendBodyMethod;
    }

    public String getNiceTo (){
        String s = to.toString();
        return s.substring(1, s.length() - 1);
    }

    public String getAttachmentString(){
        String s = Arrays.toString(attachment);
        return  s.substring(1, s.length() - 1);
    }

    public File getFile() {
        return file;
    }

    public MultipartFile[] getAttachment() {
        return attachment;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setAttachment(MultipartFile[] attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (attachment != null){
            sb.append("MessageTo{" +
                    "from='" + from + '\'' +
                    ", subject='" + subject + '\'' +
                    ", text='" + text + '\'' +
                    ", to=" + to +
                    ", deliveryMethod=" + deliveryMethod +
                    ", sendBodyMethod=" + sendBodyMethod +
                    ", file=" + file.getName() +
                    ", attachment=[");
            for (MultipartFile attachment: attachment) {
                sb.append(" " + attachment.getOriginalFilename() + ",");
            }
            sb.append("]");
            return sb.toString();
        }
        return "MessageTo{" +
                "from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                ", to=" + to +
                ", deliveryMethod=" + deliveryMethod +
                ", sendBodyMethod=" + sendBodyMethod +
                ", file=" + file +
                ", attachment=" + "Пусто" +
                '}';
    }
}
