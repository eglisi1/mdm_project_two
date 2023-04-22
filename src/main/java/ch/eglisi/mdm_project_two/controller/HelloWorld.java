package ch.eglisi.mdm_project_two.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorld {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorld.class);

    @GetMapping("/helloWorld")
    @ResponseBody
    public String hello() {
        logger.info("Hello World called");
        return "Hello World!";
    }
}
