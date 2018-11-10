package ru.memoscope.buffer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import ru.memoscope.BufferGrpc.*;
import ru.memoscope.BufferProto.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Buffer extends BufferImplBase {

  private Server server;
  private MemesLoader loader;

  public Buffer() throws IOException {
    Properties property = new Properties();
    property.load(new FileInputStream("src/main/resources/buffer.properties"));
    int id = Integer.parseInt(property.getProperty("vk.appId"));
    int port = Integer.parseInt(property.getProperty("buffer.port"));
    String token = property.getProperty("vk.token");
    server = ServerBuilder.forPort(port).addService(this).build();
    loader = new MemesLoader(id, token, property);
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
                         StreamObserver<GetNewPostResponse> responseObserver) {
    System.out.println("Got request");
    Post post = loader.getLatestPost();
    while (post == null) {
      post = loader.getLatestPost();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
    }
    GetNewPostResponse response = GetNewPostResponse.newBuilder()
        .setPost(post)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }




  public void blockUntilShutDown() throws InterruptedException {
    server.awaitTermination();
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    Buffer server = new Buffer();
    server.start();
    server.loader.startDownload();
    server.blockUntilShutDown();
  }
}
