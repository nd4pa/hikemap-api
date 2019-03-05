/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package router.demo;

// GSON imports
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// Spark imports
import static spark.Spark.*;

public class App {

    public static void main(String[] args) {
        get("/",(req,res) -> "Welcome to HikeMap API");
        get("/loop/:lat/:lon/:size", GraphHopperController.makeLoop);
    }
}
