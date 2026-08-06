package rlmf.grpc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import grpc.DummyRequest;
import grpc.DummyResponse;
import grpc.DummyServiceGrpc;
import grpc.ErrorRequest;
import grpc.ErrorResponse;
import grpc.conversation.ConversationServiceGrpc;
import grpc.conversation.GetConversationResponse;
import grpc.conversation.UpdateConversationRequest;
import grpc.conversation.UpdateConversationResponse;
import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import io.grpc.stub.StreamObserver;
import rlmf.grpc.dtos.NewConversationDTO;
import rlmf.grpc.services.ClientConversationServiceImpl;

public class GRPCClient {


	private static ClientConversationServiceImpl service = null;

	public static void main(String[] args) throws IOException, InterruptedException {
		Scanner scanner = new Scanner(System.in);
		String credentialsPath = "C:\\Workspace\\gRPC-Client-Server\\src\\main\\resources\\ssl";
		String sender = null;

		System.out.println("Connecting to the channel");

		ChannelCredentials creds = TlsChannelCredentials
				.newBuilder()
				.trustManager(new File(credentialsPath + "\\server.crt")).build();

		ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 50051, creds).build();
		service = new ClientConversationServiceImpl(channel);

		System.out.println("Channel connected");

		String menu = """
				Select the option

				 1 - Create a conversation
				 2 - List my conversations
				 3 - Open a conversation
				99 - Exit

				Select an option: """;

		mainFlow: while(true) {

			if(sender == null) {
				sender = showGreetings(scanner);	
			}

			System.out.print(menu);

			String choice = scanner.next();

			switch(choice) {
			case "1" -> createNewConversation(scanner, sender);
			case "2" -> listAllConversations(scanner, sender);
			case "3" -> updateMessage(scanner, sender, channel);
			default -> { break mainFlow;}
			}

			//gettingError(channel, 14);
			//gettingError(channel, -3);
		}
		
		scanner.close();
		channel.shutdown();
		System.out.println("Shutting Down");
	}

	private static String updateMessage(Scanner scanner, String sender, ManagedChannel channel) {

		System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
		System.out.println("Inform the conversation id.\n");
		String id = scanner.next();
		scanner.nextLine();
		
		try {
			ConversationServiceGrpc.ConversationServiceStub stub = ConversationServiceGrpc.newStub(channel);
			
			CountDownLatch finishLatch = new CountDownLatch(1);

			// Receives the server's single response
			StreamObserver<UpdateConversationResponse> responseObserver =
					new StreamObserver<UpdateConversationResponse>() {

				@Override
				public void onNext(UpdateConversationResponse response) {
					System.out.println(response.getResult());
				}

				@Override
				public void onError(Throwable t) {
					System.err.println("RPC failed: " + t.getMessage());
					finishLatch.countDown();
				}

				@Override
				public void onCompleted() {
					finishLatch.countDown();
				}
			};

			// Gets the stream used to send requests
			StreamObserver<UpdateConversationRequest> requestObserver = stub.update(responseObserver);

			while(true) {
				System.out.println("What would you like to say?");
				UpdateConversationRequest request = UpdateConversationRequest
						.newBuilder()
						.setId(id)
						.setTextMessage(scanner.nextLine())
						.build();
				requestObserver.onNext(request);
				
				System.out.println("Anything else? [Y/N]");
				String anythingElse = scanner.next();
				scanner.nextLine();				
				
				if(!anythingElse.equalsIgnoreCase("Y")) {
					requestObserver.onCompleted();
					finishLatch.await(5, TimeUnit.SECONDS);
					break;
				}
			}
			System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
			return "Message updated";
		}catch (Exception e) {
			return e.getMessage();
		}
	}

	private static void listAllConversations(Scanner scanner, String sender) {
		System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
		System.out.println("Getting messages.\n");

		Iterator<GetConversationResponse> response = service.getMessages(sender);
		System.out.println("========================================");
		while(response.hasNext()) {
			System.out.println(response.next());
			System.out.println("---------------------------------------------");
		}
		System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
	}

	public static void createNewConversation(Scanner scanner, String sender) {
		System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
		System.out.print("Inform the destinatary: ");
		String destinatary = scanner.next();
		scanner.nextLine();
		System.out.print("Type the message: ");
		String message= scanner.nextLine();

		NewConversationDTO newConverstation = new NewConversationDTO();
		newConverstation.setDestinatary(destinatary);
		newConverstation.setSender(sender);
		newConverstation.setTextMessage(message);

		String response = service.saveConversation(newConverstation);

		System.out.println("New Conversation Response: " + response);
		System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-");
	}

	private static String showGreetings(Scanner scanner) {
		String greeting = """ 
				Hello Visitor. Please inform your name: """;
		System.out.println(greeting);
		String name = scanner.next();

		System.out.println("Welcome: " + name + ". Let's start!");
		return name;
	}

	public static void callBlockingStub(ManagedChannel channel) {
		DummyServiceGrpc.DummyServiceBlockingStub stub = DummyServiceGrpc.newBlockingStub(channel);

		//Calling with deadline.
		//stub.withDeadlineAfter(1000, TimeUnit.MILLISECONDS).gettingErrors(null);

		Iterator<DummyResponse> response = stub.serverStreaming(DummyRequest.newBuilder().setName("Rafael").build());
		response.forEachRemaining(c -> System.out.println(c.getResult()));
	}

	public static void gettingError(ManagedChannel channel, int number) {
		DummyServiceGrpc.DummyServiceBlockingStub stub = DummyServiceGrpc.newBlockingStub(channel);

		try {
			ErrorResponse response = stub.gettingErrors(ErrorRequest.newBuilder().setNumber1(number).build());
			System.out.println(response.getResult());	
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
		}

	}

	public static void biDirectionalStreaming(ManagedChannel channel) {
		try {
			DummyServiceGrpc.DummyServiceStub stub = DummyServiceGrpc.newStub(channel);

			List<String> names = Arrays.asList("Rafael", "Lucas", "de", "Melo", "Farias");

			CountDownLatch finishLatch = new CountDownLatch(1);

			// Receives the server's single response
			StreamObserver<DummyResponse> responseObserver =
					new StreamObserver<DummyResponse>() {

				@Override
				public void onNext(DummyResponse response) {
					System.out.println("Server replied:");
					System.out.println(response.getResult());
				}

				@Override
				public void onError(Throwable t) {
					System.err.println("RPC failed: " + t.getMessage());
					finishLatch.countDown();
				}

				@Override
				public void onCompleted() {
					System.out.println("RPC completed.");
					finishLatch.countDown();
				}
			};

			// Gets the stream used to send requests
			StreamObserver<DummyRequest> requestObserver = stub.biDirectionalStreaming(responseObserver);

			for(String name: names) {
				requestObserver.onNext(
						DummyRequest.newBuilder()
						.setName(name)
						.build());        	
			}

			requestObserver.onCompleted();

			finishLatch.await(5, TimeUnit.SECONDS);

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}