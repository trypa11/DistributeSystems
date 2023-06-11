import java.io.*;
import java.net.*;

public class ActionsForClients extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    String gpxString;
    File gpxFiles;
    File results=null;
    String resultsString;

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

            gpxString = in.readUTF();
            gpxFiles = createFile(gpxString);
            //wait until the results are ready
            if(results==null){
                try{
                wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            //send the results to the client
            resultsString = fileToString(results);
            out.writeUTF(resultsString);
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
    public File getGpxFile() throws IOException, ClassNotFoundException {
        return gpxFiles;
    }

    public void setResultsFile(File results) throws IOException, ClassNotFoundException {
        this.results = results;
    }
    public File createFile(String gpx) throws IOException, ClassNotFoundException { 
        File file = new File("gpx.txt");
        FileWriter fr = new FileWriter(file);
        fr.write(gpx);
        fr.close();
        return file;
    }
    //create a file to string method
    public String fileToString(File file) throws IOException, ClassNotFoundException{
        String results = "";
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while((line=br.readLine())!=null){
            results+=line;
        }
        br.close();
        return results;
    }







}