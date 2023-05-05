import java.io.*;
import java.net.*;
import java.util.*;

public class ActionForWorkers extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ChunksCalc cc;
    private ArrayList<Waypoint> chunk;
    private static SynchronizedQueue<ActionForWorkers> queue = new SynchronizedQueue<ActionForWorkers>();

    public ActionForWorkers(Socket connection, ArrayList<Waypoint> chunk) throws Exception {
        try {
            out = new ObjectOutputStream((connection.getOutputStream()));
            in = new ObjectInputStream((connection.getInputStream()));
            this.chunk = chunk;
            queue.add(this);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void run() {
        try {      
            ActionForWorkers w = queue.poll();
            w.out.writeObject(chunk);
            w.out.flush();

            ChunksCalc c = (ChunksCalc) w.in.readObject();
            w.cc = c;
            
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    // get calculated chunk 
    public ChunksCalc getChunksCalc() {
        return cc;
    }

}