package ru.memoscope.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import ru.memoscope.ServerGrpc.*;
import ru.memoscope.ServerProto.*;
import ru.memoscope.MessagesProto.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static ru.memoscope.ServerGrpc.newBlockingStub;

public class Server extends ServerImplBase {

  private io.grpc.Server server;
  List<Long> groups;
  ServerBlockingStub blockingStub;


  public Server() throws IOException {
    Properties property = new Properties();
    property.load(new FileInputStream("src/main/resources/server.properties"));
    int port = Integer.parseInt(property.getProperty("server.port"));
    int dbPort = Integer.parseInt(property.getProperty("db.port"));
    String dbHost = property.getProperty("db.port");
    server = ServerBuilder.forPort(port).addService(this).build();
    loadGroups(property);
    System.out.println(groups);

//    ManagedChannel channel = ManagedChannelBuilder.forAddress(dbHost, dbPort)
//        .usePlaintext(true)
//        .build();
//    blockingStub = newBlockingStub(channel);
  }

  private void loadGroups(Properties property) {
    groups = new ArrayList<>();
    String jsonString = property.getProperty("server.groups");
    JsonArray json = (JsonArray)new JsonParser().parse(jsonString);
    for (JsonElement el : json) {
      groups.add(el.getAsLong());
    }
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

  @Override
  public void getGroups(GetGroupsRequest request,
                        StreamObserver<GetGroupsResponse> responseObserver) {
    GetGroupsResponse response = GetGroupsResponse.newBuilder()
        .addAllGroupIds(groups).build();

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
