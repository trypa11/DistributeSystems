import java.io.*;
import java.net.*;
import java.util.*;

public class ActionForWorkers extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    ArrayList<Waypoint> map;
    ChunksCalc c=null;
    Socket connection;

    public ActionForWorkers(Socket connection, ArrayList<Waypoint> map) {
        try {
            out = new ObjectOutputStream((connection.getOutputStream()));
            in = new ObjectInputStream((connection.getInputStream()));
            this.connection = connection;
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
            this.c=c;

            //System.out.println("Distance: " + c.getDist());
            //System.out.println("Average Speed: " + c.getAverageSpeed());
            //System.out.println("Total Elevation: " + c.getTotalElevation());
            //System.out.println("Total Time: " + c.getTotalTime());


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

    //get method
    public ChunksCalc getChunksCalc(){
        return c;
    }
    public Socket getSocket(){
        return connection;
    }

}
