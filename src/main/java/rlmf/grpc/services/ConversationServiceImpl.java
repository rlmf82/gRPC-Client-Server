package rlmf.grpc.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import grpc.conversation.ConversationServiceGrpc.ConversationServiceImplBase;
import grpc.conversation.GetConversationRequest;
import grpc.conversation.GetConversationResponse;
import grpc.conversation.NewConversationRequest;
import grpc.conversation.NewConversationResponse;
import grpc.conversation.UpdateConversationRequest;
import grpc.conversation.UpdateConversationResponse;
import io.grpc.stub.StreamObserver;
import rlmf.grpc.daos.ConversationDAO;
import rlmf.grpc.daos.ConversationDAOImpl;
import rlmf.grpc.entities.Conversation;

public class ConversationServiceImpl extends ConversationServiceImplBase {
	
	private ConversationDAO dao;
	
	public ConversationServiceImpl(ConversationDAO dao) {
		this.dao = dao;
	}
	
	@Override
	public void save(NewConversationRequest request, StreamObserver<NewConversationResponse> responseObserver) {
		
		Conversation conversation = new Conversation(UUID.randomUUID(), request.getDestinatary(), request.getSender(), request.getTextMessage());
		
		this.dao.save(conversation);
		
		responseObserver.onNext(NewConversationResponse.newBuilder().setResult(String.format("New conversation saved")).build());
		responseObserver.onCompleted();
	}
	
	@Override
	public void get(GetConversationRequest request, StreamObserver<GetConversationResponse> responseObserver) {

		List<Conversation> conversations = this.dao.retrieveMyConversations(request.getName());
		
		conversations.forEach(c -> {
			GetConversationResponse response = GetConversationResponse
				.newBuilder()
				.setTextMessage(c.getMessage())
				.setDestinatary(c.getDestinatary())
				.setSender(c.getSender())
				.setUuid(c.getId().toString())
				.build();
			
			responseObserver.onNext(response);
		});

		responseObserver.onCompleted();
	}

	@Override
	public StreamObserver<UpdateConversationRequest> update(StreamObserver<UpdateConversationResponse> responseObserver){

		return new StreamObserver<UpdateConversationRequest>() {

			private ConversationDAO dao = new ConversationDAOImpl();
			private String id;
			
			@Override
			public void onNext(UpdateConversationRequest request) {
				id = request.getId();
				Optional<Conversation> conversation = this.dao.getById(UUID.fromString(request.getId()));
				
				if(conversation.isPresent()) {
					Conversation updated = conversation.get();
					StringBuilder message = new StringBuilder(updated.getMessage());
					message.append("\t;" + request.getTextMessage());
					
					updated.setMessage(message.toString());
					dao.update(updated);
				}
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("Client cancelled.");
			}

			@Override
			public void onCompleted() {

				UpdateConversationResponse response = UpdateConversationResponse.newBuilder()
						.setResult(String.format("Conversation %s updated.", id))
						.build();

				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}
		};
	}
}
