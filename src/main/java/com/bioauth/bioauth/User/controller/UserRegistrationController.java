package com.bioauth.bioauth.User.controller;

import com.bioauth.bioauth.User.model.User;
import com.bioauth.bioauth.User.model.UserRegistrationDTO;
import com.bioauth.bioauth.User.service.UserServ;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;

import biometric.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {
    @Autowired
    private UserServ userServ;

    @ModelAttribute("user")
    public UserRegistrationDTO userRegistrationDTO() {
        return new UserRegistrationDTO();
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        return "registration";
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDTO userDto, BindingResult result, RedirectAttributes redirect) {

        if (result.hasErrors()) {
            return "registration";
        }

        try {
            User user = userServ.save(userDto);
            ReaderCollection m_collection = UareUGlobal.GetReaderCollection();
    		
    		try{
    			m_collection.GetReaders();
    		} 
    		catch(UareUException e) { 
    			System.out.printf("ReaderCollection.GetReaders()", e.getMessage());
    		}
    		
    		SaveFingerprint.Run(m_collection.get(0), false, user);
    		redirect.addFlashAttribute("message", "Student Created Successfully");
            return "redirect:/login";
        } catch (Exception e) {
        	result.reject(e.getMessage());
            return "redirect:/registration";
        }
    }
}
