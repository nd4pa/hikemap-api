package router.demo;

import com.graphhopper.*;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.GraphHopper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GraphHopperService {

    public static JsonObject makeLoop(GraphHopper hopper, Double lat,Double lon, String size) {
        
        GHRequest ghreq = new GHRequest().setVehicle("foot").addPoint(new GHPoint(lat, lon))
                .setAlgorithm(Parameters.Algorithms.ROUND_TRIP);
        ghreq.getHints().put(Parameters.Algorithms.RoundTrip.DISTANCE,size);
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

    
        return body;
    };
}
