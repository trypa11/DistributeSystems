import java.io.*;
import java.util.*;
//import java.time.*;
import java.net.*;
import java.lang.*;


public class Master {
    ServerSocket s;
    Socket providerSocket;
    private int file_num = 1;

    public ArrayList<Waypoint> Map(ArrayList<Waypoint> waypoints) throws Exception {
        ArrayList<Waypoint> map = new ArrayList<Waypoint>();
        GPXParser parser = new GPXParser("C:\\Users\\user\\Desktop\\route1.gpx");
        map = parser.getWaypoints();
        for (int i = 0; i < waypoints.size(); i++) {
            waypoints.get(i).setId(file_num * 1000 + i);
            map.add(waypoints.get(i));
        }
        file_num++;
        return map;

    }

    // call worker thread to handle the request

    public static void main(String[] args) throws Exception {
        // Load waypoints from GPX file
        ArrayList<Waypoint> map = new ArrayList<Waypoint>();
        GPXParser parser = new GPXParser("D:\\Downloads\\gpxs\\gpxs\\route1.gpx");
        map = parser.getWaypoints();
        // Split waypoint list into chunks
        int numChunk = 4;
        int chunkSize = map.size() / numChunk;
        ArrayList<ArrayList<Waypoint>> chunks = new ArrayList<ArrayList<Waypoint>>();
        for (int i = 0; i < numChunk; i++) {
            ArrayList<Waypoint> chunk = new ArrayList<Waypoint>();
            for (int j = 0; j < chunkSize; j++) {
                chunk.add(map.get(i * chunkSize + j));
            }
            chunks.add(chunk);
        }
        // round robin sheduling for the chunks to the workers
        int worker_num = 1;
        for (int i = 0; i < numChunk; i++) {
            Thread w = new Worker(chunks.get(i));
            w.start();
            worker_num++;
        }
        // wait for all workers to finish
        for (int i = 0; i < numChunk; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    void openServer() throws IOException {
        try {

            /* Create Server Socket */
            s = new ServerSocket(4321, 10);

            while (true) {

                /* Accept the connection */
                providerSocket = s.accept();
                /* Handle the request */
                Thread d = new ActionForWorkers(providerSocket);
                d.start();
            }

        } catch (

                IOException ioException) {
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






