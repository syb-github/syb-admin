package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author sunyibing
 * @date 2024/4/11
 */
@Controller
@Slf4j
public class IndexController {

    @RequestMapping(path = {"/", "/login"})
    public String index(){
        return "redirect:/page/end/login.html";
    }
}
