package ru.bfad.bfaApp.bots.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.bfad.bfaApp.bots.dto.BelongResponse;
import ru.bfad.bfaApp.bots.dto.RegistrationResponse;
import ru.bfad.bfaApp.bots.services.TelegramBotService;

@Controller
@RequestMapping("/bfaItBot")
@RequiredArgsConstructor
@CrossOrigin
public class BotController {
    private final TelegramBotService telegramBotService;

    @PostMapping("/ismaxResponse")
    @ResponseBody
    public String sendResponseToAgent(@RequestBody BelongResponse response){
        System.out.println("вошли в контроллер");
        return telegramBotService.sendBelongResponse(response);
    }

    @PostMapping("/registrationResponse")
    @ResponseBody
    public String registrationResponse(@RequestBody RegistrationResponse registrationResponse){
        System.out.println(registrationResponse.toString());
        return telegramBotService.saveAgent(registrationResponse);
    }

    @PostMapping("/bookingResponse")
    @ResponseBody
    public String bookingResponse(@RequestBody RegistrationResponse registrationResponse){
        System.out.println(registrationResponse.toString());
        return "{\"status\":1, \"note\":\"Пока только принимаю запросы\"}";
    }
}
