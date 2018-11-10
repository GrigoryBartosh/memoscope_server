package ru.memoscope.buffer;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.memoscope.BufferProto.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Properties;

public class DataBaseAgent {

  private String url;
  private String user;
  private String password;
  private String tableName;

  public DataBaseAgent() throws IOException {
    Properties property = new Properties();
    property.load(new FileInputStream("src/main/resources/database.properties"));
    url = property.getProperty("db.url");
    user = property.getProperty("db.user");
    password = property.getProperty("db.password");
    tableName = property.getProperty("db.rawPostsTableName");
  }

  public Post popLatestMeme() {
    try (Connection connection = DriverManager.getConnection(url, user, password);
         Statement statement = connection.createStatement()) {
      String query = "SELECT * FROM " + tableName +
          " WHERE timestamp = (SELECT MAX(timestamp) FROM " + tableName + " WHERE sended = 0) AND sended = 0";
      ResultSet res = statement.executeQuery(query);
      if (!res.next()) {
        System.out.println("No such post");
        return null;
      }
      Post.Builder post = Post.newBuilder()
          .setGroupId(res.getLong(1))
          .setPostId(res.getLong(2))
          .setTimestamp(res.getLong(3))
          .setText(res.getString(4));
      for (JsonElement el : (JsonArray) new JsonParser().parse(res.getString(5))) {
        post.addPicturePaths(el.getAsString());
      }
      String updateQuery = String.format("UPDATE " + tableName + " SET sended = 1 WHERE groupId=%d AND postId=%d",
          post.getGroupId(), post.getPostId());
      System.out.println(updateQuery);
      statement.executeUpdate(updateQuery);
      return post.build();
    } catch (SQLException e) {
      // no such raw or epic fail
      e.printStackTrace();
    }
    return null;
  }


  public TimestampRange getTimestampRange() {
    try (Connection connection = DriverManager.getConnection(url, user, password);
         Statement statement = connection.createStatement()) {
      String query = "SELECT * FROM Timestamps";
      ResultSet res = statement.executeQuery(query);
      if (!res.next()) {
        return null;
      }
      return new TimestampRange(res.getLong(1), res.getLong(2));
    } catch (SQLException e) {
      // no such raw or epic fail
      e.printStackTrace();
    }
    return null;
  }

  public void updateTimestampRange(long min, long max) {
    if (getTimestampRange() == null) {
      insertTimestampRange(min, max);
    }
    try (Connection connection = DriverManager.getConnection(url, user, password);
         Statement statement = connection.createStatement()) {
      String query = String.format("UPDATE Timestamps SET minTimestamp = %d, maxTimestamp = %d",
          min, max);
      statement.executeUpdate(query);
    } catch (SQLException e) {
      // no such raw or epic fail
      e.printStackTrace();
    }
  }

  public void insertTimestampRange(long min, long max) {
    try (Connection connection = DriverManager.getConnection(url, user, password);
         Statement statement = connection.createStatement()) {
      String query = String.format("INSERT INTO Timestamps (minTimestamp, maxTimestamp) VALUES (%d, %d)",
          min, max);
      statement.executeUpdate(query);
    } catch (SQLException e) {
      // no such raw or epic fail
      e.printStackTrace();
    }
  }

  public void storePosts(List<Post> posts) {
    try (Connection connection = DriverManager.getConnection(url, user, password);
         Statement statement = connection.createStatement()) {
      for (Post post : posts) {
        System.out.println(post.getPicturePathsList());
        JsonArray photoPaths = new JsonArray(post.getPicturePathsList().size());
        for (String photoPath : post.getPicturePathsList()) {
          photoPaths.add(photoPath);
        }
        String text = post.getText();
        String query = String.format("INSERT INTO " + tableName +
                " (groupId, postId, timestamp, text, photoPaths, sended)" +
                " VALUES (%d, %d, %d, \"%s\", \"%s\", 0);",
            post.getGroupId(), post.getPostId(), post.getTimestamp(),
            text, photoPaths.toString().replace("\"", "\\\""));

        try {
          statement.executeUpdate(query);
        } catch (SQLIntegrityConstraintViolationException e) {
          System.out.println("Already stored in database");
          // repeated primary key
        } catch (SQLException e) {
          e.printStackTrace();
          System.out.println(query);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    System.out.println("All " + posts.size() + " stored");
  }
}
