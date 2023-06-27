package ru.bfad.bfaApp.bots.models;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(schema = "bot_schema", name = "not_registereg_agent")
public class NotRegisteredAgent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "fio")
    private String fio;

    @Column(name = "phone")
    private String phone;

    @Column(name = "company")
    private String company;

    @Column(name = "telegram_info")
    private String telegramInfo;

    @Column(name = "is_firstregistrtion")
    private boolean isFirstRegistration;
}
