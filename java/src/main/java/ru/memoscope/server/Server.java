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
import ru.memoscope.DataBaseGrpc.DataBaseBlockingStub;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static ru.memoscope.DataBaseGrpc.newBlockingStub;

public class Server extends ServerImplBase {

  private io.grpc.Server server;
  List<Long> groups;
  DataBaseBlockingStub blockingStub;
  Properties property;


  public Server() throws IOException {
    property = new Properties();
    property.load(new FileInputStream("src/main/resources/server.properties"));
    int port = Integer.parseInt(property.getProperty("server.port"));
    int dbPort = Integer.parseInt(property.getProperty("db.port"));
    String dbHost = property.getProperty("db.host");
    server = ServerBuilder.forPort(port).addService(this).build();
    loadGroups();
    System.out.println(groups);

    ManagedChannel channel = ManagedChannelBuilder.forAddress(dbHost, dbPort)
        .usePlaintext(true)
        .build();
    blockingStub = newBlockingStub(channel);
  }

  private void loadGroups() {
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
    System.out.println("Find posts request: " + request);
    String newText = request.getText().replaceAll("[^(\\d\\wА-Яа-я)]", " ");
    FindPostsResponse response = blockingStub.findPosts(request.toBuilder().setText(newText).build());
    //FindPostsResponse response = FindPostsResponse.newBuilder()
    //    .addPosts(PostInfo.newBuilder().setGroupId(-92337511).setPostId(1119801).build()).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
    System.out.println("Find posts request handled: " + response);
  }

  @Override
  public void getGroups(GetGroupsRequest request,
                        StreamObserver<GetGroupsResponse> responseObserver) {
    System.out.println("Get groups request: " + request);
    loadGroups();
    GetGroupsResponse response = GetGroupsResponse.newBuilder()
        .addAllGroupIds(groups).build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
    System.out.println("Get groups request handled: " + response);
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
