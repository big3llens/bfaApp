package ru.bfad.bfaApp.webComponent.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.bfad.bfaApp.bots.services.TelegramBotService;
import ru.bfad.bfaApp.webComponent.dto.SupportMessage;
import ru.bfad.bfaApp.webComponent.services.MailService;

@Controller
@RequestMapping("/support")
@RequiredArgsConstructor
@CrossOrigin
public class SupportController {
    @Autowired
    private TelegramBotService botService;

    @Autowired
    private MailService ldapPersonService;

    @GetMapping("index")
    public String handbookIndex(Model model){
        model.addAttribute("supportMessage", new SupportMessage());
        return "support/supportIndex";
    }

    @PostMapping("message")
    public String message(@ModelAttribute SupportMessage message, Model model){
        model.addAttribute("supportMessage", new SupportMessage());
        System.out.println(message.toString());
        botService.sendMessageToSupport(message, model);
        return "support/supportIndex";
    }
}
