package com.example.profileset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProFileController {

    @Value("${me.name}")
    public List<String> name;

    @GetMapping("/")
    public List<String> name(){

        return name;
    }
}
