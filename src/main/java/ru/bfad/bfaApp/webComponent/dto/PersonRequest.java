package ru.bfad.bfaApp.webComponent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonRequest implements Comparable<PersonRequest>{
    private Integer id;
    private String fullName;
    private String email;
    private String title;

    @Override
    public int compareTo(PersonRequest person) {
        return this.fullName.compareTo(person.getFullName()) + 100;
    }
}
