import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.text.*;
import java.io.Serializable;
public class GPXParser implements Serializable {

    private ArrayList<Waypoint> waypoints;
    

    public GPXParser(String filePath) throws ParserConfigurationException, SAXException, IOException, ParseException {
        // Load GPX file
        File gpxFile = new File(filePath);
        InputStream is = new FileInputStream(gpxFile);

        // Parse GPX file
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);

        // Create a new HashMap to store the waypoints
        waypoints = new ArrayList<Waypoint>();

        // Get all waypoints from GPX file
        NodeList wptList = doc.getElementsByTagName("wpt");
        for (int i = 0; i < wptList.getLength(); i++) {
            Element wptElement = (Element) wptList.item(i);

            // Get latitude and longitude of the waypoint
            double lat = Double.parseDouble(wptElement.getAttribute("lat"));
            double lon = Double.parseDouble(wptElement.getAttribute("lon"));

            // Get elevation and time of the waypoint
            double ele = Double.parseDouble(wptElement.getElementsByTagName("ele").item(0).getTextContent());
            String timeStr = wptElement.getElementsByTagName("time").item(0).getTextContent();

            // Convert time to Unix time in milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date time = sdf.parse(timeStr);
            long timeInMillis = time.getTime();

            // Create a new Waypoint object and add it to the HashMap
            int id=i;
            Waypoint waypoint = new Waypoint(id, lat, lon, ele, timeInMillis);
            waypoints.add(waypoint);
        }
    }

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }
}

class Waypoint implements Serializable {


    private int id;
    private double lat;
    private double lon;
    private double ele;
    private long time;

    public Waypoint(int id, double lat, double lon, double ele, long time) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.ele = ele;
        this.time = time;
    }

    //return id of waypoint
    public int getId() { return id; }
    //set id of waypoint
    public void setId(int id) { this.id = id; }

    //return latitude of waypoint
    public double getLat() {
        return lat;
    }
    //return longitude of waypoint
    public double getLon() {
        return lon;
    }
    //return elevation of waypoint
    public double getEle() {
        return ele;
    }
    //return time when waypoint was reached
    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "(" + lat + ", " + lon + ") @ " + ele + "m, " + time + "ms";
    }
}