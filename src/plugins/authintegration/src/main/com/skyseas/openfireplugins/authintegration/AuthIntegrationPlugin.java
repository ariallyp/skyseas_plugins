package com.skyseas.openfireplugins.authintegration;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.auth.AuthorizationManager;
import org.jivesoftware.openfire.auth.AuthorizationPolicy;
import org.jivesoftware.openfire.auth.DefaultAuthProvider;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.user.DefaultUserProvider;
import org.jivesoftware.util.Encryptor;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AuthIntegrationPlugin  implements Plugin {
	private Logger logger = LoggerFactory.getLogger(AuthIntegrationPlugin.class);
	private String updatePropertirySQL = "UPDATE ofProperty SET propValue=? WHERE name=?";

	private ArrayList<AuthorizationPolicy> defaultAuthorizationPolicies = new ArrayList<AuthorizationPolicy>();
	
	private String providerAuthClassNameKey = "provider.auth.className";
	private String providerUserClassNameKey = "provider.user.className";

	private String defaultProviderUserClassName = XUserProvider.class.getName();//DefaultUserProvider.class.getName();
	private String defaultProviderAuthClassname = XAuthProvider.class.getName();//DefaultAuthProvider.class.getName();

	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		defaultAuthorizationPolicies.addAll(AuthorizationManager.getAuthorizationPolicies());
		AuthorizationManager.getAuthorizationPolicies().clear();
		AuthorizationManager.getAuthorizationPolicies().add(new XAuthorizationPolicy());
		
		JiveGlobals.setProperty(providerUserClassNameKey, XUserProvider.class.getName());
		JiveGlobals.setProperty(providerAuthClassNameKey, XAuthProvider.class.getName());
		
		updateProperty(providerUserClassNameKey, defaultProviderUserClassName);
		updateProperty(providerAuthClassNameKey, defaultProviderAuthClassname);

		System.out.println("auth init succeed!");
	}

	public void destroyPlugin() {
		AuthorizationManager.getAuthorizationPolicies().clear();
		AuthorizationManager.getAuthorizationPolicies().addAll(defaultAuthorizationPolicies);

		JiveGlobals.setProperty(providerUserClassNameKey, defaultProviderUserClassName);
		JiveGlobals.setProperty(providerAuthClassNameKey, defaultProviderAuthClassname);
		
		System.out.println("auth destroy succeed!");
	}

	private void updateProperty(String name, String value) {
		Encryptor encryptor = getEncryptor();
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(updatePropertirySQL);
			pstmt.setString(1, JiveGlobals.isPropertyEncrypted(name) ? encryptor.encrypt(value) : value);
			pstmt.setString(2, name);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
	}

	private Encryptor getEncryptor() {
		return JiveGlobals.getPropertyEncryptor();
	}

}
