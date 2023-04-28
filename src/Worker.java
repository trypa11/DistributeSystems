import java.io.*;
import java.util.*;
import java.net.*;

public class Worker extends Thread {
    ArrayList<Waypoint> waypoints;

    // Worker(ArrayList<Waypoint> waypoints
    // this.waypoints = waypoints;

    // }

    public void run() {
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
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Worker w = new Worker();
        w.start();
        Worker w2 = new Worker();
        w2.start();
        // restart workers after they finish
        while (true) {
            if (!w.isAlive()) {
                w = new Worker();
                w.start();
            }
            if (!w2.isAlive()) {
                w2 = new Worker();
                w2.start();
            }
        }
    }

}
