
package MiniMinecraft;

// Something affected by gravity
public abstract class Entity {
    public static final int PLAYER = 40, HURT_PLAYER = 41, HLESS_PLAYER = 42,
           ZOMBIE = 43, BURNING_ZOMB = 44, HURT_ZOMB = 45, BLOOD = 46,
           SHEEP = 47, HURT_SHEEP = 48, BASE_MOVE_SPEED = 8, P_DAMAGE1 = 3,
           Z_DAMAGE1 = 5, SHEEP_BAA = 7; // IDs in the audio/image arrays

    public static final double ACCEL = -2;
    public static final boolean LEFT = true, RIGHT = false;
    protected int imageID;
    protected double x, y, vSpeed, hSpeed;

    // used for collision detection & how large the image should be drawn (units=blocks)
    private final double WIDTH, HEIGHT;
    private boolean orientation = RIGHT;

    public Entity(double x, double y, double width, double height, int imageID) {
        WIDTH = width;
        HEIGHT = height;
        this.imageID = imageID;
        this.x = x;
        this.y = y;
    }

    public void update() {
        // attempts to accelerate if it can, then the object attempts to move down with it's speed
        vSpeed += ACCEL;
        if(!isObstructed(x, y - vSpeed)) {
            y -= vSpeed;
        } else {
            vSpeed = 0;
        }

        // if there is any hSpeed and the object isn't obstructed, the player moves horizontal with it's speed
        if(Math.abs(hSpeed) > 0) {
            hSpeed +=  .05 * ( hSpeed < 0 ? 1 : -1 );
            if(!isObstructed(x + hSpeed, y)) {
                x += hSpeed;
            } else {
                hSpeed = 0;
            }
        }
    }

    // checks if the entity is obstructed when it's top left corner is at the point (x, y)
    public boolean isObstructed(double x, double y) {
        double diffX, diffY;
        // looks through each block
        for(Block b : Map.blockMap) {
            diffX = b.x - x;
            diffY = b.y - y;

            // checks with the entity's width and height if it would be obstructed at (x, y)
            if(-Map.BSIZE < diffX && diffX <= WIDTH * Map.BSIZE && Map.BSIZE * HEIGHT >= diffY && diffY >= -Map.BSIZE ) {
                // if the entity is obstructed and the obstructing block is under them,
                // the entity "completes its movement" by adding any remaining space between the block and the entity
                double moveComplete = (b.y - this.y) - Map.BSIZE * HEIGHT;
                this.y += (moveComplete < .01)? 0 : moveComplete;
                return true;
            }
        }

        return false;
    }

    public void setVSpeed(double speed) {
        this.vSpeed = speed;
    }

    public boolean getOrientation() {
        return orientation;
    }

    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }

    public double getWidth() {
        return WIDTH;
    }

    public double getHeight() {
        return HEIGHT;
    }

    public int screenX() {
        return (int)x + Map.hOffset;
    }
    public int screenY() {
        return (int)y + Map.vOffset;
    }

    public int trueX() {
        return (int)x;
    }

    public int trueY() {
        return (int)y;
    }

    public boolean isOnScreen() {
        return (screenX() > 0 && screenX() < Map.canvas.getWidth());
    }
}
