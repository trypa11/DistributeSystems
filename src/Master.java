import java.io.*;
//import java.util.*;
//import java.time.*;
import java.net.*;
public class Master {
    ServerSocket s;
    Socket providerSocket;
    int chunksize = 5;
    double totaldist = 0;
    double totaltime = 0;
    double totalelevation = 0;
    double totalspeed = 0;

// call worker thread to handle the request
    
public static void main(String[] args) {
    Worker w1 = new Worker(1,2);
    Worker w2 = new Worker(3,4);
    w1.start();
    w2.start();
}

    void openServer() throws IOException {
        try {

            /* Create Server Socket */
            s= new ServerSocket(4321, 10);

            while (true) {
                /* Accept the connection */
                providerSocket= s.accept();
                /* Handle the request */
                Thread d = new ActionForWorkers(providerSocket);
                d.start();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }


}






