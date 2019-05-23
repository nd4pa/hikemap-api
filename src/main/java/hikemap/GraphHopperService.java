package hikemap;

import com.graphhopper.*;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.GraphHopper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


public class GraphHopperService {

    public static JSONObject makeLoop(GraphHopper hopper, Double lat,Double lon, String size, String vehicle) {
        
        GHRequest ghreq = new GHRequest().setVehicle(vehicle).addPoint(new GHPoint(lat, lon))
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

        JSONObject body = new JSONObject("{ \"points\": ["+path.getPoints().toString().replace('(', '[').replace(')', ']')+"], \"time\": "+timeInMs+", \"distance\": "+distance+"}");
    
        return body;
    };
    
    public static JSONObject makePatrimonial(GraphHopper hopper, Double lat,Double lon, String size, Integer stops, String vehicle) {
        InputStream is =null;
        JSONObject json = null;
        String url = new String("https://overpass-api.de/api/interpreter?data=%5Bout%3Ajson%5D%5Btimeout%3A25%5D%3B%28node%5B%22historic%22%5D%28around%3A"+size+","+lat.toString()+","+lon.toString()+"%29%3B%29%3Bout%3B%3E%3Bout%20skel%20qt%3B%0A");
        try {
            is = new URL(url).openStream();
        }catch(MalformedURLException murle){

        }catch(IOException ioe){

        }finally{
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }
                String jsonText = sb.toString();
                json = new JSONObject(jsonText);
            } catch(IOException ioe) {
            }finally {
                try {
                    is.close();
                }catch(IOException ioe){

                }
              
            }
        }

        Boolean success = false;
       
        GHRequest ghreq = new GHRequest().setVehicle(vehicle).addPoint(new GHPoint(lat, lon))
            .setAlgorithm(Parameters.Algorithms.ROUND_TRIP);
            ghreq.getHints().put(Parameters.Algorithms.RoundTrip.DISTANCE,size);

        if(json.getJSONArray("elements").length() != new Integer(0)) {
            
            ghreq = new GHRequest().setVehicle("hike");
            ghreq.addPoint(new GHPoint(lat,lon));
            JSONArray poi = json.getJSONArray("elements");

            if(stops > json.getJSONArray("elements").length()){
                stops=json.getJSONArray("elements").length();
            }
    
            for(int i = 0; i < stops-1; i++){
                Double latStop = Double.parseDouble(poi.getJSONObject(i).get("lat").toString());
                Double lonStop = Double.parseDouble(poi.getJSONObject(i).get("lon").toString());
                ghreq.addPoint(new GHPoint(latStop,lonStop));
            }
            ghreq.addPoint(new GHPoint(lat,lon));

            success = true;
        }
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

        JSONObject body = new JSONObject("{ \"points\": ["+path.getPoints().toString().replace('(', '[').replace(')', ']')+"], \"time\": "+timeInMs+", \"distance\": "+distance+"}");
        body.put("success",success);
        if(success){
            JSONArray historic_points = new JSONArray();
            JSONArray poi = json.getJSONArray("elements");
            for(int i = 0; i < stops-1; i++){
                Double latStop = Double.parseDouble(poi.getJSONObject(i).get("lat").toString());
                Double lonStop = Double.parseDouble(poi.getJSONObject(i).get("lon").toString());
                JSONObject point = new JSONObject();
                point.put("lat", latStop);
                point.put("lon", lonStop);
                point.put("tags",poi.getJSONObject(i).get("tags"));
                historic_points.put(point);
            }
            body.put("historic_points",historic_points);
        }
        return body;
    };
}
