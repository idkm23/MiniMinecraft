
package MiniMinecraft;

public final class Player extends Actor {
    
    protected final PlayerInventory pInven = new PlayerInventory();
    private double respawnTimer; 
    
    public Player() {
        super(0, 0, 8, 1, Block.PLAYER_HEAD, .8, 1.8, Entity.PLAYER, HURT_PLAYER, P_DAMAGE1);
    }

    //Extremely similar to Entity's update the only difference is that it affects the vOffset and hOffset.
    //I wanted to optimize my code so Player used Entity's update but I ran out of time
    @Override
    public void update() {
        vSpeed += ACCEL;
        
        if(!isObstructed(trueX(), -vSpeed + trueY())) {
            Map.vOffset += vSpeed;
            if(Map.vOffset < -2000) 
                damage(1, (Math.random() > .5));
            
        } else {
            if(vSpeed <= -30) { //fall damage if the player has reached high speeds on impact
                damage( 1 - (int)(vSpeed + 30) / 6, (Math.random() > .5));
                vSpeed = 0;
            } else {
                vSpeed = 0;
            }
        }
        
        if(Math.abs(hSpeed) > 0) {
            hSpeed +=  .05 * ( hSpeed < 0 ? 1 : -1 );
            if(!isObstructed(trueX() + hSpeed, trueY()))
                Map.hOffset -= hSpeed;
            else {
                hSpeed = 0;
            }
            
        } else
                hSpeed = 0;
        
        //used for when the player dies/is hurt/is fine to change the characters image
        imageID = (isDead()) ? HLESS_PLAYER : (getHurtCool() <= 0) ? PLAYER : HURT_PLAYER;
        tickCoolDown();
        
        if(!isDead())
            gatherItems();
    }
    
    //Again, extremely similar to the Entity's isObstructed but there is a slight change where
    //the player is affected by the vOffset and hOffset (the screen's offset from the rest of the coordinates)
    @Override
    public boolean isObstructed(double x, double y) {
        double diffX, diffY;
        
        for(Block b : Map.blockMap) {
            diffX = b.x - x;
            diffY = b.y - y;
            if(-Map.BSIZE < diffX && diffX <= getWidth() * Map.BSIZE && Map.BSIZE * getHeight() >= diffY && diffY >= -Map.BSIZE ) {
                double moveComplete = (b.y - trueY()) - Map.BSIZE * getHeight();
                Map.vOffset -=  (moveComplete < .1)? 0 : moveComplete;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void jump() {
        if(isObstructed(trueX(), trueY()))
            vSpeed = 12;
    }
    
    public void gatherItems() {
        
        Entity e;
        double centerXBlock, centerXPlayer, yBlock, yPlayer, distance;
        
        for(int i = 0; i < Map.entityList.size(); i++) {
            
            e = Map.entityList.get(i);
            
            if(e instanceof ItemEntity) {
                
                centerXBlock = e.screenX() + e.getWidth() * Map.BSIZE / 2;
                centerXPlayer = Map.player.x + Map.player.getWidth() * Map.BSIZE / 2;
                yBlock = e.screenY() + e.getHeight() * Map.BSIZE;
                yPlayer = Map.player.y + Map.player.getHeight() * Map.BSIZE;
                
                distance = Math.sqrt(Math.pow(centerXBlock - centerXPlayer, 2) + Math.pow(yBlock - yPlayer, 2));
                
                //if the player is a block away from the item
                if(distance < Map.BSIZE) {
                    if(((ItemEntity)e).itemForm.itemID == Map.FULL_HEALTH_IMG) { //if the item is a health canister
                        Map.entityList.remove(e);
                        heal(1);
                        i--;
                        continue;
                    }
                        
                    if(Map.player.pInven.addItem(((ItemEntity)e).itemForm)) { //if the item is successfully added to the player
                        Map.entityList.remove(e);
                        i--;
                    }
                }
            }
        }
    }
    
    @Override
    public boolean isDead() {

        //if this is the first time we have checked since the player has died
        if(super.isDead() && respawnTimer == 0) {
            generateBlood();
            respawnTimer = 5;
            Map.entityList.add(getItemOnDeath());
        }
        
        return super.isDead();
    }
    
    public double getRespawnTimer() {
        return respawnTimer;
    }
    
    public void attemptRespawn() {
        
        //tick the timer, then, if we are ready to respawn, do it
        if((respawnTimer -= .05) < .1) {
            setHealth(MAX_HEALTH); //heal to full
            Map.hOffset = 0;
            Map.vOffset = 0;
            respawnTimer = 0;
        }
        
    }
    
    //used when dismounting the game state (exiting the game)
    public void clear() {
        respawnTimer = 0;
        setHealth(MAX_HEALTH);
        hSpeed = 0;
        vSpeed = 0;
        pInven.clear();
    }
    
    @Override
    public int trueX() {
        return (int)x - Map.hOffset;
    }
    @Override
    public int trueY() {
        return (int)y - Map.vOffset;
    }
    @Override
    public int screenX() {
        return (int)x;
    }
    @Override
    public int screenY() {
        return (int)y;
    }
}
