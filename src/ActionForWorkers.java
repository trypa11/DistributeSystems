import java.io.*;
import java.net.*;
public class ActionForWorkers extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;

    public ActionForWorkers(Socket connection) {
        try {
            out= new ObjectOutputStream((connection.getOutputStream()));
            in = new ObjectInputStream((connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            ChunksCalc c = (ChunksCalc) in.readObject();
            c.getDist();
            c.getAverageSpeed();
            c.getTotalElevation();
            c.getTotalTime();

            out.writeObject(c);
            out.flush();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            throw new RuntimeException(e);

        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
