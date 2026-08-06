package rlmf.grpc;

import java.io.File;
import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import rlmf.grpc.daos.ConversationDAOImpl;
import rlmf.grpc.services.ConversationServiceImpl;
import rlmf.grpc.services.DummyServiceImpl;

public class GRPCServer {

	public static void main(String[] args) throws IOException, InterruptedException {
		int port= 50051;

		String path = "C:\\Workspace\\gRPC-Client-Server\\src\\main\\resources\\ssl";

		Server server = ServerBuilder
				.forPort(port)
				.useTransportSecurity(
						new File(path + "\\server.crt"), 
						new File(path + "\\server.key"))
				.addService(new ConversationServiceImpl(new ConversationDAOImpl()))
				.build();
		server.start();
		System.out.println("Server started. Listening on port: " + port);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> 
		{
			System.out.println("Shutting down");
			server.shutdown();
			System.out.println("Server stopped");
		}));

		server.awaitTermination();
	}
}