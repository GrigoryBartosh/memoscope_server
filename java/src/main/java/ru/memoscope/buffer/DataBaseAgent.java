package ru.memoscope.buffer;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.memoscope.BufferProto.*;

import java.sql.*;
import java.util.List;
import java.util.Properties;

public class DataBaseAgent {


  private Connection connection;

  public DataBaseAgent(Properties property) {
    try {
      String url = property.getProperty("db.url");
      String user = property.getProperty("db.user");
      String password = property.getProperty("db.password");
      connection = DriverManager.getConnection(url, user, password);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Post popLatestMeme() {
    try (Statement statement = connection.createStatement()) {
      String query = "SELECT * FROM Memes " +
          "WHERE timestamp = (SELECT MIN(timestamp) FROM Memes)";
      ResultSet res = statement.executeQuery(query);
      if (!res.next()) {
        return null;
      }
      Post.Builder post = Post.newBuilder()
          .setGroupId(res.getLong(1))
          .setPostId(res.getLong(2))
          .setTimestamp(res.getLong(3))
          .setText(res.getString(4));
      for (JsonElement el: (JsonArray)new JsonParser().parse(res.getString(5))) {
        post.addPicturePaths(el.getAsString());
      }
      return post.build();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void storePosts(List<Post> posts) {
    try (Statement statement = connection.createStatement()) {
      for (Post post : posts) {
        JsonArray photoPaths = new JsonArray(post.getPicturePathsList().size());
        for (String photoPath: post.getPicturePathsList()) {
          photoPaths.add(photoPath);
        }
        String query = String.format("INSERT INTO Memes " +
                "(groupId, postId, timestamp, text, photoPaths)" +
                " VALUES (%d, %d, %d, \"%s\", \"%s\");",
            post.getGroupId(), post.getPostId(), post.getTimestamp(),
            post.getText(), photoPaths.toString().replace("\"", "\\\""));
        statement.executeUpdate(query);

      }
    } catch (SQLIntegrityConstraintViolationException e) {
        // repeated primary key
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
