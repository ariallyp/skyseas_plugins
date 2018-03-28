package com.skyseas.openfireplugins.pushofflinemsg;

public class Device {
	
 private String token;
 
 //0：android 1：apple
 private String OS;
 
 private String brand;
 
 private String user;
 
 private String jid;

public String getJid() {
	return jid;
}

public void setJid(String jid) {
	this.jid = jid;
}

public String getToken() {
	return token;
}

public void setToken(String token) {
	this.token = token;
}

public String getOS() {
	return OS;
}

public void setOS(String oS) {
	OS = oS;
}

public String getBrand() {
	return brand;
}

public void setBrand(String brand) {
	this.brand = brand;
}

public String getUser() {
	return user;
}

public void setUser(String user) {
	this.user = user;
}
 
}
