import java.io.*;
import java.net.*;
import java.util.*;

public class ActionsForClients extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    ArrayList<File> gpxFiles;
    File results=null;

    public ActionsForClients(Socket connection) {
        try {
            out = new ObjectOutputStream((connection.getOutputStream()));
            in = new ObjectInputStream((connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void run() {
        try {

            gpxFiles = (ArrayList<File>) in.readObject();
            //wait until the results are ready
            if(results==null){
                try{
                wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            //send the results to the client
            out.writeObject(results);
            out.flush();
            
            



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
    public ArrayList<File> getGpxFile() throws IOException, ClassNotFoundException {
        return gpxFiles;
    }

    public void setResultsFile(File results) throws IOException, ClassNotFoundException {
        this.results = results;
    }

}