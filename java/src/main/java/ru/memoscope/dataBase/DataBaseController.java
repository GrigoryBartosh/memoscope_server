package ru.memoscope.dataBase;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.memoscope.BufferProto;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DataBaseController {


    private String url;
    private String user;
    private String password;
    private String tableName;

    public DataBaseController() throws IOException {
        Properties property = new Properties();
        property.load(new FileInputStream("src/main/resources/database.properties"));
        url = property.getProperty("db.url");
        user = property.getProperty("db.user");
        password = property.getProperty("db.password");
        tableName = property.getProperty("db.analizedPostsTableName");
    }

    public void addPost(String text, long groupId, long postId, long timestamp) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {
            String query = String.format("INSERT INTO " + tableName +
                " (groupId, postId, timestamp, text)" +
                " VALUES (%d, %d, %d, \"%s\")",
                groupId, postId, timestamp, text);
            statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PostLink> findPosts(String text, List<Long> groupIds, long timeFrom, long timeTo) {
        List<PostLink> list = new ArrayList<>();
        return list;
    }
}
