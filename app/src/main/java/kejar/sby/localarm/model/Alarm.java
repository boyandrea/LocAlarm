package kejar.sby.localarm.model;

/**
 * Created by Irfan Septiadi Putra on 06/05/2016.
 */
public class Alarm {

    private int id;
    private double latitude;
    private double longitude;
    private int radius;
    private int status;
    private String destination;

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getRadius() {
        return radius;
    }
    public int getStatus(){
        return status;
    }

    public String getDestination() {
        return destination;
    }
}
