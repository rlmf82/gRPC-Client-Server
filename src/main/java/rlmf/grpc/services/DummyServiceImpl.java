package rlmf.grpc.services;

import grpc.DummyServiceGrpc.DummyServiceImplBase;
import grpc.ErrorRequest;
import grpc.ErrorResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class DummyServiceImpl extends DummyServiceImplBase{

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