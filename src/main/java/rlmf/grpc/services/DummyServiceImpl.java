package rlmf.grpc.services;

import grpc.DummyRequest;
import grpc.DummyResponse;
import grpc.DummyServiceGrpc.DummyServiceImplBase;
import grpc.ErrorRequest;
import grpc.ErrorResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class DummyServiceImpl extends DummyServiceImplBase{

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
	
	@Override
	public void gettingErrors(ErrorRequest request, StreamObserver<ErrorResponse> responseObserver) {

		if(request.getNumber1() < 0) {
			responseObserver.onError(Status.INVALID_ARGUMENT
					.withDescription("The number cannot be negative")
					.augmentDescription("Number:" + request.getNumber1())
					.asRuntimeException());
		} else {
			responseObserver.onNext(ErrorResponse.newBuilder().setResult("OK").build());
		}
		
		responseObserver.onCompleted();
	}
}