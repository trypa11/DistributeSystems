import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client extends Thread {

    private ArrayList<File>gpxFiles  = new ArrayList<File>();

    Client(String filep) {
        listfp(filep, gpxFiles);
    }

    public void run() {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        Socket requestSocket = null;
        try {

            requestSocket = new Socket("localhost", 6666);

            /* Create the streams to send and receive data from server */
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream((requestSocket.getInputStream()));
            // write the file
            out.writeObject(gpxFiles);
            out.flush();

            File results = (File)in.readObject();
            System.out.println(results);
            

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (in != null){
                    in.close();
                }
                if (out != null){
                    out.close();
                }
                if (requestSocket != null){
                    requestSocket.close();
                }    
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    public void listfp(String filepath, ArrayList<File>gpxFiles) {
        File gpxfilepath = new File(filepath);
        File[] fList = gpxfilepath.listFiles();
        if(fList != null)
            for (File file : fList) {      
                if (file.isFile()) {
                    gpxFiles.add(file);
                } else if (file.isDirectory()) {
                    listfp(file.getAbsolutePath(), gpxFiles);
                }
            }
    }
    public static void main(String[] args) throws Exception {
        Client c = new Client("C:\\Users\\user\\Desktop");
        c.start();
    }

}