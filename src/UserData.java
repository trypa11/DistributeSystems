public class UserData {
    private double dist;
    private double averageSpeed;
    private double totalElevation;
    private double totalTime;
    private String name;

    public UserData(String name ,double dist, double averageSpeed, double totalElevation, double totalTime) {
        this.dist = dist;
        this.averageSpeed = averageSpeed;
        this.totalElevation = totalElevation;
        this.totalTime = totalTime;
        this.name = name;
    }

    public double getDist() {
        return dist;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public double getTotalElevation() {
        return totalElevation;
    }

    public double getTotalTime() {
        return totalTime;
    }
    public String getName() {
        return name;
    }
    public void addDist(double dist) {
        this.dist += dist;
    }
    public void addAverageSpeed(double averageSpeed) {
        this.averageSpeed += averageSpeed;
    }
    public void addTotalElevation(double totalElevation) {
        this.totalElevation += totalElevation;
    }
    public void addTotalTime(double totalTime) {
        this.totalTime += totalTime;
    }

    
}
