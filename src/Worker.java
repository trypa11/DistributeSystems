//import libraries
import java.io.*;
import java.util.*;
import java.net.*;

public class Worker extends Thread {
    ArrayList<Waypoint> waypoints;

    Worker(ArrayList<Waypoint> waypoints) {
        this.waypoints = waypoints;

    }


        public void run() {
            ObjectOutputStream out= null ;
            ObjectInputStream in = null ;
            Socket requestSocket= null ;


            try {

                /* Create socket for contacting the server on port 4321*/

                requestSocket = new Socket("localhost", 4321);

                /* Create the streams to send and receive data from server */
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream((requestSocket.getInputStream()));
                /* Write the two integers */

                ChunksCalc c = new ChunksCalc(waypoints);
                out.writeObject(c);
                out.flush();

                /* Print the received result from server */
                ChunksCalc c2 = (ChunksCalc) in.readObject();
                System.out.println("Distance: " + c2.getDist());
                System.out.println("Average Speed: " + c2.getAverageSpeed());
                System.out.println("Total Elevation: " + c2.getTotalElevation());
                System.out.println("Total Time: " + c2.getTotalTime());
            } catch (UnknownHostException unknownHost) {
                System.err.println("You are trying to connect to an unknown host!");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (ClassNotFoundException e){
                throw new RuntimeException(e);

            }  finally{
                try {
                    in.close(); out.close();
                    requestSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }


}
