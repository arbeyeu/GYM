package com.ironcore.gym.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object throwable = request.getAttribute("jakarta.servlet.error.exception");

        if (throwable instanceof Exception ex) {
            log.error("Framework error status={} rendering {} : {}",
                    status, request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI),
                    ex.getMessage(), ex);
        } else {
            log.warn("Framework error status={} uri={} : {}",
                    status, request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI), message);
        }

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("status", statusCode);

            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403";
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            }
        }

        model.addAttribute("status", status != null ? status : 500);
        model.addAttribute("message", message != null ? message : "An unexpected server error occurred.");
        return "error/500";
    }

    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("message", "Access Denied: You do not have permissions for this section.");
        return "error/403";
    }

    @GetMapping("/404")
    public String notFound(Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("message", "The page or resource you requested could not be found.");
        return "error/404";
    }

    @GetMapping("/500")
    public String serverError(Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("message", "An unexpected system exception occurred.");
        return "error/500";
    }
}
