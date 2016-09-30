
package MiniMinecraft;

public class Cloud {
    public static final int CLOUD1 = 33, CLOUD2 = 34;
    private double x;
    private final double y;
    protected final int cloudID, hSpeed;

    // creates a cloud 200 pixels off screen with random y-coord
    // between -100 to 50
    public Cloud() {
        x = -200 - Map.hOffset;
        y = 150 * Math.random() - 100;
        hSpeed = (int)(Math.random()*4 + 1); //picks a random speed
        cloudID = (int)(Math.random() * 2 + 33); //picks a random cloud from two
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // returns false if the cloud is off the screen (on the right) which will delete the cloud
    public boolean move() {
        return (x += hSpeed) > Map.canvas.getWidth() - Map.hOffset;
    }
}
