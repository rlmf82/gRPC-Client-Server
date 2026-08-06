package rlmf.grpc.services;

import grpc.conversation.ConversationServiceGrpc.ConversationServiceImplBase;
import grpc.conversation.*;
import io.grpc.stub.StreamObserver;
import rlmf.grpc.daos.ConversationDAO;
import rlmf.grpc.daos.ConversationDAOImpl;
import rlmf.grpc.entities.Conversation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConversationServiceImpl extends ConversationServiceImplBase {
	
	private final ConversationDAO dao;
	
	public ConversationServiceImpl(ConversationDAO dao) {
		this.dao = dao;
	}
	
	@Override
	public void save(NewConversationRequest request, StreamObserver<NewConversationResponse> responseObserver) {
		
		Conversation conversation = new Conversation(UUID.randomUUID(), request.getDestinatary(), request.getSender(), request.getTextMessage());
		
		this.dao.save(conversation);
		
		responseObserver.onNext(NewConversationResponse.newBuilder().setResult("New conversation saved").build());
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

	@Override
	public StreamObserver<GetConversationInfoRequest>
		getConversationInfo(StreamObserver<GetConversationInfoResponse> responseObserver){

		return new StreamObserver<GetConversationInfoRequest>() {
			private final ConversationDAO dao = new ConversationDAOImpl();

			@Override
			public void onNext(GetConversationInfoRequest request) {

				//1-Number of Senders, 2-Number of Destinataries, 3-Number of Messages
				String response = null;

				switch (request.getType()){
					case "1": {
						response = "Number of senders: " + dao.getInformation(request.getType());
						break;
					}
					case "2": {
						response = "Number of destinations: " + dao.getInformation(request.getType());
						break;
					}
					case "3": {
						response = "Number of messages: " + dao.getInformation(request.getType());
						break;
					}
					default:
						throw new IllegalStateException("Unexpected value: " + request.getType());
				}

				responseObserver.onNext(GetConversationInfoResponse.newBuilder().setResponse(response).build());
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("Client cancelled.");
			}

			@Override
			public void onCompleted() {
				responseObserver.onCompleted();
			}
		};
	}
}
