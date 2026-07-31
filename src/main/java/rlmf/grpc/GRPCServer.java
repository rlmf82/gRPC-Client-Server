package rlmf.grpc;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import rlmf.grpc.services.DummyServiceImpl;

public class GRPCServer {

	public static void main(String[] args) throws IOException, InterruptedException {
		int port= 50051;
		Server server = ServerBuilder
				.forPort(port)
				.addService(new DummyServiceImpl())
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