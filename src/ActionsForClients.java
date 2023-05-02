import java.io.*;
import java.net.*;

public class ActionsForClients extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    File gpxFile;
    File results;
    boolean ready = false;
    int num_gpx=0;

    public ActionsForClients(Socket connection) {
        try {

            out = new ObjectOutputStream((connection.getOutputStream()));
            in = new ObjectInputStream((connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {

            this.gpxFile = (File) in.readObject();
            num_gpx++;
            // if (ready) {
            // out.writeObject(results);
            // out.flush();
            // }

        } catch (

        IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);

        } finally {
            try {
                if (in != null){
                    in.close();
                }
                if (out != null){
                    out.close();
                }

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    // get method of a file from client
    public File getGpxFile() throws IOException, ClassNotFoundException {
        return gpxFile;
    }

    public void setResultsFile(File results) throws IOException, ClassNotFoundException {
        this.results = results;
    }

    public void readyResults(boolean ready) {
        this.ready = true;
    }

    public boolean getReady() {
        return ready;
    }
    public int getNumGpx(){
        return num_gpx;
    }

}