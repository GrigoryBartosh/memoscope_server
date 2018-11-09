package ru.memoscope.buffer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.memoscope.BufferGrpc.*;
import ru.memoscope.BufferProto.*;

import java.util.ArrayList;
import java.util.List;

public class MemesLoader {
  int id;
  String token;
  VkApiClient vk;
  UserActor user;

  public MemesLoader(int id, String token) {
    this.id = id;
    this.token = token;
    TransportClient transportClient = HttpTransportClient.getInstance();
    vk = new VkApiClient(transportClient);
    user = new UserActor(id, token);
  }


  public void startDownload() {
    while (true) {

    }
  }

  private List<Post> jsonToPosts(String jsonString) throws ParseException {
    JsonObject obj = (JsonObject) new JSONParser().parse(jsonString);
    JsonArray items = obj.getAsJsonArray("items");
    ArrayList<Post> posts = new ArrayList<>();
    for (JsonElement item : items) {
      JsonObject itemObj = item.getAsJsonObject();
      Post.Builder post = Post.newBuilder()
          .setPostId(itemObj.get("post_id").getAsLong())
          .setGroupId(itemObj.get("source_id").getAsLong())
          .setTimestamp(itemObj.get("date").getAsLong())
          .setText(itemObj.get("text").getAsString());



      posts.add(post.build());
    }
    return posts;
  }

  public Post getLatestPost() {
    return vk.newsfeed().get(user).count(1).executeAsString();
  }
}
