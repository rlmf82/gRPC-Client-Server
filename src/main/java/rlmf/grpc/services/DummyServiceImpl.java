package rlmf.grpc.services;

import grpc.DummyRequest;
import grpc.DummyResponse;
import grpc.DummyServiceGrpc.DummyServiceImplBase;
import io.grpc.stub.StreamObserver;

public class DummyServiceImpl extends DummyServiceImplBase{

	@Override
	public void unaryType(DummyRequest request, StreamObserver<DummyResponse> responseObserver) {
		responseObserver.onNext(DummyResponse.newBuilder().setResult(String.format("Hello %s. Your RPC works.", request.getName())).build());
		responseObserver.onCompleted();
	}


	@Override
	public void serverStreaming(DummyRequest request, StreamObserver<DummyResponse> responseObserver) {

		DummyResponse response = DummyResponse.newBuilder().setResult(String.format("Hello %s.", request.getName())).build();

		for(int i = 0; i < 10; i++) {
			responseObserver.onNext(response);
			System.out.println("message: " + i +" sent");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		responseObserver.onCompleted();
	}

	@Override
	public StreamObserver<DummyRequest> clientStreaming(StreamObserver<DummyResponse> responseObserver) {

		return new StreamObserver<DummyRequest>() {

			StringBuilder names = new StringBuilder();

			@Override
			public void onNext(DummyRequest request) {
				names.append(request.getName()).append(" ");
				System.out.println("Received: " + request.getName());
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("Client cancelled.");
			}

			@Override
			public void onCompleted() {

				DummyResponse response = DummyResponse.newBuilder()
						.setResult("Hello " + names.toString())
						.build();

				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}
		};
	}
	
	@Override
	public StreamObserver<DummyRequest> biDirectionalStreaming(StreamObserver<DummyResponse> responseObserver) {

		return new StreamObserver<DummyRequest>() {

			@Override
			public void onNext(DummyRequest request) {
				if(request.getName().length() > 5) {
					responseObserver.onNext(DummyResponse.newBuilder().setResult("Hello: " + request.getName()).build());
				}
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