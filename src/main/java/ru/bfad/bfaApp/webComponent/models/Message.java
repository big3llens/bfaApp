package ru.bfad.bfaApp.webComponent.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "handbook_schema", name = "message")
@Getter
@Setter
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_id")
    private From from;

    @Column(name = "subject")
    private String subject;

    @Column(name = "text")
    private String text;

    @Column(name = "message_to")
    private String to;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "file_body")
    private String file_body;

    public Message(From from, String subject, String text, User user, String to) {
        this.from = from;
        this.subject = subject;
        this.text = text;
        this.user = user;
        this.to = to;
    }

    public Message(From from, String subject, String text, User user, String to, String file_body) {
        this.from = from;
        this.subject = subject;
        this.text = text;
        this.user = user;
        this.to = to;
        this.file_body = file_body;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from=" + from +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                ", user=" + user +
                ", to='" + to + '\'' +
                '}';
    }
}
