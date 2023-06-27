package ru.bfad.bfaApp.webComponent.models;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(schema = "handbook_schema", name = "from")
public class From {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "email")
    private String email;
}
