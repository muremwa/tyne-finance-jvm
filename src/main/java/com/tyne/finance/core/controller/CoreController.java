package com.tyne.finance.core.controller;

import com.tyne.finance.configurations.ConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller()
@RequestMapping("/core")
public class CoreController {
    private final ConfigProperties properties;


    @Autowired
    public CoreController(ConfigProperties properties) {
        this.properties = properties;

        System.out.println(properties);
    }

    @GetMapping("")
    public String coreHome(@RequestParam(defaultValue = "Daniel") String name, Model model) {
        model.addAttribute("name", name);
        return "home";
    }
}
