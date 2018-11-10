package ru.memoscope.buffer;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.memoscope.BufferProto.*;

import java.sql.*;
import java.util.List;
import java.util.Properties;

public class DataBaseAgent {

  private String url;
  private String user;
  private String password;

  public DataBaseAgent(Properties property) {
      url = property.getProperty("db.url");
      user = property.getProperty("db.user");
      password = property.getProperty("db.password");

  }

  public Post popLatestMeme() {
    try (Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement()) {
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
      String deleteQuery = String.format("DELETE FROM Memes WHERE groupId=%d AND postId=%d",
          post.getGroupId(), post.getPostId());
      statement.executeUpdate(deleteQuery);
      return post.build();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void storePosts(List<Post> posts) {
    try (Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement()) {
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
    System.out.println("All " + posts.size() + " stored");
  }
}