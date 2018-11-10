package ru.memoscope.dataBase;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import ru.memoscope.DataBaseGrpc.*;
import ru.memoscope.DataBaseProto.*;
import ru.memoscope.MessagesProto.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class DataBase extends DataBaseImplBase {
    private Server server;
    private TextAnalyzer textAnalyzer;
    private DataBaseController dataBaseController;

    private static int readPort() {
        int port = 0;

        try (FileInputStream inputStream = new FileInputStream("src/main/resources/database.properties")) {
            Properties property = new Properties();
            property.load(inputStream);

            port = Integer.parseInt(property.getProperty("server.port"));
        } catch (IOException e) {
            System.err.println("failed to upload config file database.properties");
            System.exit(1);
        }

        return port;
    }

    public DataBase() throws IOException {
        int port = readPort();
        server = ServerBuilder.forPort(port).addService(this).build();

        textAnalyzer = new TextAnalyzer();
        dataBaseController = new DataBaseController();

        System.out.println("0.constructor finished");
    }

    public void start() throws IOException {
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
        }));

        System.out.println("0.started");
    }

    public void blockUntilShutDown() throws InterruptedException {
        server.awaitTermination();
    }

    @Override
    public void storePost(StorePostRequest request, StreamObserver<StorePostResponse> responseObserver) {
        System.out.println("1.1.new store request");

        String text = request.getText();
        long groupId = request.getGroupId();
        long postId = request.getPostId();
        long timestamp = request.getTimestamp();

        text = textAnalyzer.analyze(text);

        dataBaseController.addPost(text, groupId, postId, timestamp);
        responseObserver.onNext(StorePostResponse.getDefaultInstance());
        responseObserver.onCompleted();

        System.out.println("1.2.store request processing completed");
    }

    @Override
    public void findPosts(FindPostsRequest request, StreamObserver<FindPostsResponse> responseObserver) {
        System.out.println("2.1.new find request");

        String text = request.getText();
        List<Long> groupIds = request.getGroupIdsList();
        long timeFrom = request.getTimeFrom();
        long timeTo = request.getTimeTo();

        text = textAnalyzer.analyze(text);

        List<PostLink> posts = dataBaseController.findPosts(text, groupIds, timeFrom, timeTo);

        FindPostsResponse.Builder builder = FindPostsResponse.newBuilder();
        for (PostLink post : posts) {
            builder.addPosts(PostInfo.newBuilder()
                    .setGroupId(post.getGroupId())
                    .setPostId(post.getPostId())
                    .build());
        }
        FindPostsResponse response = builder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

        System.out.println("2.2.find request processing completed");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        DataBase dataBase = new DataBase();
        dataBase.start();
        dataBase.blockUntilShutDown();
    }
}
