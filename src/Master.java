import java.io.*;
import java.util.*;
import java.net.*;
import java.time.*;

public class Master {
    ServerSocket s;
    ArrayList<Socket> provider_sockets = new ArrayList<Socket>();

    ArrayList<Waypoint> map = new ArrayList<Waypoint>();
    ArrayList<Double> total_dist = new ArrayList<Double>();
    ArrayList<Double> total_averageSpeed = new ArrayList<Double>();
    ArrayList<Double> total_totalElevation = new ArrayList<Double>();
    ArrayList<Double> total_totalTime = new ArrayList<Double>();
    ArrayList<ArrayList<Waypoint>> chunks = new ArrayList<ArrayList<Waypoint>>();
    ArrayList<ChunksCalc> reducelistchunk = new ArrayList<ChunksCalc>();

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

    public void setChunk(ArrayList<ArrayList<Waypoint>> chunks) throws Exception {
        this.chunks = chunks;
    }

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

    public static void main(String[] args) throws Exception {
        new Master().openServer(2);

    }

    void openServer(int i) throws Exception {
        try {
            /* Create Server Socket */
            s = new ServerSocket(6969, 10);
            Queue<Thread> queue = new LinkedList<Thread>();
            setChunk(Chunk());
            int curr_chunk = 0;
            Instant start;
            int curr_socket = 0;
            while (true) {
                /* Accept the connections */
                for (int j = 0; j < i; j++) {
                    Socket provider_socket = s.accept();
                    provider_sockets.add(provider_socket);
                    /* Handle the request */
                    Thread d = new ActionForWorkers(provider_sockets.get(j), chunks.get(j));
                    curr_chunk++;
                    d.start();
                    synchronized (d) {
                        d.wait();
                    }
                    queue.add(d);
                }
                // check if all chunks are done
                while (curr_chunk < chunks.size()) {
                    Thread d = queue.poll();
                    // print thread name
                    System.out.println("Thread " + d.getName() + " is running");
                    if (d.isAlive()) {
                        synchronized (d) {
                            d.notify();
                        }
                        start = Instant.now();
                        // checks if time that the thread has been running is 1 second
                        if (Duration.between(start, Instant.now()).toMillis() == 1000) {
                            System.out.println("Thread " + d.getName() + " wayting for 1 second");
                            synchronized (d) {
                                d.wait();
                            }
                            queue.add(d);
                        }
                        break;
                    } else {
                        System.out.println("Thread " + d.getName() + " is done");
                        reducelistchunk.add(((ActionForWorkers) d).getChunksCalc());
                        if (curr_chunk < chunks.size()) {
                            Socket provider_socket = s.accept();
                            provider_sockets.set(curr_socket, provider_socket);
                            Thread new_d = new ActionForWorkers(provider_sockets.get(curr_socket),
                                    chunks.get(curr_chunk));
                            curr_chunk++;
                            curr_socket++;
                            if (curr_socket == i) {
                                curr_socket = 0;
                            }
                            new_d.start();
                            synchronized (new_d) {
                                new_d.wait();
                            }
                            // print thread name
                            System.out.println("Thread " + new_d.getName() + " is running");
                            queue.add(new_d);
                        }
                        break;
                    }

                }

            }

        } catch (

        IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                for (int j = 0; j < i; j++) {
                    provider_sockets.get(j).close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

}
