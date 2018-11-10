package ru.memoscope.dataBase;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ru.memoscope.AnalyzerTextGrpc;
import ru.memoscope.AnalyzerTextProto.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static ru.memoscope.AnalyzerTextGrpc.newBlockingStub;
import static ru.memoscope.AnalyzerTextGrpc.AnalyzerTextBlockingStub;

public class TextAnalyzer extends AnalyzerTextGrpc.AnalyzerTextImplBase {
    private String host;
    private int port;
    private AnalyzerTextBlockingStub blockingStub;

    private void readConfig() {
        try (FileInputStream inputStream = new FileInputStream("src/main/resources/database.properties")) {
            Properties property = new Properties();
            property.load(inputStream);

            host = property.getProperty("analyzerText.host");
            port = Integer.parseInt(property.getProperty("analyzerText.port"));
        } catch (IOException e) {
            System.err.println("failed to upload config file database.properties");
            System.exit(1);
        }
    }

    public TextAnalyzer() {
        readConfig();

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true)
                .build();
        blockingStub = newBlockingStub(channel);
    }

    public String analyze(String text) {
        AnalyzeTextRequest request = AnalyzeTextRequest.newBuilder()
                .setText(text)
                .build();

        AnalyzeTextResponse response = blockingStub.analyzeText(request);

        return response.getText();
    }
}
