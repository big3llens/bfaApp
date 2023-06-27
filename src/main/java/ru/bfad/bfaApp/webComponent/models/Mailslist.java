package ru.bfad.bfaApp.webComponent.models;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
@Table(schema = "handbook_schema", name = "mail_list")
@RequiredArgsConstructor
public class Mailslist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(schema = "handbook_schema", name = "person_maillist",
            joinColumns = @JoinColumn(name = "maillist_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Collection<Person> persons;

    public Mailslist(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Mailslist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", persons=" + persons +
                '}';
    }
}
