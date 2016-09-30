package MiniMinecraft;

// something affected by gravity and has health/attack
public abstract class Actor extends Entity {
    private final double ATTKCOOL = .7;
    private double currentAttkCool, hurtCool;
    protected final int AUDIO_ID, HURT_ID, MAX_HEALTH;
    private int health;
    protected final int attack, itemOnDeath;
    
    public Actor(double x, double y, int health, int attack, int itemOnDeath,
            double width, double height, int imageID, int hurtID, int audioID) {
        super(x, y, width, height, imageID);
        AUDIO_ID = audioID;
        HURT_ID = hurtID;
        this.itemOnDeath = itemOnDeath;
        this.health = MAX_HEALTH = health;
        this.attack = attack;
    }
    
    public abstract void jump();
    
    @Override
    public void update() {
        super.update();
        tickCoolDown();
    }
    
    // this actor attempts to swing(attack) at another
    public void swingAt(Actor a) {
        // if it hasn't attacked recently and the target actor is alive then
        // the actor damages the other
        if(currentAttkCool <= 0 && !a.isDead()) {
            // reset attkCool so the actor cannot attack rapidly
            currentAttkCool = ATTKCOOL;
            a.damage(attack, (a.trueX() - trueX() > 0));
        }
    }
    
    public boolean isDead() {
        return health <= 0;
    }
    
    public void heal(int d) {
        if((health += d) > MAX_HEALTH) {
            health = MAX_HEALTH;
        }
    }
    
    public void tickCoolDown() {
        if(currentAttkCool > 0) {
            currentAttkCool -= .05;
        }
        if(hurtCool > 0) {
            hurtCool -= .05;
        }
    }
    
    // whenever an actor dies blood particles are generated.
    public void generateBlood() {
        int bloodDrops = (int)(Math.random() * 8) + 30;
        for(int j = 0; j < bloodDrops; j++)
            Map.entityList.add(new BloodEntity(this));
    }
    
    // damages the actor, knocks them away from the source, and changes the
    // actor's image red temporarily.
    public void damage(int d, boolean fromWhere) {
        health -= d;
        
        jump();
        hSpeed = (fromWhere ? 1 : -1 ) * 6; //knocks back
        
        startHurtCool();
        imageID = HURT_ID;
        
        // if the damaged actor is on screen, play the actor's sound
        if(isOnScreen()) {
            Map.sounds[AUDIO_ID].stop();
            Map.sounds[AUDIO_ID].setFramePosition(0);
            Map.sounds[AUDIO_ID].start();
        }
    }
    
    // used for moving AI actors.
    public void moveAIHorz(int stepSize) {
        if(stepSize > 0) {
            setOrientation(RIGHT);
        } else {
            setOrientation(LEFT);
        }

        // if the actor can move in that horizontal direction, move it
        // otherwise, have the actor attempt to jump over the obstacle.
        if(!isObstructed(x + stepSize, y - 1)) {
            x += stepSize;
        } else {
            jump();
        }
    }
    
    // prevents actors from getting hit rapidly
    public void startHurtCool() {
         hurtCool = .55;   
    }
    
    public double getHurtCool() {
        return hurtCool;
    }
    
    public ItemEntity getItemOnDeath() {
        return new ItemEntity(trueX(), trueY(), itemOnDeath);
    }
    
    public void setHealth(int h) {
        if(h > MAX_HEALTH) {
            health = MAX_HEALTH;
        } else {
            health = h;
        }
    }
    
    public int getHealth() {
        return health;
    }
}
