package com.bioauth.bioauth.User.service;

import java.security.Principal;
import org.springframework.stereotype.Service;
import com.digitalpersona.uareu.*;

@Service
public class BiometricServImpl implements BiometricServ {

	@Override
	public void testIt(Principal principal) throws UareUException {
		ReaderCollection m_collection = UareUGlobal.GetReaderCollection();
		
		try{
			m_collection.GetReaders();
		} 
		catch(UareUException e) { 
			System.out.printf("ReaderCollection.GetReaders()", e.getMessage());
		}
		
		//SaveFingerprint.Run(m_collection.get(0), false, principal);
		
	}

}
