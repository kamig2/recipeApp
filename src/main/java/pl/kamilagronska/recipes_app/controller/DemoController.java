package pl.kamilagronska.recipes_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class DemoController {

    @GetMapping("/demo")
    public String demo(){
        return "Zaszyfrowana srtrona";
    }

}
