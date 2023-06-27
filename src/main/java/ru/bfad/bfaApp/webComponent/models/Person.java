package ru.bfad.bfaApp.webComponent.models;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@Table(schema = "handbook_schema", name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;

    @ManyToMany(mappedBy = "persons", fetch = FetchType.LAZY)
    List<Mailslist> mailLists;

    public Person(String email, String fullName) {
        this.email = email;
        this.name = fullName;
    }

    public Person() {
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' + "}";
    }
}
