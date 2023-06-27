package ru.bfad.bfaApp.webComponent.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.Charsets;
//import org.apache.http.Consts;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import ru.bfad.bfaApp.webComponent.dto.*;
import ru.bfad.bfaApp.webComponent.models.*;
import ru.bfad.bfaApp.webComponent.repositories.*;
import ru.bfad.bfaApp.webComponent.utilities.MailUtilities;

import javax.naming.InvalidNameException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MailService {

    @Autowired
    private LdapPersonRepository ldapPersonRepository;
    @Autowired
    private MaillistRepositories maillistRepositories;
    @Autowired
    private PersonRepositories personRepositories;
    @Autowired
    private PersonMaillistRepositories personMaillistRepositories;
    @Autowired
    private FromRepository fromRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    private final MailUtilities utilities = new MailUtilities();

    private final StringBuilder sb = new StringBuilder();

/**    Формируем лист инициализации для sendMail.html: лист отправителей (Архипов и компания) + названия листов рассылки для селектов */
    public void initSendMail(Model model){
        ListsInit listsInit = new ListsInit(utilities.mapMaillistToDto(maillistRepositories.findAll()),
                fromRepository.findAll());
        model.addAttribute("fromList", listsInit.getListFrom());
        model.addAttribute("mailsList", listsInit.getMailslist());
        model.addAttribute("messageFrom", new MessageFrom(0,0));
    }

/**    Формируем список листов с адресами для showMailsLists.html: тупо список листов рассылки */
    public void getAllMaillists(Model model){
        model.addAttribute("mailsList", utilities.mapMaillistToDto(maillistRepositories.findAll()));
    }

/**    Формируем список сотрудников для конкретного листа рассылки для showMailsList.html */
    public void getMailslist (Integer id, Model model){
        List<PersonRequest> persons = utilities.mapPersonToPersonRequest(maillistRepositories.findById(id).orElseThrow(() -> new RuntimeException("Листа с id: {" + id + "} в базе нет")).getPersons());
        model.addAttribute("personsOfmailList", new EmailsRequest(persons, utilities.getEmailsFromPersonsList(persons)));
        model.addAttribute("mailslistId", id);
    }

/**    Сохраняем созданный в showMailsLists.html лист рассылки */
    public void saveMaillist(Mailslist mailList){
        maillistRepositories.save(mailList);
    }

/**    Вытягиваем всех ползователей из AD, у которых есть email, nameSpace - область поиска в AD (имя компании) */
    public void getAllPersonWithEmail(Model model, String nameSpace){
        List<PersonRequest> persons = null;
        try {
            persons = ldapPersonRepository.getAllPersonWithEmail(nameSpace);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
        model.addAttribute("personsFromAD", persons);
    }

/**     Если сотрудника, которого добавляем в лист еще нет в PSQL базе, то добавляем и id этого сотрудника добавляем к промежуточной таблице PersonMailslist */
    public void addPersonToMailslist(Model model, String nameCompany, PersonToMailslist personToMailslist){
        if(!personRepositories.existsByEmail(personToMailslist.getEmail())) personRepositories.save(new Person(personToMailslist.getEmail(), personToMailslist.getFullName()));
        Integer idPerson = personRepositories.findPersonByEmail(personToMailslist.getEmail()).orElseThrow(() -> new RuntimeException("emailа {" + personToMailslist.getEmail() +"} в базе нет")).getId();
        try {
            personMaillistRepositories.save(new PersonMailslist(personToMailslist.getMailslistId(), idPerson));
        }catch (RuntimeException e){
            System.out.println("произошла ошибочка при добавлении, скорее всего юзер обновил страницу браузера, " +
                    "тем самым проведя дублированный запрос на добавление уже имеющего сотрдника в лист рассылки!!!");
        }
        getEmployeesOffTheMailslist(model, nameCompany, personToMailslist.getMailslistId(), 1);
    }
/**     Вычитаем из общего списка сотрудников выбранной компании (nameSpace) сотрудников, которые уже присутствуют в листе рассылок (mailslistId) */
    public List<PersonRequest> getEmployeesOffTheMailslist(Model model, String nameCompany, Integer mailslistId, Integer indexSort){
        try {
            List<PersonRequest> personsFromTheCompany = ldapPersonRepository.getAllPersonWithEmail(nameCompany);
            Mailslist mailslist = maillistRepositories.findById(mailslistId).orElseThrow();
            List<PersonRequest> personsInTheList = utilities.mapPersonToPersonRequest(mailslist.getPersons());
            String mailsListName = mailslist.getName();
            String nameCompanyForUser;
            switch (nameCompany){
                case "ou=bfa development,ou=bfa users,dc=bfad,dc=ru": nameCompanyForUser = "БФА-Девелопмент";
                    break;
                case "ou=БФА-Инвестиции,ou=externalcompanies,dc=bfad,dc=ru": nameCompanyForUser = "БФА-Инвестиции";
                    break;
                case "ou=Дудергофский проект,ou=externalcompanies,dc=bfad,dc=ru": nameCompanyForUser = "Дудергофский проект";
                    break;
                case "ou=БФА Сервис,ou=externalcompanies,dc=bfad,dc=ru": nameCompanyForUser = "БФА-Сервис";
                    break;
                case "ou=Центр-Инвест-Строй,ou=externalcompanies,dc=bfad,dc=ru": nameCompanyForUser = "Центр-инвест-Строй";
                    break;
                case "ou=Пламя,ou=externalcompanies,dc=bfad,dc=ru": nameCompanyForUser = "Пламя";
                    break;
                case "ou=МК БФА,ou=externalcompanies,dc=bfad,dc=ru": nameCompanyForUser = "МК-БФА";
                    break;
                case "ou=БФА-Монолит,ou=externalcompanies,dc=bfad,dc=ru": nameCompanyForUser = "БФА-Монолит";
                    break;
                case "ou=Невское наследие,ou=externalcompanies,dc=bfad,dc=ru": nameCompanyForUser = "Невское наследие";
                    break;
                case "ou=ПСБ Жилстрой,ou=externalcompanies,dc=bfad,dc=ru": nameCompanyForUser = "ПСБ Жилстрой";
                    break;
                case "ou=Северо-Западная индустриальная компания,ou=externalcompanies,dc=bfad,dc=ru": nameCompanyForUser = "Северо-Западная индустриальная компания";
                    break;
                case "ou=Зенит-Строй-Инвест,ou=externalcompanies,dc=bfad,dc=ru": nameCompanyForUser = "Зенит-инвест-строй";
                    break;
                default:nameCompanyForUser = "БФА-Девелопмент";
                    break;
            }

            List<PersonRequest> p = utilities.createRemainingPersonList(personsFromTheCompany, personsInTheList);
            PersonRequestComparator comparator = new PersonRequestComparator();
            if (indexSort == 0) p.sort(comparator.reversed());
            else if (indexSort == 1) p.sort(comparator);
            model.addAttribute("mailsListName", mailsListName);
            model.addAttribute("personsFromAD", p);
            model.addAttribute("nameCompanyForUser", nameCompanyForUser);
            return p;
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
        return null;
    }
/** Удаляем сотрудника из листа рассылки */
    public void removePersonFromMailsList (Integer mailsListId, Integer personId){
        System.out.println(personMaillistRepositories.findByMaillistIdAndPersonId(mailsListId, personId).get());
        personMaillistRepositories.deleteById(personMaillistRepositories.findByMaillistIdAndPersonId(mailsListId, personId).get().getId());
    }

/** Отправляем письмо полученное с фронтенда */
    public String sendMessageToSagalov(Model model, MessageFrom message, Principal principal){
        if(message.getText().equals("") && message.getFile().getSize() == 0){
            model.addAttribute("messageError", "Сообщение не было отправлено, т.к. в письме не было ни текста, ни файла, заменяющего собой текстовое оформление");
            return "gkj[f";
        }
        if (message.getFile().getSize() != 0 && !message.getFile().getOriginalFilename().substring(message.getFile().getOriginalFilename().indexOf(".")).equals(".mht")){
            sb.setLength(0);
            sb.append("Вы вложили файл с расширением: [").append(message.getFile().getOriginalFilename().substring(message.getFile().getOriginalFilename().indexOf(".")))
                    .append("], вложите пожалуйста файл с расширением [.mht]");
            model.addAttribute("messageError", sb.toString());
            return "gkj[f";
        }
        if (message.getMailslistId() == null){
            sb.setLength(0);
            sb.append("Сообщение не было отправленно т.к. вы не выбрали Список рассылки, выберите пожалуйста Список рассылки");
            model.addAttribute("messageError", sb.toString());
            return "gkj[f";
        }
        MessageTo messageTo = saveMessage(message, principal);
//        System.out.println(messageTo.getFile().getName());
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("from", messageTo.getFrom()));
        params.add(new BasicNameValuePair("subject", messageTo.getSubject()));
        params.add(new BasicNameValuePair("text", messageTo.getText()));
        params.add(new BasicNameValuePair("to", messageTo.getNiceTo()));
        params.add(new BasicNameValuePair("deliveryMethod", String.valueOf(messageTo.getDeliveryMethod())));
        params.add(new BasicNameValuePair("sendBodyMethod", String.valueOf(messageTo.getSendBodyMethod())));
        try (CloseableHttpClient httpClient = HttpClients.createDefault();){
            // Создать запрос на запись
//            HttpPost httpPost = new HttpPost("http://ismaks.bfa-d.ru:31880/api/send_mail");
            HttpPost httpPost = new HttpPost("http://ismaks.bfa-d.ru:31880/api/send_bodyasattach");
//            HttpPost httpPost = new HttpPost("http://10.160.3.232:7981/handbook/ldap/file");
            // Конструировать многокомпонентный порубительский строитель
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Consts.UTF_8);
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            // Передача файлов HTTP-запрос HTTP-запрос (MultiMaPtace / Form-data)
            if (message.getFile().getSize() != 0) {
                builder.addBinaryBody("image[]", messageTo.getFile(), ContentType.MULTIPART_FORM_DATA, message.getFile().getOriginalFilename());// поток файла
//                System.out.println("ЗАШЕЛВБИЛДЕР");
//                System.out.println("ИМЯ ОТПРАВЛЯЕМОГО ФАЙЛА: " + message.getFile().getOriginalFilename());
            }
            // Байт Передача http Запрос головы (приложение / json)
            ContentType contentType = ContentType.create("application/json", Consts.UTF_8);
            for (NameValuePair param : params) {
                // Передача байтового потока
                builder.addTextBody(param.getName(), param.getValue(), contentType);
            }
            org.apache.http.HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
//             выполнить представление
            org.apache.http.HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // Конвертировать содержимое ответа на строку
                return EntityUtils.toString(responseEntity, Charsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return "gkj[f";
    }

/** Конвернтируем пришедшее с фронта письмо в письмо для отправки (заменяем Id листа рассылки реальными почтовыми адресами) */
    public MessageTo createMessageTo (MessageFrom message){
//        System.out.println("ПРОВЕРЯЕМ ФАЙЛ: " + message.getFile());
        if (!message.getFile().getOriginalFilename().equals("")){
            return new MessageTo(message.getFrom(), message.getSubject(), message.getText(),
                    utilities.converPersonsToEmails(maillistRepositories.findById(message.getMailslistId()).orElseThrow().getPersons()), message.getDeliveryMethod(),
                    message.getSendBodyMethod(), saveFileFromMessage(message.getFile(), (byte) 0), message.getAttachment());
        }
        return new MessageTo(message.getFrom(), message.getSubject(), message.getText(),
                utilities.converPersonsToEmails(maillistRepositories.findById(message.getMailslistId()).orElseThrow().getPersons()), message.getDeliveryMethod(),
                message.getSendBodyMethod(), null, message.getAttachment());
    }

/** Сохраняем письмо в базе */
    public MessageTo saveMessage(MessageFrom message, Principal principal){
        MessageTo messageTo = createMessageTo(message);
        System.out.println("***************ФОРМИРУЕМ ПИСЬМО ДЛЯ СОХРАНЕНИЯ***************");
        if (maillistRepositories.findById(message.getMailslistId()).orElseThrow(() -> new RuntimeException("лист: " + message.getMailslistId() + "не найден")).toString() != null){
            Message messageForSave = new Message(fromRepository.findFromByEmail(messageTo.getFrom()),
                    messageTo.getSubject(), messageTo.getText(), userRepository.findUserByUsername(principal.getName()).orElseThrow(),
                    utilities.getEmailsFromPersonsList(utilities.mapPersonToPersonRequest(maillistRepositories.findById(message.getMailslistId()).orElseThrow(() -> new RuntimeException("лист: " + message.getMailslistId() + "не найден")).getPersons())));
            if (messageTo.getFile() != null) messageForSave.setFile_body(messageTo.getFile().getAbsolutePath());
            System.out.println("ПИСЬМО ДЛЯ СОХРАНЕНИЯ В БАЗУ: " + messageForSave.toString());
            messageRepository.save(messageForSave);
        }
        else System.out.println("ОТПРАВЛЯЮТ В ПУСТОЙ ЛИСТ!!");
        return messageTo;
    }

/** Сохраняем файл из письма в директории */
    public File saveFileFromMessage(MultipartFile file, byte b){
        sb.setLength(0);
        try {
            byte[] uploadingFileContent = file.getBytes();
            if (b == 0){
                sb.append("C:\\Data\\Handbook\\Files\\Body\\").append(utilities.generatedFileName()).append(file.getOriginalFilename());
            }
            if (b ==1){
                sb.append("C:\\Data\\Handbook\\Files\\Attachment\\").append(utilities.generatedFileName()).append(file.getOriginalFilename());
            }

            Path path = Paths.get(sb.toString());
            Files.write(path, uploadingFileContent);
            System.out.println("ПУТЬ!!!:" + sb.toString());
            return new File(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String fromToString(From from){
        return from.getEmail();
    }

    public void persondataUpdate(){

    }

    public void test (){
        try {
            System.out.println(ldapPersonRepository.getAllPersonWithEmail("ou=bfa development,ou=bfa users,dc=bfad,dc=ru"));
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
    }
     private class PersonRequestComparator implements Comparator<PersonRequest>{

         @Override
         public int compare(PersonRequest person1, PersonRequest person2) {
             return person1.getFullName().compareTo(person2.getFullName());
         }
     }
}
