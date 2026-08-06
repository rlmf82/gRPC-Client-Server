package rlmf.grpc.services;

import java.util.Iterator;

import grpc.conversation.ConversationServiceGrpc;
import grpc.conversation.GetConversationRequest;
import grpc.conversation.GetConversationResponse;
import grpc.conversation.NewConversationRequest;
import grpc.conversation.NewConversationResponse;
import io.grpc.ManagedChannel;
import rlmf.grpc.dtos.NewConversationDTO;

public class ClientConversationServiceImpl {

	private ConversationServiceGrpc.ConversationServiceBlockingStub stub;
	
	public ClientConversationServiceImpl(ManagedChannel channel) {
		this.stub = ConversationServiceGrpc.newBlockingStub(channel);;
	}
	
	public String saveConversation(NewConversationDTO conversation) {

		//Calling with deadline.
		//stub.withDeadlineAfter(1000, TimeUnit.MILLISECONDS).gettingErrors(null);
		NewConversationRequest newConversation = NewConversationRequest.newBuilder()
		.setDestinatary(conversation.getDestinatary())
		.setSender(conversation.getSender())
		.setTextMessage(conversation.getTextMessage()).build();
		
		
		NewConversationResponse response = stub.save(newConversation);
		return response.getResult();
	}
	
	public Iterator<GetConversationResponse> getMessages(String sender) {
		return stub.get(GetConversationRequest.newBuilder().setName(sender).build());
	}

}
