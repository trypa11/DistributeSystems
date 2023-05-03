import java.io.*;
import java.net.*;

public class Client extends Thread {
    private String filepath;
    private File gpxFile;

    Client(String filep) {
        this.filepath = filep;
        this.gpxFile = new File(filepath);
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
            out.writeObject(gpxFile);
            out.flush();

            // File results = (File)in.readObject();
            // System.out.println(results);

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
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

    public static void main(String[] args) throws Exception {
        Client c = new Client("C:\\Users\\user\\Desktop\\route1.gpx");
        c.start();
    }

}