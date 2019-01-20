package ru.memoscope;

import org.junit.Assert;
import org.junit.Test;
import ru.memoscope.dataBase.DataBase;
import ru.memoscope.dataBase.DataBaseController;
import ru.memoscope.dataBase.PostLink;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DBTest {

  @Test
  public void kek() throws IOException {
    DataBaseController c = new DataBaseController();
    c.addPost("kek lol lul lel", -2, 1, 3);
    c.addPost("kek lul cheburek lol", -2, 2, 3);
    c.addPost("lol lul cheburek", -2, 3, 3);
    c.addPost("lol", -1, 4, 3);
    c.addPost("lul lul lul", -2, 5, 3);
    c.addPost("\") DROP TABLE db.analizedPostsTableName;", -2, 6, 3);
    Set<PostLink> posts = new HashSet<>(c.findPosts("lol cheburek",
            Arrays.asList(-2L, -1L), 0, 6));
    Assert.assertTrue(posts.contains(new PostLink(-2, 2)));
    Assert.assertTrue(posts.contains(new PostLink(-2, 3)));
    Assert.assertTrue(posts.contains(new PostLink(-2, 1)));
    Assert.assertTrue(posts.contains(new PostLink(-1, 4)));
    Assert.assertEquals(posts.size(), 4);
  }
}
