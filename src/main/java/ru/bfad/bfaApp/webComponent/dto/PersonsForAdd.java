package ru.bfad.bfaApp.webComponent.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PersonsForAdd {
    private List<PersonRequest> persons;

    public PersonsForAdd(List<PersonRequest> persons) {
        this.persons = persons;
    }

    public List<PersonRequest> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonRequest> persons) {
        this.persons = persons;
    }
}
