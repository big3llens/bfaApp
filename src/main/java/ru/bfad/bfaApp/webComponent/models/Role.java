package ru.bfad.bfaApp.webComponent.models;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(schema = "handbook_schema", name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;
}
