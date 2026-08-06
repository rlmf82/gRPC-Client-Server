package rlmf.grpc.entities;

import java.util.UUID;

public class Conversation {

	private UUID id;
	private String destinatary;
	private String sender;
	private String message;
	
	public Conversation(UUID id, String destinatary, String sender, String message) {
		super();
		this.id = id;
		this.destinatary = destinatary;
		this.sender = sender;
		this.message = message;
	}
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getDestinatary() {
		return destinatary;
	}
	public void setDestinatary(String destinatary) {
		this.destinatary = destinatary;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}