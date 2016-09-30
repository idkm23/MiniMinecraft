
package MiniMinecraft;

public class BloodEntity extends Entity {
    private int timer = 2000; // the bloodEntity's limited age

    public BloodEntity(Actor source) {
        // generates a small blood entity at the source actor
        super(source.trueX() + source.getWidth() * Map.BSIZE/2,
            source.trueY() + source.getHeight() * Map.BSIZE/2,
            .1, .1, BLOOD);

        // generates random vertical and horizontal speed for the particle
        vSpeed = Math.random() * 7 + 8;
        hSpeed = Math.random() * 8 - 4;
    }

    @Override
    public void update() {
        super.update();
        // if the blood entity's time is up, remove it
        if((timer -= 50) <= 0) {
            Map.entityList.remove(this);
        }
    }
}
