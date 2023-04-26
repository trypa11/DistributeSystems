import java.io.*;
import java.net.*;
import java.util.*;

public class ActionForWorkers extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    ArrayList<Waypoint> map;
    double dist;
    double averageSpeed;
    double totalElevation;
    double totalTime;

    public ActionForWorkers(Socket connection, ArrayList<Waypoint> map) {
        try {
            out = new ObjectOutputStream((connection.getOutputStream()));
            in = new ObjectInputStream((connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.map = map;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void run() {

        try {
            out.writeObject(map);
            out.flush();

            ChunksCalc c = (ChunksCalc) in.readObject();

            System.out.println("Distance: " + c.getDist());
            System.out.println("Average Speed: " + c.getAverageSpeed());
            System.out.println("Total Elevation: " + c.getTotalElevation());
            System.out.println("Total Time: " + c.getTotalTime());
            setDist(c.getDist());
            setAverageSpeed(c.getAverageSpeed());
            setTotalElevation(c.getTotalElevation());
            setTotalTime(c.getTotalTime());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    public double getDist(){
        return dist;
    }
    public double getAverageSpeed(){
        return averageSpeed;
    }
    public double getTotalElevation(){
        return totalElevation;
    }
    public double getTotalTime(){
        return totalTime;
    }
    //create set method for each variable
    public void setDist(double dist){
        this.dist = dist;
    }
    public void setAverageSpeed(double averageSpeed){
        this.averageSpeed = averageSpeed;
    }
    public void setTotalElevation(double totalElevation){
        this.totalElevation = totalElevation;
    }
    public void setTotalTime(double totalTime){
        this.totalTime = totalTime;
    }
}
