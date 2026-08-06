package rlmf.grpc.dtos;

public class NewConversationDTO {

	private String sender;
	private String destinatary;
	private String textMessage;
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getDestinatary() {
		return destinatary;
	}
	public void setDestinatary(String destinatary) {
		this.destinatary = destinatary;
	}
	public String getTextMessage() {
		return textMessage;
	}
	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}
	
	
	
}
