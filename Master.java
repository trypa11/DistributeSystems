import java.io.*;
import java.util.*;

public class Master {
    //write a method that reads a xml file and returns a map of words(lat,lon,time,ele)
    //and their numbers
    public static Map<String, Long> readXML(String filename) throws Exception {
        //create a map of words and their numbers
        Map<String, Long> map = new HashMap<>();
        //create a file object
        File file = new File(filename);
        //create a scanner object
        Scanner scanner = new Scanner(file);
        //loop through the file
        while (scanner.hasNextLine()) {
            //read the line
            String line = scanner.nextLine();
            //remove all the special characters
            line = line.replace("<", "")
                    .replace(">", "")
                    .replace("/", "")
                    .replace("=", "")
                    .replace("\"", "");
            //split the line into words
            String[] words = line.split(" ");
            //loop through the words
            for (String word : words) {
                //System.out.println(word);
                //check if the word is lat, lon, time, ele
                if (word.contains("lat") ) {
                    word=word.replace("lat", "");
                    System.out.println(word);
                    long num=Long.parseLong(word);
                    map.put("lat", num);
                } else if (word.contains("lon") ) {
                    word=word.replace("lon", "");
                    System.out.println(word);
                    long num=Long.parseLong(word);
                    map.put("lon", num);
                } else if (word.contains("time") ) {
                    word=word.replace("time", "");
                    System.out.println(word);
                    //long num=Long.parseLong(word);
                    //map.put("time", num);
                } else if (word.contains("ele") ) {
                    word=word.replace("ele", "");
                    System.out.println(word);
                    long num=Long.parseLong(word);
                    map.put("ele", num);

                }
            }
        }

        //close the scanner
        scanner.close();
        //return the map
        return map;
    }
}
