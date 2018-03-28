package com.skyseas.openfireplugins.authintegration;

import java.util.Date;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.DefaultUserProvider;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.JID;

public class XUserProvider extends DefaultUserProvider {

public XUserProvider() {
	// TODO Auto-generated constructor stub
}

@Override
	public User loadUser(String username) throws UserNotFoundException {
		// TODO Auto-generated method stub
	System.out.println(username);
	
    if(username.contains("admin")){
    	return super.loadUser(username);
    }
	
    if(username.contains("@")) {
        if (!XMPPServer.getInstance().isLocal(new JID(username))) {
            throw new UserNotFoundException("Cannot load user of remote server: " + username);
        }
        username = username.substring(0,username.lastIndexOf("@"));
    }
    
    try {
    	
    	
        String name = "1111";
        String email = "test@qq.com";
        Date creationDate = new Date();
        Date modificationDate = new Date();

        return new User(username, name, email, creationDate, modificationDate);
    }catch (Exception e) {
        throw new UserNotFoundException(e);
    }
	
	}
}
