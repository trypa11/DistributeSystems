import java.io.*;
import java.util.*;
import java.net.*;


public class Master {
    ServerSocket s;
    Socket providerSocket;
    private int file_num = 1;
    ArrayList<Waypoint> map = new ArrayList<Waypoint>();
    ArrayList<Thread> connections = new ArrayList<Thread>();
    ArrayList<Double> total_dist = new ArrayList<Double>();
    ArrayList<Double> total_averageSpeed = new ArrayList<Double>();
    ArrayList<Double> total_totalElevation = new ArrayList<Double>();
    ArrayList<Double> total_totalTime = new ArrayList<Double>();

    public ArrayList<ArrayList<Waypoint>> Chunk() throws Exception {
        ArrayList<Waypoint> map = new ArrayList<Waypoint>();
        GPXParser parser = new GPXParser("C:\\Users\\user\\Desktop\\route1.gpx");
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
        return chunks;
    }

    public ArrayList<Waypoint> getChunk(ArrayList<ArrayList<Waypoint>> chunks,int i){
        ArrayList<Waypoint> chunk = new ArrayList<Waypoint>();
        chunk = chunks.get(i);
        return chunk;
    }

    public static void main(String[] args) throws Exception {
        new Master().openServer();

    }

    void openServer() throws Exception {
        try {

            /* Create Server Socket */
            s = new ServerSocket(6969, 10);

            while (true) {
                /* Accept the connection */
                providerSocket = s.accept();
                /* Handle the request */
                Thread d = new ActionForWorkers(providerSocket,map=getChunk(Chunk(),1));
                connections.add(d);
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
