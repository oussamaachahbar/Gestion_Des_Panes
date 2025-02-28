package com.GestionPanne.GestionPanne.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwimlController {

    @GetMapping("/twiml")
    public String getTwiml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Response>\n" +
                "    <Say voice=\"alice\">Bonjour, ceci est un test d'appel Twilio.</Say>\n" +
                "</Response>";
    }
}