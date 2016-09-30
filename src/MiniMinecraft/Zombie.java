
package MiniMinecraft;

public class Zombie extends Actor {
    private int sunCoolDown = 0;
    public Zombie() {
        super( Math.random() * 300 + (int)(Math.random() * 2) * 700 - Map.hOffset, //calculates a random x for the zombie on the screen from 0 - 300 or 700 - 1000
                100, //spawns him in the air above the ground
                8,  1, Map.FULL_HEALTH_IMG, .8, 1.8, ZOMBIE, HURT_ZOMB, Z_DAMAGE1); //5 health, 1 attack
        
    }

    @Override
    public void jump() {
        if(isObstructed(x, y)) //if he has room to jump, jump
            vSpeed = 16;
    }
    
    @Override
    public void update() {
        super.update();
        
        //move towards the player at half the base movement speed
        int horzMove = Entity.BASE_MOVE_SPEED / 2 * (int)((Map.player.trueX() - x) / Math.abs(Map.player.trueX() - x));
             
        if(Math.abs(Map.player.trueX() - x) > Entity.BASE_MOVE_SPEED / 2) 
            moveAIHorz(horzMove);
        
        //if the zombie is one block away or less from the player, the zombie attempts to attack the player
        double distance = Math.sqrt( Math.pow(Map.player.trueX() - x, 2) + Math.pow(Map.player.trueY() - y, 2));
        if(distance < Map.BSIZE) 
            swingAt(Map.player);
                
        //incharge of the tick damage that happens when the sun burns the zombies
        if(sunCoolDown != 0)
            sunCoolDown--;
        else if(Map.isDay()) { //if the cooldown is ready and it's day, burn the zombie with one damage
            damage(1, (Math.random() > .5));
            sunCoolDown = 100; //then start the timer again
        }
        
        //sets up the correct image for the zombie
        if(Map.isDay())
            imageID = BURNING_ZOMB;
        else if(getHurtCool() > 0)
            imageID = HURT_ZOMB;
        else
            imageID = ZOMBIE;
    }
}
