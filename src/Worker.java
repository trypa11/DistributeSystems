import java.io.*;
import java.util.*;
import java.net.*;

public class Worker extends Thread {
    ArrayList<Waypoint> waypoints;


    public synchronized void run() {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        Socket requestSocket = null;

        try {

            /* Create socket for contacting the server on port 6969 */

            requestSocket = new Socket("localhost", 6969);
            /* Create the streams to send and receive data from server */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream((requestSocket.getInputStream()));
            waypoints = (ArrayList<Waypoint>) in.readObject();

            // test to see if waypoints are being read in correctly
            // for (Waypoint w : waypoints) {
            // System.out.println(w);
            // }
            ChunksCalc c = new ChunksCalc(waypoints);

            c.getDist();
            c.getAverageSpeed();
            c.getTotalElevation();
            c.getTotalTime();

            out.writeObject(c);
            out.flush();
            
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (requestSocket != null) {
                    requestSocket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Worker w = new Worker();
        w.start();
        // restart worker after they finish
        while (true) {
            if (!w.isAlive()) {
                w = new Worker();
                w.start();
            }
        }
    }

}

