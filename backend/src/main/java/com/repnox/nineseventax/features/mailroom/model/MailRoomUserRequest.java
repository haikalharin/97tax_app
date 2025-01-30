package com.repnox.nineseventax.features.mailroom.model;

public class MailRoomUserRequest {

	private Long userNum;
	
	private String userName;
	
	private String email;
	
	private String phone;
	
	private String password;
	
	private boolean update;
	
	public Long getUserNum() {
        return userNum;
    }

    public void setUserNum(Long userNum) {
        this.userNum = userNum;
    }
    
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhone() {
		return phone;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
	
	public void setUpdate(boolean update) {
		this.update = update;
	}
	public boolean getUpdate() {
		return update;
	}
}
