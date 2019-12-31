package com.bioauth.bioauth.User.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping(value = {"/index", ""})
    public String viewIndex() {
        return "index";
    }
}