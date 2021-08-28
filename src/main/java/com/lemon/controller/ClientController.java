package com.lemon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Liang Zhancheng
 * @date 2021/8/4 10:12
 */
@Controller
public class ClientController {
    @GetMapping({"/", "/index.html"})
    public String index() {
        return "index";
    }

    @GetMapping({"/admin/index.html", "/admin"})
    public String admin() {
        return "admin/index";
    }
}
