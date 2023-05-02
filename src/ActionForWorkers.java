import java.io.*;
import java.net.*;
import java.util.*;

public class ActionForWorkers extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ChunksCalc c = null;
    private Socket connection;
    private ArrayList<ArrayList<Waypoint>> chunks = new ArrayList<ArrayList<Waypoint>>();
    private ArrayList<ChunksCalc> reducelistchunk = new ArrayList<ChunksCalc>();
    private int curr_chunk = 0;
    private static Queue<Thread> queue = new LinkedList<Thread>();

    public ActionForWorkers(Socket connection, File gpxFile) {
        try {
            out = new ObjectOutputStream((connection.getOutputStream()));
            in = new ObjectInputStream((connection.getInputStream()));
            this.connection = connection;
            synchronized (queue) {
                queue.add(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            GPXParser chunk = new GPXParser(gpxFile);
            this.chunks = chunk.Chunk();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {
        while (connection.isConnected()) {
            try {
                // send chunks with round robin
                while (curr_chunk < chunks.size()) {
                    ArrayList<Waypoint> curr_chunk_waypoints = chunks.get(curr_chunk);
                    ChunksSender(curr_chunk_waypoints);
                    ChunksCalc c = (ChunksCalc) in.readObject();
                    reducelistchunk.add(c);
                    curr_chunk++;
                    synchronized (queue) {
                        queue.add(this);
                        queue.notifyAll();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
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

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

    }

    public synchronized void ChunksSender(ArrayList<Waypoint> curr_chunk_waypoints)
            throws IOException, ClassNotFoundException {
        synchronized (queue) {
            while (queue.peek() != this) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        ActionForWorkers w = (ActionForWorkers) queue.poll();
        w.out.writeObject(curr_chunk_waypoints);
        w.out.flush();
    }

    // get reduce list
    public ArrayList<ChunksCalc> getReduceList() {
        return reducelistchunk;
    }

}
