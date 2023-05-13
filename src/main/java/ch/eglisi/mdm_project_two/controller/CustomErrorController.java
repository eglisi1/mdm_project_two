package ch.eglisi.mdm_project_two.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public class CustomErrorController implements ErrorController {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {
        logger.debug("Error page called");
        Object status = request.getAttribute("javax.servlet.error.status_code");
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            httpStatus = HttpStatus.valueOf(statusCode);
        }

        logger.error("Error page called with status: " + httpStatus.value() + " " + httpStatus.getReasonPhrase());
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", httpStatus.value());
        modelAndView.addObject("error", httpStatus.getReasonPhrase());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject("error", ex.getMessage());
        return modelAndView;
    }
}
