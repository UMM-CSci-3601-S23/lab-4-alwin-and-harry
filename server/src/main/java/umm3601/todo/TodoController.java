package umm3601.todo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mongojack.JacksonMongoCollection;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;

/**
 * Controller that manages requests for info about users.
 */
public class TodoController {

  static final String STATUS_KEY = "status";
  static final String ID_KEY = "_id";

  private static final String CATEGORY_REGEX = "^(homework|groceries|software design|video games)$";

  private final JacksonMongoCollection<Todo> todoCollection;

  /**
   * Construct a controller for Todos.
   *
   * @param database the database containing todo data
   */
  public TodoController(MongoDatabase database) {
    todoCollection = JacksonMongoCollection.builder().build(
        database,
        "todos",
        Todo.class,
        UuidRepresentation.STANDARD);
  }

  /**
   * Set the JSON body of the response to be the single user
   * specified by the `id` parameter in the request
   *
   * @param ctx a Javalin HTTP context
   */
  public void getTodo(Context ctx) {
    String id = ctx.pathParam("id");
    Todo todo;

    try {
      todo = todoCollection.find(eq("_id", new ObjectId(id))).first();
    } catch (IllegalArgumentException e) {
      throw new BadRequestResponse("The requested todo id wasn't a legal Mongo Object ID.");
    }
    if (todo == null) {
      throw new NotFoundResponse("The requested todo was not found");
    } else {
      ctx.json(todo);
      ctx.status(HttpStatus.OK);
    }
  }

  public int getLimitedTodos(Context ctx) {
    int limit = -1;
    if (ctx.queryParamMap().containsKey("limit")) {
      limit = ctx.queryParamAsClass("limit", Integer.class).get();
    }
    return limit;
}
  /**
   * Set the JSON body of the response to be a list of all the users returned from the database
   * that match any requested filters and ordering
   *
   * @param ctx a Javalin HTTP context
   */
  public void getTodos(Context ctx) {
    Bson combinedFilter = constructFilter(ctx);
    Bson sortingOrder = constructSortingOrder(ctx);
    int targetLimit = getLimitedTodos(ctx);
    ArrayList<Todo> matchingTodos;
    // All three of the find, sort, and into steps happen "in parallel" inside the
    // database system. So MongoDB is going to find the users with the specified
    // properties, return those sorted in the specified manner, and put the
    // results into an initially empty ArrayList.
    if (targetLimit != -1) {
      matchingTodos = todoCollection
      .find(combinedFilter)
      .sort(sortingOrder)
      .limit(targetLimit)
      .into(new ArrayList<>());
    } else {
      matchingTodos = todoCollection
      .find(combinedFilter)
      .sort(sortingOrder)
      .into(new ArrayList<>());
    }


    // Set the JSON body of the response to be the list of users returned by the database.
    // According to the Javalin documentation (https://javalin.io/documentation#context),
    // this calls result(jsonString), and also sets content type to json
    ctx.json(matchingTodos);

    // Explicitly set the context status to OK
    ctx.status(HttpStatus.OK);
  }

  private Bson constructFilter(Context ctx) {
    List<Bson> filters = new ArrayList<>();
    if (ctx.queryParamMap().containsKey(STATUS_KEY)) {
      String completed = ctx.queryParamAsClass(STATUS_KEY, String.class).get();
      boolean targetStatus;
      if (completed.equals("incomplete")) {
        targetStatus = false;
      } else {
        targetStatus = true;
      }
      filters.add(eq(STATUS_KEY, targetStatus));
    }

    Bson combinedFilter = filters.isEmpty() ? new Document() : and(filters);

    return combinedFilter;
  }



  //private Bson constructFilter()

  private Bson constructSortingOrder(Context ctx) {
    String sortBy = Objects.requireNonNullElse(ctx.queryParam("sortby"), "status");
    String sortOrder = Objects.requireNonNullElse(ctx.queryParam("sortorder"), "asc");
    Bson sortingOrder = sortOrder.equals("desc") ?  Sorts.descending(sortBy) : Sorts.ascending(sortBy);
    return sortingOrder;
  }

  //method for adding a new todo
  public void addNewTodo(Context ctx) {
    //validating all the parts of the new todo
    Todo newTodo = ctx.bodyValidator(Todo.class)
      .check(todo -> todo.owner != null && todo.owner.length() > 0, "Todo must have a non-empty owner")
      .check(todo -> todo.body != null && todo.body.length() > 0, "Todo must have a non-empty body")
      .check(todo -> todo.category.matches(CATEGORY_REGEX), "Todo must have a correct category")
      .get();

    todoCollection.insertOne(newTodo);

    ctx.json(Map.of("id", newTodo._id));
    ctx.status(HttpStatus.CREATED);
  }

  @SuppressWarnings("lgtm[java/weak-cryptographic-algorithm]")
  public String md5(String str) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] hashInBytes = md.digest(str.toLowerCase().getBytes(StandardCharsets.UTF_8));

    StringBuilder result = new StringBuilder();
    for (byte b : hashInBytes) {
      result.append(String.format("%02x", b));
    }
    return result.toString();
  }
}

