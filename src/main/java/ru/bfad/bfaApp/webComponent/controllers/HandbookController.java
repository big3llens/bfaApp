package ru.bfad.bfaApp.webComponent.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.bfad.bfaApp.webComponent.services.HandbookService;

@Controller
@RequestMapping("/handbook")
@RequiredArgsConstructor
@CrossOrigin
public class HandbookController {

    @Autowired
    private HandbookService service;

    @GetMapping("/index")
    public String handbookIndex(){
        return "handbook/handbookIndex";
    }

    @GetMapping("/persons")
    public String getHandbookPersons(Model model, String company){
        service.getHandbookPersons(model, company);
        if (company.equals("ou=bfa development,ou=bfa users,dc=bfad,dc=ru")) return "handbook/development";
        return "handbook/externalCompany";
    }

//    @GetMapping("/development")
//    public String developmentPersons(Model model){
//        service.getDevelopmentPersons(model, );
//        return "handbook/development";
//    }
}
