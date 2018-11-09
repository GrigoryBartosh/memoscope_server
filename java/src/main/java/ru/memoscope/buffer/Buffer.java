package ru.memoscope.buffer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import ru.memoscope.BufferGrpc.*;
import ru.memoscope.BufferProto.*;

import java.io.IOException;

public class Buffer extends BufferImplBase {

  private Server server;
  private MemesLoader loader;

  public Buffer(int port) {
    server = ServerBuilder.forPort(port).addService(this).build();
    loader = new MemesLoader(6746791,
        "2ac587c0917cc2cee6727a4476408c04bf8fd4c9b7c287ca7e1061854e47a5b0a5235d7fbb767bad919f1");
  }

  public void start() throws IOException {
    server.start();
    System.out.println("Buffer started");
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Buffer shut down");
      server.shutdown();
    }));
  }

  @Override
  public void getNewPost(GetNewPostRequest request,
                         io.grpc.stub.StreamObserver<GetNewPostResponse> responseObserver) {
  }




  public void blockUntilShutDown() throws InterruptedException {
    server.awaitTermination();
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    int port = Integer.parseInt(args[0]);
    Buffer server = new Buffer(port);
    //server.start();
    //server.blockUntilShutDown();
  }
}
