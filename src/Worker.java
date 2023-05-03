import java.io.*;
import java.util.*;
import java.net.*;

public class Worker {
    private ArrayList<Waypoint> waypoints;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private Socket requestSocket = null; 

    Worker() {
    try{
        this.requestSocket = new Socket("localhost", 6666);
        this.out= new ObjectOutputStream(requestSocket.getOutputStream());
        this.in = new ObjectInputStream((requestSocket.getInputStream()));
    } catch (UnknownHostException unknownHost) {
        System.err.println("You are trying to connect to an unknown host!");
    } catch (IOException ioException) {
        ioException.printStackTrace();
    } finally {

        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (requestSocket != null) {
                requestSocket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }
    }
    public void ProcessSendChunk() throws IOException, ClassNotFoundException {
        try{
        ChunksCalc c = new ChunksCalc(waypoints);

        c.getDist();
        c.getAverageSpeed();
        c.getTotalElevation();
        c.getTotalTime();

        out.writeObject(c);
        out.flush();
        while (requestSocket.isConnected()) {
            c = new ChunksCalc(waypoints);

            c.getDist();
            c.getAverageSpeed();
            c.getTotalElevation();
            c.getTotalTime();

            out.writeObject(c);
            out.flush();
        }
    } catch (UnknownHostException unknownHost) {
        System.err.println("You are trying to connect to an unknown host!");
    } catch (IOException ioException) {
        ioException.printStackTrace();
    } finally {

        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (requestSocket != null) {
                requestSocket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    }
    public void readwaypoints(){
        new Thread (new Runnable() {
            @Override
            public void run() {
                try{
                        //requestSocket = new Socket("localhost", 6666);
                        //out= new ObjectOutputStream(requestSocket.getOutputStream());
                        //in = new ObjectInputStream((requestSocket.getInputStream()));
                        waypoints = (ArrayList<Waypoint>) in.readObject();

                    } catch (UnknownHostException unknownHost) {
                        System.err.println("You are trying to connect to an unknown host!");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
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
                            if (requestSocket != null) {
                                requestSocket.close();
                            }
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                
                    }
                }
            
        }).start();
    }
    
    



    public static void main(String[] args) throws Exception {
        Worker w = new Worker();
        w.readwaypoints();
        w.ProcessSendChunk();
    }

}
