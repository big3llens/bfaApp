package ru.bfad.bfaApp.webComponent.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.bfad.bfaApp.webComponent.dto.MessageFrom;
import ru.bfad.bfaApp.webComponent.dto.PersonToMailslist;
import ru.bfad.bfaApp.webComponent.models.Mailslist;
import ru.bfad.bfaApp.webComponent.services.MailService;

import java.security.Principal;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
@CrossOrigin
public class MailController {

    @Autowired
    private MailService ldapPersonService;

//    @GetMapping("")
//    public String index(){
//        return "index";
//    }

    @GetMapping("/home")
    public String index(){
        return "index";
    }

    @GetMapping("/mailingService")
    public String mailingService(){
        return "mailing/mailingServicee";
    }

    @RequestMapping("login")
    public String login() {
        return "mailing/login";
    }

    @RequestMapping("login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "mailing/login";
    }

    @RequestMapping("sendMessage")
    public String showSendMessage(Model model){
        ldapPersonService.initSendMail(model);
        return "mailing/sendMessage";
    }

    @GetMapping("mailsList/{id}")
    public String mailsList(@PathVariable Integer id, Model model){
        ldapPersonService.getMailslist(id, model);
        return "mailing/showMailsList";
    }

    @GetMapping("mailsLists")
    public String mailsLists(Model model){
        ldapPersonService.getAllMaillists(model);
        return "mailing/showMailsLists";
    }

    @GetMapping("saveMailsList")
    public RedirectView saveMailsList(@RequestParam(name = "nameMailslist") String name){
        ldapPersonService.saveMaillist(new Mailslist(name));
        return new RedirectView("mailsLists");
    }

    @GetMapping("addPersonToMailList")
    public String addPersonToMailList(Model model){
        return "mailing/addPersonToMailList";
    }

    @GetMapping("addPersonToMailListUtil")
    public String addPersonToMailList(Model model, @RequestParam(required = false, defaultValue = "ou=bfa development,ou=bfa users,dc=bfad,dc=ru") String nameCompany,
                                      @RequestParam(required = false, name = "listId") Integer listId, @RequestParam(required = false, name = "email") String email,
                                      @RequestParam(required = false, name = "fullName") String fullName, @RequestParam(required = false, name = "indexSort") Integer indexSort){
        System.out.println(nameCompany);
        System.out.println(listId);
        System.out.println(email);
//        System.out.println("id: " + listId);
        if(model.getAttribute("mailsList") == null) ldapPersonService.getAllMaillists(model);
        if (email != null && listId != null){
            ldapPersonService.addPersonToMailslist(model, nameCompany, new PersonToMailslist(listId, email, fullName));
            return "mailing/addPersonToMailList";
        }
        if(listId != null && indexSort != null){
            System.out.println("СОРТИНДЕКС: " + indexSort);
            ldapPersonService.getEmployeesOffTheMailslist(model, nameCompany, listId, indexSort);
            return "mailing/addPersonToMailList";
        }
        if(listId != null){
            ldapPersonService.getEmployeesOffTheMailslist(model, nameCompany, listId, 1);
            return "mailing/addPersonToMailList";
        }
        System.out.println("вывел");
        ldapPersonService.getAllPersonWithEmail(model, nameCompany);
        return "mailing/addPersonToMailList";
    }

    @GetMapping("preparationMailsLists")
    public RedirectView preparationMailsLists (){
        return new RedirectView("mailsLists");
    }

    @PostMapping("message")
    public String sendMessage(@ModelAttribute MessageFrom message, Model model,
                              @RequestParam(name = "deliveryMethod") Integer deliveryMethod, @RequestParam(name = "sendBodyMethod") Integer sendBodyMethod, Principal principal){
        model.addAttribute("messageFrom", new MessageFrom());
        ldapPersonService.initSendMail(model);
        message.setDeliveryMethod(deliveryMethod);
        message.setSendBodyMethod(sendBodyMethod);
        System.out.println(message.getFile().getOriginalFilename());
//        System.out.println(message.toString());
//        ldapPersonService.sendMessage(message, " ");
//        System.out.println(ldapPersonService.sendMessage2(message));

//        Закомментил для теста
        ldapPersonService.sendMessageToSagalov(model, message, principal);

//        System.out.println(ldapPersonService.sendMessageToSagalov(message));
        return "mailing/sendMessage";
    }

    @GetMapping("/removePerson")
    public RedirectView removePersonFromMailsList(@RequestParam Integer mailsListId, @RequestParam Integer personId){
        ldapPersonService.removePersonFromMailsList(mailsListId, personId);
        return new RedirectView("mailsList/" + mailsListId);
    }


    @GetMapping("/test")
    public String test(){
        ldapPersonService.test();
//        model.addAttribute()
        return "index";
    }
}
