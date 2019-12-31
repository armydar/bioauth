package com.bioauth.bioauth.User.controller;

import com.bioauth.bioauth.User.model.User;
import com.bioauth.bioauth.User.service.BiometricServ;
import com.bioauth.bioauth.User.service.UserServ;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;

import java.security.Principal;
import biometric.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class LoginController {

    private UserServ userServ;
    
    @Autowired
    private BiometricServ bioServ;

    public LoginController(UserServ userServ) {
        this.userServ = userServ;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/successPage")
    public String verify() {
        return "successPage";
    }

    @GetMapping("/test")
    @ResponseBody
    public void testAJAX(Principal principal) throws UareUException {
    	bioServ.testIt(principal);
    }

    @PostMapping("/verify")
    public String verifyStudent(@RequestParam(value = "admissionNumber") String admissionNumber, RedirectAttributes redirectAttributes, Model model) throws UareUException, Exception {
    	String result = null;
    	String msgType = null;
        User user = userServ.findByAdmissionNumber(admissionNumber);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error, User not available");
            return "redirect:/";
        } else {
        	ReaderCollection m_collection = UareUGlobal.GetReaderCollection();
    		
    		try{
    			m_collection.GetReaders();
    		} 
    		catch(UareUException e) { 
    			System.out.printf("ReaderCollection.GetReaders()", e.getMessage());
    		}
        	try {
				CompareWithDB.Run(m_collection.get(0), user);
				result = "Success, Fingerprint matches";
				msgType = "message";
			} catch (Exception e) {
				result = e.getMessage();
				msgType = "errorMessage";
			}
        	if (msgType.equalsIgnoreCase("errorMessage")) {
        		redirectAttributes.addFlashAttribute(msgType, result);
                return "redirect:/";
        	} else {
        		redirectAttributes.addFlashAttribute(msgType, result);
                return "redirect:/successPage";
        	}
            
        }
    }
}
