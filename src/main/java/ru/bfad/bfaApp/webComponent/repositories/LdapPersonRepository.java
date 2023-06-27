package ru.bfad.bfaApp.webComponent.repositories;

import static org.springframework.ldap.query.LdapQueryBuilder.query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;
import ru.bfad.bfaApp.webComponent.dto.HandbookPerson;
import ru.bfad.bfaApp.webComponent.dto.PersonRequest;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;
import java.util.List;

@Service
public class LdapPersonRepository {

    @Autowired
    private LdapTemplate ldapTemplate;

    private String filter = "(&(objectclass=person)(title=*))";
//    private String filter = "(&(mail=*)(title=*)";
//    private String filter = "(&(mail=*)(name=backuper))";
//    private String filter = "(name=backuper)";
//    private String filter = "(&(objectClass=person)(mail!null))";
//    private String filter = "(&(&(objectclass=person)(cn=*))!(mail=*)))";
//    private String filter = "(distinguishedName=cn=backuper,cn=users,dc=bfad,dc=ru)";
    private String base = "dc=bfad,dc=ru";
//    private String domenName = "ou=it,ou=bfa users,dc=bfad,dc=ru";
//    private String domenName = "cn=users,dc=bfad,dc=ru";
    private String domenName = "cn=users,dc=bfad,dc=ru";


//    public List<String> getAllPersonNames() {
//        ldapTemplate.setIgnorePartialResultException(true);
//        return ldapTemplate.search(
//                base, filter,
//                new AttributesMapper<String>() {
//                    public String mapFromAttributes(Attributes attrs)
//                            throws NamingException {
//                        return (String) attrs.get("mail").get();
//                    }
//                });
//    }

//    public List<Person> getAllPersonNames() {
//        ldapTemplate.setIgnorePartialResultException(true);
//        return ldapTemplate.search(
//                base, filter,
//                new PersonAttributesMapper());
//    }

    public List<PersonRequest> getAllPersonWithEmail(String nameSpase) throws InvalidNameException {
        ldapTemplate.setIgnorePartialResultException(true);
        LdapName ldapName = new LdapName(nameSpase);
        return ldapTemplate.search(
                ldapName, filter,
                new SimplePersonAttributesMapper());
    }

    public List<HandbookPerson> getAllPersonForHandbook(String nameSpase) throws InvalidNameException {
        ldapTemplate.setIgnorePartialResultException(true);
        LdapName ldapName = new LdapName(nameSpase);
        return ldapTemplate.search(
                query().base(nameSpase).where("title").isPresent().and("mail").isPresent(),
                new FullPersonAttributesMapper());
    }

//    public List<String> getAllPersonWithEmails() {
//        ldapTemplate.setIgnorePartialResultException(true);
//        LdapQuery query = query()
//                .attributes("cn", "sn")
//                .where("objectclass").is("person")
//                .and("instanceType").is("4");
//        return ldapTemplate.search(
//                query,
//                new AttributesMapper<String>() {
//                    public String mapFromAttributes(Attributes attrs)
//                            throws NamingException {
//                        return (String) attrs.get("mail").get();
//                    }
//                });
//    }
//
//    public List<PersonRequest> getAllPersons() {
//        ldapTemplate.setIgnorePartialResultException(true);
//        return ldapTemplate.search(query()
//                .where("objectclass").is("person"), new PersonAttributesMapper());
//    }

    private class SimplePersonAttributesMapper implements AttributesMapper<PersonRequest> {
        public PersonRequest mapFromAttributes(Attributes attrs) throws NamingException {
            PersonRequest person = new PersonRequest();
            person.setFullName((String)attrs.get("name").get());
            person.setEmail((String)attrs.get("mail").get());
            person.setTitle((String)attrs.get("title").get());
            return person;
        }
    }
    private class FullPersonAttributesMapper implements AttributesMapper<HandbookPerson> {
        public HandbookPerson mapFromAttributes(Attributes attrs) throws NamingException {
            HandbookPerson person = new HandbookPerson();
            person.setFullName(attrs.get("name")==null ? "-" : (String)attrs.get("name").get());
            person.setEmail(attrs.get("mail")==null ? "-" : (String)attrs.get("mail").get());
            person.setTitle(attrs.get("title")==null ? "-" : (String)attrs.get("title").get());
            person.setDepartment(attrs.get("department")==null ? "-" : (String)attrs.get("department").get());
            person.setInnerNumber(attrs.get("telephoneNumber")==null ? "-" : (String)attrs.get("telephoneNumber").get());
            person.setCityNumber(attrs.get("homePhone")==null ? "-" : (String)attrs.get("homePhone").get());
            person.setMobileNumber(attrs.get("mobile")==null ? "-" : (String)attrs.get("mobile").get());
            person.setOrder(100);
            return person;
        }
    }
}
