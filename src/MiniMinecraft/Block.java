
package MiniMinecraft;

public class Block {
    public static final int DIRT = 0, STONE = 1, GRASS = 2, BEDROCK = 3,
           WOOL = 4, PLAYER_HEAD = 5;
    public final int blockID, x, y;

    public Block(int blockID, int x, int y) {
        this.blockID = blockID;
        this.x = x;
        this.y = y;
    }

    public boolean obstructsActor() {
        double diffX, diffY;
        // looks through each entity in our entity list(zombies, etc.)
        for(Entity e : Map.entityList) {
            if(!(e instanceof Actor)) {
                continue;
            }
            diffX = x - e.x;
            diffY = y - e.y;
            // checks for a collison between the block and the entity
            if(-Map.BSIZE < diffX && diffX <= e.getWidth() * Map.BSIZE
                    && Map.BSIZE * e.getHeight() >= diffY && diffY >= -Map.BSIZE ) {
                return true;
            }
        }

        // this checks if the PLAYER is going to be obstructed by our new block.
        diffX = x - Map.player.trueX();
        diffY = y - Map.player.trueY();
        return (-Map.BSIZE < diffX && diffX <= Map.player.getWidth() * Map.BSIZE
                && Map.BSIZE * Map.player.getHeight() >= diffY && diffY >= -Map.BSIZE );

    }

    public int screenX() {
        return x + Map.hOffset;
    }

    public int screenY() {
        return y + Map.vOffset;
    }
}
