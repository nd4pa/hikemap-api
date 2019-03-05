package router.demo;

import com.graphhopper.*;
import com.graphhopper.routing.util.*;
import com.graphhopper.util.*;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;

import spark.Route;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GraphHopperController {

    public static Route makeLoop = (Request req, Response res) -> {

        // ======================= Route Planéning =====================================
         // Loading Graphhopper
        GraphHopper hopper = new GraphHopperOSM().forServer();
        hopper.setDataReaderFile("src/main/resources/bretagne-latest.osm.pbf").setGraphHopperLocation("temp")
                .setEncodingManager(new EncodingManager("foot")).setCHEnabled(false);

        // Load OSM File
        System.out.println("Loading OSM file ...");
        hopper.importOrLoad();
        
        Double lat = Double.parseDouble(req.params("lat"));
        Double lon = Double.parseDouble(req.params("lon"));
        
        GHRequest ghreq = new GHRequest().setVehicle("foot").addPoint(new GHPoint(lat, lon))
                .setAlgorithm(Parameters.Algorithms.ROUND_TRIP);
        ghreq.getHints().put(Parameters.Algorithms.RoundTrip.DISTANCE, req.params("size"));
        GHResponse rsp = hopper.route(ghreq);

        // first check for errors
        if (rsp.hasErrors()) {
            // handle them!
            System.out.println(rsp.getErrors());
        }

        //List<PathWrapper> paths = rsp.getAll();

        PathWrapper path = rsp.getBest();
        PointList pointList = path.getPoints();
        double distance = path.getDistance();
        long timeInMs = path.getTime();

        InstructionList il = path.getInstructions();

        // or get the json
        

        //System.out.println(il.toString().replace('(', '[').replace(')', ']'));
        //System.out.println("==============================================================================");
        //Gson gson = new GsonBuilder().create();

        JsonParser parser = new JsonParser();
        JsonObject body = parser.parse("{ \"points\": ["+path.getPoints().toString().replace('(', '[').replace(')', ']')+"], \"time\": "+timeInMs+", \"distance\": "+distance+"}").getAsJsonObject();
        //body.add("instructions",iList);
        //System.out.println(body);
        //System.out.println("==============================================================================");
        //System.out.println("==============================================================================");
        //System.out.println(itineraire);
        res.header("Access-Control-Allow-Origin", "*");
        res.type("application/json");
    
        return body;
    };
}
