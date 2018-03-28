package com.skyseas.openfireplugins.authintegration;

import org.apache.commons.lang.StringUtils;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.AuthProvider;
import org.jivesoftware.openfire.auth.ConnectionException;
import org.jivesoftware.openfire.auth.DefaultAuthProvider;
import org.jivesoftware.openfire.auth.InternalUnauthenticatedException;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.JiveProperties;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.jivesoftware.util.PropertyEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class XAuthProvider  extends DefaultAuthProvider implements PropertyEventListener {

	private Logger logger = LoggerFactory.getLogger(XAuthProvider.class);
	private String account_verify_enable = "account.verify.enable";
	private boolean verifyEnable;

//	private UserApi userApi;

	{
		verifyEnable = JiveProperties.getInstance().getBooleanProperty(account_verify_enable, true);
		PropertyEventDispatcher.addListener(this);
	}

	public XAuthProvider() {
//		userApi = SpringContextHolder.getBean("userApi", UserApi.class);
	}

	public void authenticate(String username, String password)throws UnauthorizedException  {

		if (username == null || password == null) {
			throw new UnauthorizedException();
		}
		
	    if(username.contains("admin")){
	    	super.authenticate(username, password);
	    	return;
	    }

		username = getUserNameForAuth(username);
System.out.println(username+"-----"+password);
//		Result<Message> result;
		if (verifyEnable) {
			return;
//			try {
//				result = userApi.validate(new UserValidatePojo(username, password));
//				
//				if( !result.isSuccess() ){
//					if(StringUtils.equalsIgnoreCase(password, getPasswordValue(username))){
//						return;
//					}
//				}
//				
//			} catch (Throwable t) {
//				logger.error("remote service invoke error!", t);
//				throw new UnauthorizedException("service error!");
//			}
//
//			if (!result.isSuccess()) {
//				logger.error("UserApi.validate fail({},{}),caurse:{}", username, password, result.getMessage()
//						.getText());
//				throw new UnauthorizedException(result.getMessage().getCode());
//			}

		} else {

		}

	}


	private String getUserNameForAuth(String username) throws UnauthorizedException {
		username = username.trim().toLowerCase();
		if (username.contains("@")) {
			// Check that the specified domain matches the server's domain
			int index = username.indexOf("@");
			String domain = username.substring(index + 1);
			if (domain.equals(XMPPServer.getInstance().getServerInfo().getXMPPDomain())) {
				username = username.substring(0, index);
			} else {
				// Unknown domain. Return authentication failed.
				throw new UnauthorizedException();
			}
		}
		return username;
	}

	@Override
	public String getPassword(String username) throws UserNotFoundException {
		// TODO Auto-generated method stub
		return "123456";
	}

	
	public void propertySet(String property, Map<String, Object> params) {
		if (account_verify_enable.equals(property)) {
			String value = (String) params.get("value");
			if (value != null) {
				verifyEnable = Boolean.parseBoolean(value);
			}
		}

	}

	public void propertyDeleted(String property, Map<String, Object> params) {
		if (account_verify_enable.equals(property)) {
			verifyEnable = false;
		}
	}

	@Override
	public void xmlPropertySet(String property, Map<String, Object> params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void xmlPropertyDeleted(String property, Map<String, Object> params) {
		// TODO Auto-generated method stub
		
	}


}
