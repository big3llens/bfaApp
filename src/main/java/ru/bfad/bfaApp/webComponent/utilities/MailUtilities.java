package ru.bfad.bfaApp.webComponent.utilities;

import ru.bfad.bfaApp.webComponent.dto.MailslistDto;
import ru.bfad.bfaApp.webComponent.dto.PersonRequest;
import ru.bfad.bfaApp.webComponent.models.Mailslist;
import ru.bfad.bfaApp.webComponent.models.Person;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class MailUtilities {
    public List<MailslistDto> mapMaillistToDto (List<Mailslist> maillists){
        List<MailslistDto> maillistDtos = new ArrayList<>();
        for (Mailslist m: maillists) {
            maillistDtos.add(new MailslistDto(m.getId(), m.getName()));
        }
        return maillistDtos;
    }

    public Collection<String> converPersonsToEmails (Collection<Person> persons){
        Collection<String> emails = new ArrayList<>();
        for (Person p: persons) {
            emails.add(p.getEmail());
        }
        return emails;
    }

    public List<PersonRequest> mapPersonToPersonRequest (Collection<Person> personslist){
        List<PersonRequest> personsRequestList = new ArrayList<>();
        for (Person m: personslist) {
            personsRequestList.add(new PersonRequest(m.getId(), m.getName(), m.getEmail(), m.getTitle()));
        }
        return personsRequestList;
    }

    public List<PersonRequest> createRemainingPersonList (List<PersonRequest> allPersons, List<PersonRequest> personsInTheMailslist){
        List<PersonRequest> remainingPersons = new ArrayList<>(allPersons);
        for (PersonRequest ap: allPersons) {
            for (PersonRequest personInList: personsInTheMailslist) {
                if(ap.getEmail().equals(personInList.getEmail())) {
                    remainingPersons.remove(ap);
                    break;
                }
            }
        }
        return remainingPersons;
    }

    public String generatedFileName (){
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss-");
        sb.append(calendar.get(Calendar.DAY_OF_MONTH))
                .append(".").append(calendar.get(Calendar.MONTH)).append(".").append(calendar.get(Calendar.YEAR))
                .append("-").append(calendar.get(Calendar.HOUR_OF_DAY))
                .append(".").append(calendar.get(Calendar.MINUTE))
                .append(".").append(calendar.get(Calendar.SECOND )).append("-");
//        sb.append("C:\\Data\\uploadFiles\\").append(calendar.getTime());
//        return sb.toString().replaceAll("\\s", "");
//        System.out.println("cutythbhjdfyyjt bvz: " + sb.toString());
        return formatter.format(calendar.getTime());
    }

    public String getEmailsFromPersonsList(List<PersonRequest> persons){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < persons.size(); i++) {
            sb.append(persons.get(i).getEmail());
            if(i < persons.size() - 1) sb.append("; ");
        }
        return sb.toString();
    }
}
