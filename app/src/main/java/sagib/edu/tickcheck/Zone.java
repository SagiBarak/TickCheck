package sagib.edu.tickcheck;

/**
 * Created by sagib on 14/06/2017.
 */

public class Zone {
    private String name;
    private int capacity;
    private int free;

    public Zone(String name, int capacity, int free) {
        this.name = name;
        this.capacity = capacity;
        this.free = free;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    @Override
    public String toString() {
//        return name + " - מקומות: " + capacity + " פנויים: " + free + "\n";
        String string = String.format("%s - מקומות פנויים: %d\n",name,free);
        return string;
    }
}
