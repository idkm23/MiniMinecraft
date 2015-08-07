
package MiniMinecraft;

public class Sheep extends Actor {
    
    //Essential for the sheep's ability to roam
    private int stepsToWalk;
    
    public Sheep() {
        super(Math.random() * 300 + (int)(Math.random() * 2) * 700 - Map.hOffset, //calculates a random x for the zombie on the screen from 0 - 300 or 700 - 1000
            100, 4, 0, Block.WOOL, 1.8, .8, SHEEP, HURT_SHEEP, SHEEP_BAA);
    }
    
    @Override
    public void jump() {
        if(isObstructed(x, y)) //if he has room to jump, jump
            vSpeed = 15;
    }
    
    @Override
    public void update() {
        super.update();
        updateSteps(); //roaming
        
        //baa if 
        if(isOnScreen() && (int)(Math.random() * 400) == 0)
        {
            Map.sounds[SHEEP_BAA].stop();    
            Map.sounds[SHEEP_BAA].setFramePosition(0);    
            Map.sounds[SHEEP_BAA].start();
        }
        
        //move one step, left or right, based on stepsToWalk
        int horzMove = Entity.BASE_MOVE_SPEED/2 * ( stepsToWalk > 0 ? -1 : 1 );
        moveAIHorz(horzMove);
        
        imageID = (getHurtCool() <= 0) ? SHEEP : HURT_SHEEP;
    }
    
    //tick one step away from the steps
    //and if the steps reach 0, roll for a new amount of steps in a particular direction
    public void updateSteps() {
        stepsToWalk += (stepsToWalk > 0 ? -1 : 1 );
        
        if(stepsToWalk == 0)
            stepsToWalk = (int) (Math.random() * 40) - 20;
    }
}
