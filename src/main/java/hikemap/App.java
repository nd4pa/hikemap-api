/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package hikemap;


// Spark imports
import static spark.Spark.*;

// Graphhopper imports
import com.graphhopper.*;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;

// JSON Imports
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;



public class App {

    public static void main(String[] args) {
        // Loading Graphhopper
        GraphHopper hopper = new GraphHopperOSM().forServer();
        hopper.setDataReaderFile("osm/france.osm.pbf").setGraphHopperLocation("temp")
                .setEncodingManager(new EncodingManager("hike,car,bike")).setCHEnabled(false);

        // Load OSM File
        hopper.importOrLoad();

        get("/",(req,res) -> "Welcome to HikeMap API");
        get("/loop/:lat/:lon/:size/:vehicle", (req,res) -> {
            Double lat = Double.parseDouble(req.params("lat"));
            Double lon = Double.parseDouble(req.params("lon"));
            String size = req.params("size");
            String vehicle = req.params("vehicle");
            JSONObject body = GraphHopperService.makeLoop(hopper,lat,lon,size,vehicle);
            res.header("Access-Control-Allow-Origin", "*");
            res.type("application/json");
            return body;
        });
        get("/patrimonial/:lat/:lon/:size/:stops/:vehicle", (req,res) -> {
            Double lat = Double.parseDouble(req.params("lat"));
            Double lon = Double.parseDouble(req.params("lon"));
            Integer stops = Integer.parseInt(req.params("stops"));
            String size = req.params("size");
            String vehicle = req.params("vehicle");
            JSONObject body = GraphHopperService.makePatrimonial(hopper,lat,lon,size,stops,vehicle);
            res.header("Access-Control-Allow-Origin", "*");
            res.type("application/json");
            return body;
        });
    }
}
