package eu.depa.browsing.stack;

public class Tab {

    String address;
    int position;
    boolean isPrivate;

    public Tab (String address, int position, boolean state) {
        this.address = address;
        this.position = position;
        this.isPrivate = state;
    }

    public Tab () {
        this.position = 0;
        this.isPrivate = false;
    }

    public void setAddress (String address) {
        this.address = address;
    }

    public String getAddress () {
        return address;
    }

    public void setPosition (int position) {
        this.position = position;
    }

    public int getPosition () {
        return position;
    }

    public void setPrivate (boolean state) {
        this.isPrivate = state;
    }

    public boolean isPrivate () {
        return isPrivate;
    }
}
