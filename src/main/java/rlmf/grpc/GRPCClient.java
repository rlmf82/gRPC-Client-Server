package rlmf.grpc;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import grpc.DummyRequest;
import grpc.DummyResponse;
import grpc.DummyServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GRPCClient {

	public static void main(String[] args) throws IOException, InterruptedException {

		System.out.println("Connecting to the channel");
		ManagedChannel channel = ManagedChannelBuilder
				.forAddress("localhost", 50051)
				.usePlaintext()
				.build();
		System.out.println("Channel connected");

		System.out.println("Calling gRPC");
		//callBlockingStub(channel);

		callStub(channel);

		System.out.println("Shutting Down");
		channel.shutdown();
	}

	public static void callBlockingStub(ManagedChannel channel) {
		DummyServiceGrpc.DummyServiceBlockingStub stub = DummyServiceGrpc.newBlockingStub(channel);

		//DummyResponse response = stub.sum(DummyRequest.newBuilder().setNumber1(8).setNumber2(42).build());

		Iterator<DummyResponse> response = stub.manyTimes(DummyRequest.newBuilder().setName("Rafael").build());
		response.forEachRemaining(c -> System.out.println(c.getResult()));
	}

	public static void callStub(ManagedChannel channel) {
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
			StreamObserver<DummyRequest> requestObserver = stub.callNames(responseObserver);

			for(String name: names) {
				requestObserver.onNext(
						DummyRequest.newBuilder()
						.setName(name)
						.build());        	
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}