package ru.memoscope.buffer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.queries.newsfeed.NewsfeedGetFilter;
import ru.memoscope.BufferProto.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static com.google.common.primitives.Longs.max;

public class MemesLoader {
  private VkApiClient vk;
  private UserActor user;
  private String nextFrom = null;
  private long latestTime;
  private int memesUpdatedCount;
  private int memesOldCount;

  private DataBaseAgent db;

  public MemesLoader(int id, String token, Properties property) throws IOException {
    TransportClient transportClient = HttpTransportClient.getInstance();
    vk = new VkApiClient(transportClient);
    user = new UserActor(id, token);
    db = new DataBaseAgent();
    memesUpdatedCount = Integer.parseInt(property.getProperty("loader.memesUpdatedCount"));
    memesOldCount = Integer.parseInt(property.getProperty("loader.memesOldCount"));
  }


  public void startDownload() {
    System.out.println("Starting to load memes");
    try {
      //while (true) {
      for (int i = 0; i < 5; i++) {
        try {
          ArrayList<Post> posts = new ArrayList<>();
          if (nextFrom == null) {
            posts.addAll(jsonToPosts(vk.newsfeed()
                .get(user)
                .filters(NewsfeedGetFilter.POST)
                .count(memesUpdatedCount)
                .executeAsString(), false));
            posts.addAll(jsonToPosts(vk.newsfeed()
                .get(user)
                .filters(NewsfeedGetFilter.POST)
                .count(memesOldCount)
                .executeAsString(), true));
          } else {
            posts.addAll(jsonToPosts(vk.newsfeed()
                .get(user)
                .filters(NewsfeedGetFilter.POST)
                .count(memesUpdatedCount)
                .startTime((int) latestTime)
                .executeAsString(), false));
            posts.addAll(jsonToPosts(vk.newsfeed()
                .get(user)
                .filters(NewsfeedGetFilter.POST)
                .count(memesOldCount)
                .startFrom(nextFrom)
                .executeAsString(), true));
          }

          db.storePosts(posts);
        } catch (ClientException e) {
          e.printStackTrace();
        }
        System.out.println("\nIteration " + i + " finished\n");
        Thread.sleep(10000);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private String savePhoto(JsonObject photo) {
    String url = getUrl(photo);
    if (url == null) {
      return null;
    }
    String id = String.valueOf(photo.get("id").getAsLong());
    String ownerId = String.valueOf(photo.get("owner_id").getAsLong());
    String photoId = id + "_" + ownerId;
    try (InputStream in = new URL(url).openStream()) {
      String path = "../photos/" + photoId + ".jpg";
      File directory = new File("../photos");
      if (!directory.exists()) {
        directory.mkdir();
      }
      File file = new File(path);
      if (file.exists()) {
        System.out.println("File already created: " + photoId);
        return photoId;
      }
      file.createNewFile();
      InputStream buffIn = new BufferedInputStream(in);
      OutputStream out = new BufferedOutputStream(new FileOutputStream(path));

      for (int i; (i = in.read()) != -1; ) {
        out.write(i);
      }
      buffIn.close();
      out.close();
      return photoId;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String getUrl(JsonObject photo) {
    JsonElement url2560 = photo.get("photo_2560");
    JsonElement url1280 = photo.get("photo_1280");
    JsonElement url807 = photo.get("photo_807");
    JsonElement url604 = photo.get("photo_604");
    JsonElement url130 = photo.get("photo_130");
    JsonElement url75 = photo.get("photo_75");
    if (url2560 != null) {
      return url2560.getAsString();
    }
    if (url1280 != null) {
      return url1280.getAsString();
    }
    if (url807 != null) {
      return url807.getAsString();
    }
    if (url604 != null) {
      return url604.getAsString();
    }
    if (url130 != null) {
      return url130.getAsString();
    }
    if (url75 != null) {
      return url75.getAsString();
    }
    return null;
  }

  private List<Post> jsonToPosts(String jsonString, boolean updateNextFrom) {
    JsonObject obj = ((JsonObject) new JsonParser().parse(jsonString)).getAsJsonObject("response");
    JsonArray items = obj.getAsJsonArray("items");
    if (updateNextFrom) nextFrom = obj.get("next_from").getAsString();
    ArrayList<Post> posts = new ArrayList<>();
    for (JsonElement item : items) {
      JsonObject itemObj = item.getAsJsonObject();
      String text = itemObj.get("text")
          .getAsString()
          .replaceAll("[^(\\d\\wА-Яа-я)]", " ");
      Post.Builder post = Post.newBuilder()
          .setPostId(itemObj.get("post_id").getAsLong())
          .setGroupId(itemObj.get("source_id").getAsLong())
          .setTimestamp(itemObj.get("date").getAsLong())
          .setText(text);
      latestTime = max(post.getTimestamp(), latestTime);
      JsonArray attachments = itemObj.getAsJsonArray("attachments");
      if (attachments == null) {
        continue;
      }
      for (JsonElement attachment : attachments) {
        JsonObject attach = attachment.getAsJsonObject();
        String type = attach.get("type").getAsString();
        if (!type.equals("photo")) {
          continue;
        }
        JsonObject photo = attach.getAsJsonObject("photo");
        System.out.println("Photo # " + String.valueOf(photo.get("id").getAsLong()) + " in " + new Date(itemObj.get("date").getAsLong() * 1000));
        String photoPath = savePhoto(photo);
        if (photoPath == null) {
          continue;
        }
        post.addPicturePaths(photoPath);
      }
      Post buildedPost = post.build();
      if (buildedPost.getPicturePathsList().size() == 0) {
        continue;
      }
      posts.add(buildedPost);
    }
    return posts;
  }

  public Post getLatestPost() {
    return db.popLatestMeme();
  }
}
