package com.edu_management.beans;

public class admin {

	public String getIdString() {
		return idString;
	}
	public void setIdString(String idString) {
		this.idString = idString;
	}
	public String getPwdString() {  
		return pwdString;
	}
	public void setPwdString(String pwdString) {
		this.pwdString = pwdString;
	}
	public admin(String idString, String nameString,String pwdString) {
		super();
		this.idString = idString;
		this.pwdString = pwdString;
		this.nameString = nameString;
	}
	public String getNameString() {
		return nameString;
	}
	public void setNameString(String nameString) {
		this.nameString = nameString;
	}
	private String pwdString;
	private String nameString;
	private String idString;
}
