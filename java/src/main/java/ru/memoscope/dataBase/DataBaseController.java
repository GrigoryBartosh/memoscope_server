package ru.memoscope.dataBase;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

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
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + tableName + " (groupId, postId, timestamp, text) VALUES (?, ?, ?, ?)");
            statement.setLong(1, groupId);
            statement.setLong(2, postId);
            statement.setLong(3, timestamp);
            statement.setString(4, text);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PostLink> findPosts(String text, List<Long> groupIds, long timeFrom, long timeTo) {
        List<PostLink> links = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement;
            if (groupIds.isEmpty()) {
                statement = connection.prepareStatement(
                        "SELECT postId, groupId FROM "
                                + tableName
                                + " WHERE timestamp BETWEEN ? AND ?"
                                + " AND MATCH text AGAINST (?)");
            } else {
                StringBuilder builder = new StringBuilder("SELECT postId, groupId FROM "
                        + tableName
                        + " WHERE timestamp BETWEEN ? AND ?"
                        + " AND MATCH text AGAINST (?)"
                        + " AND groupId in (");
                for (int i = 0; i < groupIds.size(); ++i) {
                    if (i != 0) {
                        builder.append(", ");
                    }
                    builder.append('?');
                }
                builder.append(")");
                statement = connection.prepareStatement(builder.toString());
                for (int i = 0; i < groupIds.size(); ++i) {
                    // yikes...
                    statement.setLong(4 + i, groupIds.get(i));
                }
            }
            statement.setLong(1, timeFrom);
            statement.setLong(2, timeTo);
            statement.setString(3, text);
            ResultSet res = statement.executeQuery();
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
