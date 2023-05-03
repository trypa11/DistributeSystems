import java.io.*;
import java.util.*;
import java.net.*;

public class Master {
    ServerSocket s;
    ServerSocket s_c;
    Socket provider_socket=null;
    Socket client_socket=null;
    ArrayList<Double> total_dist = new ArrayList<Double>();
    ArrayList<Double> total_averageSpeed = new ArrayList<Double>();
    ArrayList<Double> total_totalElevation = new ArrayList<Double>();
    ArrayList<Double> total_totalTime = new ArrayList<Double>();
    ArrayList<ChunksCalc> reducelistchunk = new ArrayList<ChunksCalc>();
    Queue<File> queue_gpx = new LinkedList<File>();
    // array list that save the chunks from diffrent gpx file
    ArrayList<ArrayList<ArrayList<Waypoint>>> chunkslist = new ArrayList<ArrayList<ArrayList<Waypoint>>>();
    File gpx;
    File results;

    public void StatisticCalculator(ArrayList<Double> total_dist, ArrayList<Double> total_totalElevation,
            ArrayList<Double> total_totalTime) {
        double average_dist = 0;
        double average_totalElevation = 0;
        double average_totalTime = 0;
        for (int i = 0; i < total_dist.size(); i++) {
            average_dist += total_dist.get(i);
            average_totalElevation += total_totalElevation.get(i);
            average_totalTime += total_totalTime.get(i);
        }
        average_dist = average_dist / total_dist.size();
        average_totalElevation = average_totalElevation / total_totalElevation.size();
        average_totalTime = average_totalTime / total_totalTime.size();
        System.out.println("Average Distance: " + average_dist);
        System.out.println("Average Total Elevation: " + average_totalElevation);
        System.out.println("Average Total Time: " + average_totalTime);
    }

    // reduce method
    public void reduce(ArrayList<ChunksCalc> reducelistchunk) {
        double total_dist = 0;
        double total_averageSpeed = 0;
        double total_totalElevation = 0;
        double total_totalTime = 0;
        // add all the data from the chunks
        for (int i = 0; i < reducelistchunk.size(); i++) {
            total_dist += reducelistchunk.get(i).getDist();
            total_averageSpeed += reducelistchunk.get(i).getAverageSpeed();
            total_totalElevation += reducelistchunk.get(i).getTotalElevation();
            total_totalTime += reducelistchunk.get(i).getTotalTime();
        }
        // save it to the total arraylists
        this.total_dist.add(total_dist);
        this.total_averageSpeed.add(total_averageSpeed);
        this.total_totalElevation.add(total_totalElevation);
        this.total_totalTime.add(total_totalTime);
    }

    public static void main(String[] args) throws Exception {
        Master m = new Master();
        m.openserverclient();
        m.openServer();
    }
    //write open server method for client 
    
    public void openserverclient() throws Exception{
        s_c = new ServerSocket(6666, 10);
        try{
            while (queue_gpx.isEmpty()) {
                Socket client_socket = s_c.accept();
                /* Handle the request */
                ActionsForClients c = new ActionsForClients(client_socket);
                c.start();
                gpx = ((ActionsForClients) c).getGpxFile();
                queue_gpx.add(gpx);

            }
    } catch (IOException ioException) {
        ioException.printStackTrace();
    } finally {
        try {
            if (client_socket != null){
                client_socket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    }

    public void openServer() throws Exception {
        try {
            /* Create Server Socket */
            s = new ServerSocket(6969, 10);
            while (true) {
                /* Accept the connections */
                File gpx = queue_gpx.poll();
                Socket provider_socket = s.accept();
                Thread d = new ActionForWorkers(provider_socket,gpx);
                d.start();
                reduce(((ActionForWorkers) d).getReduceList());

            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                provider_socket.close();
                
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

}
