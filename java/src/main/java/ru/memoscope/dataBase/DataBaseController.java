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
            System.out.println(query);
            statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PostLink> findPosts(String text, List<Long> groupIds, long timeFrom, long timeTo) {
        List<PostLink> links = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {
            StringBuilder query = new StringBuilder(String.format("SELECT postId, groupId FROM " + tableName +
                    " WHERE timestamp >= %d AND timestamp <= %d " +
                    "AND MATCH text AGAINST (\'%s\')",
                timeFrom, timeTo, text));
            if (groupIds.size() != 0) {
                query.append(" AND (");
                boolean first = true;
                for (Long groupId : groupIds) {
                    if (first) {
                        first = false;
                        query.append("groupId = ").append(groupId);
                    } else {
                        query.append(" OR groupId = ").append(groupId);
                    }
                }
                query.append(")");
            }
            System.out.println(query.toString());
            ResultSet res = statement.executeQuery(query.toString());
            while (res.next()) {
                PostLink link = new PostLink(res.getLong("groupId"), res.getLong("postId"));
                links.add(link);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return links;
    }
}
