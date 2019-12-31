package com.bioauth.bioauth.User.service;

import java.security.Principal;

import com.digitalpersona.uareu.UareUException;

public interface BiometricServ {

	void testIt(Principal principal) throws UareUException;

}
