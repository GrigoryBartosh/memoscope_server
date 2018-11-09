package ru.memoscope.server;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import ru.memoscope.ServerGrpc.*;
import ru.memoscope.ServerProto.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Server extends ServerImplBase {

  private io.grpc.Server server;

  public Server() throws IOException {
    Properties property = new Properties();
    property.load(new FileInputStream("src/main/resources/buffer.properties"));
    int port = Integer.parseInt(property.getProperty("buffer.port"));
    server = ServerBuilder.forPort(port).addService(this).build();
  }

  public void start() throws IOException {
    server.start();
    System.out.println("Server started");
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Shut down");
      server.shutdown();
    }));
  }

  @Override
  public void findPosts(FindPostsRequest request,
                      StreamObserver<FindPostsResponse> responseObserver) {
    System.out.println("Got request: " + request);
    PostInfo postInfo = PostInfo.newBuilder()
        .setGroupId(-29534144)
        .setPostId(9975266)
        .build();
    FindPostsResponse response = FindPostsResponse.newBuilder()
        .addPosts(postInfo).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }




  public void blockUntilShutDown() throws InterruptedException {
    server.awaitTermination();
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    Server server = new Server();
    server.start();
    server.blockUntilShutDown();
  }
}
