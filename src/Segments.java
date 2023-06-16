import java.util.ArrayList;
public class Segments {
    private ArrayList<Waypoint> s_w=new ArrayList<Waypoint>();
    private ArrayList<ArrayList<Waypoint>> all_user=new ArrayList<ArrayList<Waypoint>>();
    private ArrayList <Long> timeofusers=new ArrayList<Long>();
    private ArrayList <String> user_names = new ArrayList<String>();

    //set the segments
    public void setSegments(ArrayList<ChunksCalc> segments) {
        for (int i = 0; i < segments.size(); i++) {
            for (int j =0;j<segments.get(i).getWaypoints().size();j++){
                s_w.add(segments.get(i).getWaypoints().get(j));
            }          
        }
    }
    //set the user
    public void setUser(ArrayList<ChunksCalc> user) {
        ArrayList<Waypoint> u_w=new ArrayList<Waypoint>();
        for (int i = 0; i < user.size(); i++) {
            for (int j =0;j<user.get(i).getWaypoints().size();j++){
                u_w.add(user.get(i).getWaypoints().get(j));
            }          
        }
        all_user.add(u_w);
    }
public void checkSegments(int user_num) {
    double tolerance = 0.000045; // Approximately 5 meters in degrees
    ArrayList<Waypoint> useronseg = new ArrayList<Waypoint>();
    for (int i = 0; i < s_w.size(); i++) {
            for (int k = 0; k < all_user.get(user_num).size(); k++) {
            // Check if the u_w waypoint is within the tolerance of the s_w and elevation is within 5 meters
                if (Math.abs(s_w.get(i).getLat() - all_user.get(user_num).get(k).getLat()) < tolerance
                        && Math.abs(s_w.get(i).getLon() - all_user.get(user_num).get(k).getLon()) < tolerance
                        && Math.abs(s_w.get(i).getEle() - all_user.get(user_num).get(k).getEle()) < 5) {
                    useronseg.add(all_user.get(user_num).get(k));
                }
            }
    }
    if (useronseg.size() > 0){
    timeofusers.add(getTotalTime(useronseg)); 
    //get the name of the user
    user_names.add(useronseg.get(user_num).getUser());
    }

}


//create a string to print a leaderboard of the users on the segments sorted by time 
public String printLeaderboard() {
    String leaderboard = "";
    for (int i = 0; i < all_user.size(); i++) {
        checkSegments(i);
    }
    sortLeaderboard();
    for (int i = 0; i < timeofusers.size(); i++) {
        leaderboard += "User: " +user_names.get(i) + " " + "Time:"+timeofusers.get(i) + "\n";
    }
    timeofusers.clear();
    user_names.clear();
    return leaderboard;  
    }

public long getTotalTime( ArrayList<Waypoint> userseg) {
    long totaltime = 0;
    for (int j = 0; j < userseg.size() - 1; j++) {
        totaltime += userseg.get(j + 1).getTime() - userseg.get(j).getTime();
    }
    return totaltime;
}

public void sortLeaderboard() {
    //sort the leaderboard by time 
    //sort the time of the users and the user names in the same order
    for (int i = 0; i < timeofusers.size(); i++) {
        for (int j = 0; j < timeofusers.size() - 1; j++) {
            if (timeofusers.get(j) < timeofusers.get(j + 1)) {
                long temp = timeofusers.get(j);
                timeofusers.set(j, timeofusers.get(j + 1));
                timeofusers.set(j + 1, temp);
                String temp2 = user_names.get(j);
                user_names.set(j, user_names.get(j + 1));
                user_names.set(j + 1, temp2);
            }
        }
    }

}



}
