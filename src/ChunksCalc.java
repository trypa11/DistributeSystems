import java.io.Serializable;
import java.util.*;
public class ChunksCalc implements Serializable {
    ArrayList<Waypoint> waypoints;

    double dist;
    
    public ChunksCalc(ArrayList<Waypoint> waypoints) {
        this.waypoints = waypoints;

    }


    public double getDist() {
        dist = 0;
        for (int i = 0; i < waypoints.size()-1; i++) {
            double lon = waypoints.get(i).getLon();
            double lat = waypoints.get(i).getLat();
            double lon2 = waypoints.get(i+1).getLon();
            double lat2 = waypoints.get(i+1).getLat();
            dist += distCalculation(lon, lat, lon2, lat2);
        }
        return dist;
    }
    //calculate distance between two points
    public double distCalculation(double lon,double lat,double lon2,double lat2){
        double R = 6371e3; // metres
        double f1 = lat * Math.PI/180; // φ, λ in radians
        double f2 = lat2 * Math.PI/180;
        double df = (lat2-lat) * Math.PI/180;
        double dl = (lon2-lon) * Math.PI/180;

        double a = Math.sin(df/2) * Math.sin(df/2) +
                Math.cos(f1) * Math.cos(f2) *
                Math.sin(dl/2) * Math.sin(dl/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = R * c; // in metres
        return d;
        
    }
  

        

    public double getAverageSpeed() {
        //calculate speed for every waypoint and then average it
        double speed = 0;
        for (int i = 0; i < waypoints.size()-1; i++) {
            double lon = waypoints.get(i).getLon();
            double lat = waypoints.get(i).getLat();
            double lon2 = waypoints.get(i+1).getLon();
            double lat2 = waypoints.get(i+1).getLat();
            double time = waypoints.get(i+1).getTime() - waypoints.get(i).getTime();
            speed += distCalculation(lon, lat, lon2, lat2)/time;
        }
        return speed/(waypoints.size()-1);
        
    }
    public double getTotalElevation() {
        double ele = 0;
        for (int i = 0; i < waypoints.size()-1; i++) {
            //checks if  waypoints are at the same elevation
            if (waypoints.get(i).getEle() != waypoints.get(i+1).getEle()) {
                //checks if the next waypoint is higher than the previous
                if (waypoints.get(i).getEle() < waypoints.get(i+1).getEle()) {
                    ele += waypoints.get(i+1).getEle() - waypoints.get(i).getEle();
                }
            }
        }
        return ele;
    }
    public double getTotalTime() {
        //get the time of the first and last waypoint
        double time = waypoints.get(waypoints.size()-1).getTime() - waypoints.get(0).getTime();
        return time;
    }
    //get waypoints
    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

}