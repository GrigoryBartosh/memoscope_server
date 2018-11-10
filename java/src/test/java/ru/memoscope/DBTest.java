package ru.memoscope;

import org.junit.Test;
import ru.memoscope.dataBase.DataBase;
import ru.memoscope.dataBase.DataBaseController;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class DBTest {
  @Test
  public void kek() throws IOException {
    DataBaseController c = new DataBaseController();
    c.addPost("kek lol lul lel", -2, 1, 3);
    c.addPost("kek lul cheburek lol", -2, 2, 3);
    c.addPost("lol lul cheburek", -2, 3, 3);
    c.addPost("lol", -1, 4, 3);
    c.addPost("lul lul lul", -2, 5, 3);
    System.out.println(
        Arrays.toString(c.findPosts("lol cheburek",
            Arrays.asList(-2L, -1L), 0, 6).toArray()));
    System.out.println(
        Arrays.toString(c.findPosts("lol cheburek",
            Collections.singletonList(-2L), 0, 6).toArray()));
  }
}
