package com.example.library_management_v2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {


    @GetMapping("/")
    public Map<String, Object> home(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Välkommen till Library Management System!");
        response.put("page", "home");

        // Kolla om användaren är inloggad
        if (principal != null) {
            response.put("user", principal.getName()); // Detta blir email:en
            response.put("authenticated", true);
            response.put("info", "Du är inloggad! Du kan komma åt /books nu.");
        } else {
            response.put("authenticated", false);
            response.put("info", "Du är inte inloggad. Denna sida kan alla se, men för att komma åt /books måste du logga in.");
        }

        return response;
    }

    /**
     * Alternativ URL för startsidan
     */
    @GetMapping("/home")
    public Map<String, Object> homeAlternative(Principal principal) {
        Map<String, Object> response = home(principal);
        response.put("page", "home-alternative");
        return response;
    }


    @GetMapping("/public/info")
    public Map<String, Object> publicInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Detta är publik information om systemet");
        response.put("version", "2.0");
        response.put("features", new String[]{"Bokhantering", "Användarhantering", "Lånesystem"});
        response.put("security_note", "Denna sida kräver ingen inloggning");
        return response;
    }
}