package umm3601.todo;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;

@SuppressWarnings({"MagicNumber"})
public class TodoControllerSpec {

  private TodoController todoController;
  private ObjectId alwinsId;

  private static MongoClient mongoClient;
  private static MongoDatabase db;

  @Mock
  private Context ctx;

  @Captor
  private ArgumentCaptor<ArrayList<Todo>> todoArrayListCaptor;

  @Captor
  private ArgumentCaptor<Todo> todoCaptor;

  @Captor
  private ArgumentCaptor<Map<String, String>> mapCaptor;

  @BeforeAll
  public static void setupAll() {
    String mongoAddr = System.getenv().getOrDefault("MONGO_ADDR", "localhost");

    mongoClient = MongoClients.create(
        MongoClientSettings.builder()
            .applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress(mongoAddr))))
            .build()
    );
    db = mongoClient.getDatabase("test");
  }

  @AfterAll
  public static void teardown() {
    db.drop();
    mongoClient.close();
  }

  @BeforeEach
  public void setupEach() throws IOException {
    // Reset our mock context and argument captor (declared with Mockito annotations @Mock and @Captor)
    MockitoAnnotations.openMocks(this);

    // Setup database
    MongoCollection<Document> todoDocuments = db.getCollection("todos");
    todoDocuments.drop();
    List<Document> testTodos = new ArrayList<>();
    testTodos.add(
        new Document()
        .append("owner", "Harry")
        .append("status", false)
        .append("body", "Metal under tension beggin you to touch and go")
        .append("category", "video games"));
    testTodos.add(
        new Document()
        .append("owner", "Nic")
        .append("status", false)
        .append("body", "Revvin up your engine listen to her howlin roar")
        .append("category", "software design"));
    testTodos.add(
        new Document()
        .append("owner", "KK")
        .append("status", true)
        .append("body", "Metal under tension beggin you to touch and go")
        .append("category", "homework"));
    alwinsId = new ObjectId();
    Document alwin = new Document()
        .append("_id", alwinsId)
        .append("owner", "Alwin")
        .append("status", true)
        .append("body", "Headin into twilight spreadin out her wings tonight")
        .append("category", "software design");

    todoDocuments.insertMany(testTodos);
    todoDocuments.insertOne(alwin);

    todoController = new TodoController(db);
  }

  @Test
  public void canGetAllTodos() throws IOException {
    when(ctx.queryParamMap()).thenReturn(Collections.emptyMap());
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayListCaptor.capture());
    verify(ctx).status(HttpStatus.OK);
    assertEquals(db.getCollection("todos").countDocuments(), todoArrayListCaptor.getValue().size());
  }

  @Test
  public void canGetTodoWithStatusTrue() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(TodoController.STATUS_KEY, Arrays.asList(new String[] {"complete"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    when(ctx.queryParam(TodoController.STATUS_KEY)).thenReturn("complete");
  }

  @Test
  public void canGetTodoWithStatusFalse() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put(TodoController.STATUS_KEY, Arrays.asList(new String[] {"incomplete"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    when(ctx.queryParam(TodoController.STATUS_KEY)).thenReturn("incomplete");
  }

  @Test
  public void getTodoWithExistentId() throws IOException {
    String id = alwinsId.toHexString();
    when(ctx.pathParam("id")).thenReturn(id);
    todoController.getTodo(ctx);
    verify(ctx).json(todoCaptor.capture());
    verify(ctx).status(HttpStatus.OK);
    assertEquals("Alwin", todoCaptor.getValue().owner);
    assertEquals(alwinsId.toHexString(), todoCaptor.getValue()._id);
  }

  @Test
  public void getTodoWithBadId() throws IOException {
    when(ctx.pathParam("id")).thenReturn("bad");
    Throwable exception = assertThrows(BadRequestResponse.class, () -> {
      todoController.getTodo(ctx);
    });
    assertEquals("The requested todo id wasn't a legal Mongo Object ID.", exception.getMessage());
  }

  @Test
  public void getUserWithNonexistentId() throws IOException {
    String id = "588935f5c668650dc77df581";
    when(ctx.pathParam("id")).thenReturn(id);

    Throwable exception = assertThrows(NotFoundResponse.class, () -> {
      todoController.getTodo(ctx);
    });

    assertEquals("The requested todo was not found", exception.getMessage());
  }
}







