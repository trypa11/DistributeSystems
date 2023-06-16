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
    Segments seg = new Segments();
    ArrayList<Segments> seglist = new ArrayList<Segments>();
    ArrayList<File> gpxFiles = new ArrayList<File>();
    ArrayList<UserData> userlist = new ArrayList<UserData>();
    String currUser;
    
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
        try {
            results = new File("results.txt");
            FileWriter fw = new FileWriter(results);
            fw.write(currUser + "\n");
            fw.write("Average Distance: " + average_dist + "\n");
            fw.write("Average Total Elevation: " + average_totalElevation + "\n");
            fw.write("Average Total Time: " + average_totalTime + "\n");
            //write the total dist , total elevation and total time to the file for each user
            for (int i = 0; i < userlist.size(); i++) {
                fw.write("User: " + userlist.get(i).getName() + "\n");
                fw.write("Total Distance: " + userlist.get(i).getDist() + "\n");
                fw.write("Total Elevation: " + userlist.get(i).getTotalElevation() + "\n");
                fw.write("Total Time: " + userlist.get(i).getTotalTime() + "\n");
            }
            //write the segments to the file
            for(int i = 0; i < seglist.size(); i++){
                int n= i+1;
                fw.write("Segment: "+n+"\n"+seglist.get(i).printLeaderboard());
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    // reduce method
    public synchronized void reduce(String user) {
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
        if (currUser.equals("gpxgenerator.com")) {
                Segments seg = new Segments();
                seg.setSegments(reducelistchunk); 
                seglist.add(seg);           
        }else{

        boolean flag = false;
        for (int i = 0; i < userlist.size(); i++) {
            if (userlist.get(i).getName().equals(user)) {
                userlist.get(i).addDist(total_dist);
                userlist.get(i).addTotalElevation(total_totalElevation);
                userlist.get(i).addTotalTime(total_totalTime);
                userlist.get(i).addAverageSpeed(total_averageSpeed);
                flag = true;
            }
        }
        if (flag == false) {
            UserData u = new UserData(user, total_dist, total_averageSpeed, total_totalElevation, total_totalTime);
            userlist.add(u);
        }
        //add the data to the array list of total data
        this.total_dist.add(total_dist);
        this.total_totalElevation.add(total_totalElevation);
        this.total_totalTime.add(total_totalTime);
        this.total_averageSpeed.add(total_averageSpeed);
        //for each segment add the user
        for (int i = 0; i < seglist.size(); i++) {
            seglist.get(i).setUser(reducelistchunk);
        }
        }
    

        this.reducelistchunk.clear();
    }

    public static void main(String[] args) throws Exception {
        Master m = new Master();
        m.openserverclient();
    }

    public void openserverclient() throws Exception {
        try {
            s_c = new ServerSocket(1234, 10);
            while (true) {
                Socket client_socket = s_c.accept();
                /* Handle the request */
                ActionsForClients c = new ActionsForClients(client_socket);
                c.start();
                synchronized (c) {
                    c.wait(10);
                }
                gpx = ((ActionsForClients) c).getGpxFile();
                gpxFiles.add(gpx);
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
                String user=chunkslist.get(0).get(0).getUser();
                this.currUser=user;
                int curr_chunk = 0;
                while (curr_chunk < chunkslist.size()) {
                    /* Accept the connections */
                    Socket provider_socket = s.accept();
                    ActionForWorkers d = new ActionForWorkers(provider_socket, chunkslist.get(curr_chunk));
                    d.start();
                    //Thread.sleep(10);
                    synchronized (d) {
                        d.wait(10);
                    }
                    curr_chunk++;
                    reducelistchunk.add(d.getChunksCalc());
                }
            reduce(user);
            gpx_num++;
            gpxFiles.clear();
            this.chunkslist.clear();
            
        }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (provider_socket != null){
                    provider_socket.close();
                }
                if (s != null) {
                    s.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

}
