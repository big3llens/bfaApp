package ru.bfad.bfaApp.webComponent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandbookPerson implements Comparable<HandbookPerson> {
    private Integer id;
    private String fullName;
    private String email;
    private String title;
    private String innerNumber;
    private String cityNumber;
    private String mobileNumber;
    private String department;
    private Integer order;

    @Override
    public int compareTo(HandbookPerson person) {
        int resultDepartment = this.department.compareTo(person.getDepartment());
        int resultOrder = this.getOrder() - person.getOrder();
        if (resultDepartment != 0) return resultDepartment;
        if (resultOrder != 0) return Integer.compare(this.getOrder(), person.getOrder());
        return this.getFullName().compareTo(person.getFullName());
    }

    @Override
    public String toString() {
        return "HandbookPerson{" +
                "fullName='" + fullName + '\'' +
                ", title='" + title + '\'' +
                ", department='" + department + '\'' +
                ", order=" + order +
                '}' + '\n';
    }
}
