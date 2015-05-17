package org.model;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
public class AboutMessage implements JSONAware {

	private String userName;
	private String message;
	private int idNumber;
	private boolean edit;
	private boolean delete;
	
	public AboutMessage(){
		userName = "1";
		idNumber = -1;	
		message = "";	
	}
	public AboutMessage(String message,String userName){
		this.userName = userName;
		this.message = message;
		idNumber = -1;		
	}
	public AboutMessage(int id, String name, String mess ){
		idNumber = id;
		userName = name;
		message = mess;
	}
	public String getUserName()
	{
		return userName;
	}
	public void setMessage(String message){
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setIdNumber(int idNumber){
		this.idNumber = idNumber;
	}
	public int getIdNumber(){
		return idNumber;
	}
	public void setEdit(boolean edit){
		this.edit = edit;
	}
	public boolean getEdit(){
		return edit;
	}
	public void setDelete(boolean delete){
		this.delete = delete;
	}
	public boolean isDelete(){
		return delete;
	}
	
	public void deleteMessage(){
		if(delete != true){
			this.message = "Deleted message";
			this.setDelete(true);
		}
	}
	
	public static AboutMessage parseInfoMessage(JSONObject obj){
		AboutMessage information = new AboutMessage();
		if((String)obj.get("user") != null){
		    information.userName = (String)obj.get("user");
		}
		information.message = (String)obj.get("message");
		if (obj.get("id") != null){
		    information.idNumber = Integer.parseInt(obj.get("id").toString());
		}
		return information;
	}	
	public String toJSONString(){
		JSONObject obj = new JSONObject();
		obj.put("user", userName);
		obj.put("message", message);
		obj.put("id", idNumber);
		return obj.toString();
	}
	@Override
	public String toString(){
		return userName+" : "+message;
	}
	@Override
	public boolean equals(Object obj){
		return (((AboutMessage)obj).getIdNumber()==idNumber);
	}
}
