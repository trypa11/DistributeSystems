import java.io.*;
import java.util.*;
import java.net.*;

public class Master {
    ServerSocket s;
    ServerSocket s_c;
    Socket provider_socket = null;
    Socket client_socket = null;
    ArrayList<Double> total_dist = new ArrayList<Double>();
    ArrayList<Double> total_averageSpeed = new ArrayList<Double>();
    ArrayList<Double> total_totalElevation = new ArrayList<Double>();
    ArrayList<Double> total_totalTime = new ArrayList<Double>();
    ArrayList<ChunksCalc> reducelistchunk = new ArrayList<ChunksCalc>();
    //array list file 
    ArrayList<File> gpxFiles = new ArrayList<File>();
    
    // array list that save the chunks from diffrent gpx file
    ArrayList<ArrayList<Waypoint>> chunkslist = new ArrayList<ArrayList<Waypoint>>();
    File gpx;
    File results;

    public File StatisticCalculator(ArrayList<Double> total_dist, ArrayList<Double> total_totalElevation,
            ArrayList<Double> total_totalTime, ArrayList<Double> total_averageSpeed) {
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
        //write the results to the file
        try {
            results = new File("results.txt");
            FileWriter fw = new FileWriter(results);
            fw.write("Average Distance: " + average_dist + "\n");
            fw.write("Average Total Elevation: " + average_totalElevation + "\n");
            fw.write("Average Total Time: " + average_totalTime + "\n");
            //write the total dist , total elevation and total time to the file
            for (int i = 0; i < total_dist.size(); i++) {
                fw.write(i + 1 + "th file" + "\n");
                fw.write("Total Distance: " + total_dist.get(i) + "\n");
                fw.write("Total Total Elevation: " + total_totalElevation.get(i) + "\n");
                fw.write("Total Total Time: " + total_totalTime.get(i) + "\n");
                fw.write("Total Average Speed: " + total_averageSpeed.get(i) + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    // reduce method
    public void reduce() {
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
        this.reducelistchunk.clear();
    }

    public static void main(String[] args) throws Exception {
        Master m = new Master();
        m.openserverclient();
        m.reduce();
        // print the results
        System.out.println("Total Distance: " + m.total_dist);
        System.out.println("Total Average Speed: " + m.total_averageSpeed);
        System.out.println("Total Total Elevation: " + m.total_totalElevation);
        System.out.println("Total Total Time: " + m.total_totalTime);
    }

    public void openserverclient() throws Exception {
        try {
            s_c = new ServerSocket(6666, 10);
            while (true) {
                Socket client_socket = s_c.accept();
                /* Handle the request */
                ActionsForClients c = new ActionsForClients(client_socket);
                c.start();
                // wait 1 sec for the client to finish
                synchronized (c) {
                    c.wait(100);
                }
                gpxFiles = ((ActionsForClients) c).getGpxFile();
                openServer();
                this.results = StatisticCalculator(total_dist, total_totalElevation, total_totalTime, total_averageSpeed);
                ((ActionsForClients) c).setResultsFile(results);
                synchronized (c)
                {
                    c.notify();
                }


            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (client_socket != null) {
                    client_socket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void openServer() throws Exception {
        try {
            int gpx_num = 0;
            s = new ServerSocket(6969, 10);
            while (gpxFiles.size() > gpx_num) {
                File gpx = gpxFiles.get(gpx_num);
                GPXParser chunk = new GPXParser(gpx);
                this.chunkslist = chunk.Chunk();
                int curr_chunk = 0;
                while (curr_chunk < chunkslist.size()) {
                    /* Accept the connections */
                    Socket provider_socket = s.accept();
                    ActionForWorkers d = new ActionForWorkers(provider_socket, chunkslist.get(curr_chunk));
                    d.start();
                    Thread.sleep(1000);
                    curr_chunk++;
                    reducelistchunk.add(d.getChunksCalc());
                }
            reduce();
            gpx_num++;
            this.chunkslist.clear();
        }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (provider_socket != null){
                    provider_socket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

}
