import java.io.*;
import java.util.*;
//import java.time.*;
import java.net.*;
import java.lang.*;


public class Master {
    ServerSocket s;
    Socket providerSocket;
    private int file_num=1;

    public ArrayList<Waypoint> Map( ArrayList<Waypoint> waypoints) {
        ArrayList<Waypoint> map = new ArrayList<Waypoint>();
        GPXParser parser = new GPXParser("C:\\Users\\user\\Desktop\\route1.gpx");
        map = parser.getWaypoints();
        for (int i = 0; i < waypoints.size(); i++) {
            waypoints.get(i).setId(file_num*1000+i);
            map.add( waypoints.get(i));
        }
        file_num++;
        return map;

    }

// call worker thread to handle the request
    
/*public static void main(String[] args) {
    Worker w1 = new Worker(1,2);
    Worker w2 = new Worker(3,4);
    w1.start();
    w2.start();
}*/

    void openServer() throws IOException {
        try {

            /* Create Server Socket */
            s= new ServerSocket(4321, 10);

            while (true) {
                /* Accept the connection */
                providerSocket= s.accept();
                /* Handle the request */
                Thread d = new ActionForWorkers(providerSocket);
                d.start();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }



}






