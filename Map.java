
package MiniMinecraft;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.applet.AudioClip;
import java.io.FileNotFoundException;

public class Map {
    public     static   final   int              BSIZE = 40, FULL_HEALTH_IMG = 30, EMPTY_HEALTH_IMG = 31, INVENTORY_BAR = 32, MOON = 35, 
                                                 THEME_S = 0, BREAK_S = 1, JUMP = 0, LEFT = 1, RIGHT = 2;
    
    protected  static   final   boolean          isPressed[] = new boolean[3];
    protected  static   final   ArrayList<Block> blockMap = new ArrayList();
    protected  static   final   ArrayList<Entity>entityList = new ArrayList();
    protected  static   final   ArrayList<Cloud> prettyClouds = new ArrayList();
    protected  static   final   GameScreen       canvas = new GameScreen();
    protected  static           int              hOffset, vOffset, lExplored, rExplored;
    private    static           double           timeOfDay = 0;
    private    static           Point            mouse;
    protected  static   final   Player           player = new Player();
    protected  static   final   AudioClip[]      sounds = new AudioClip[10];
    protected  static           MainApp          mainApp;
    
    
    //Heart of the game, updates everything every 50ms
    private    static   final   Timer            gameUpdater =
        new Timer(50, new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(player.isDead() && player.getRespawnTimer() != 0) 
                    player.attemptRespawn();
                
                readInput();
                tickTime();
                updateExplored();
                updateClouds();
                updateEntities();
                canvas.repaint();
            }
        });
    
    public Map(MainApp mainApp) {
        Map.mainApp = mainApp;
        
        //MouseWheelListener controls the selection on the item bar
        canvas.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent mwe) {
                
                if(mwe.getWheelRotation() < 0) 
                    player.pInven.scrollLeft();
                else 
                    player.pInven.scrollRight();
                
            }
            
        });
        
        //updates our mouse position variable for the selection box
        canvas.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent me) {
                mouse = me.getPoint();
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                mouse = me.getPoint();
            }
        });
        
        //Mouse listener for left click
        //Possiblities:
        //  Click back button to return to menu which will save the game
        //  Break block
        //  Damage entity
        canvas.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(MouseEvent me) {
                
                if(me.getButton() == MouseEvent.BUTTON1 && canvas.onBackButton(mouse)) {
                    unMountGameState();
                    
                } else if(!player.isDead()) { //unallows the player to attack/break block if dead
                    
                    if(me.getButton() == MouseEvent.BUTTON1) {
                        
                        if(!breakBlockOnMouse()) //if there is no block to break
                            damageEntityNearCursor(); //attempt to damage entity under mouse
                        
                    } else if(me.getButton() == MouseEvent.BUTTON3) {
                        placeBlockOnMouse();
                    }
                    
                }
            }
            
            public void mouseClicked(MouseEvent me) {}
            public void mouseReleased(MouseEvent me) {}
            public void mouseEntered(MouseEvent me) {}
            public void mouseExited(MouseEvent me) {}
            
        });
        
        //Keylistener for left and right movement as well as jump
        canvas.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if(!player.isDead()) //unallows movement when the player is dead
                    switch(e.getKeyCode()) {
                        
                        //Movement is controlled by the boolean array isPressed which is read from in another method
                        //because this enables the user to use more than one key at once
                        //setOrientation sets the direction the image is facing
                        
                        case KeyEvent.VK_A:
                        case KeyEvent.VK_LEFT:
                            player.setOrientation(Entity.LEFT); 
                            isPressed[LEFT] = true;
                            break;        
                            
                        case KeyEvent.VK_SPACE:
                        case KeyEvent.VK_UP:
                            isPressed[JUMP] = true;
                            break;
                            
                        case KeyEvent.VK_D:
                        case KeyEvent.VK_RIGHT:
                            player.setOrientation(Entity.RIGHT);
                            isPressed[RIGHT] = true;
                    }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch(e.getKeyCode()) {
                    
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        isPressed[LEFT] = false;
                        break;        
                        
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_UP:
                        isPressed[JUMP] = false;
                        break;
                        
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        isPressed[RIGHT] = false;
                }
            }
            
            @Override
            public void keyTyped(KeyEvent e) { }

        });
    }
    
    //First attempts to read a map file, if the map file doesn't exist an exception is thrown
    //Then the FileNotFoundException is caught and a new world is generated
    public static void mountGameState() {
        
        try {
            MapReader.readMap();
        } catch(FileNotFoundException ex) {
            genNewWorld();
        }
        gameUpdater.start();
        
        canvas.setFocusable(true); //important to get focus for input
        canvas.requestFocus();
        
    }
    
    //Generates the first chunk of blocks for a new world
    public static void genNewWorld() {
        for(int i = 0; i < mainApp.getWidth(); i += BSIZE) 
            generateCol(i);
    }

    //Switches to the menu, stops the game engine, and saves its state to the correct text file
    public static void unMountGameState() {
        gameUpdater.stop();
        MapReader.saveMap();
        resetVariables();
        mainApp.switchToMenu();
    }
    
    //Necessary for switching from map to map
    public static void resetVariables() {
        entityList.clear();
        blockMap.clear();
        prettyClouds.clear();
        player.clear();
        timeOfDay = 0;
        hOffset = 0;
        vOffset = 0;
        lExplored = 0;
        rExplored = 0;
        for(int i = 0; i < isPressed.length; i++) 
            isPressed[i] = false;
    }
    
    //Calculates the point where the selection box needs to be drawn
    public static Point selectionBox() {
        
        int x = (int)((mouse.x - (hOffset % BSIZE))/BSIZE) * BSIZE + (hOffset % BSIZE),  //This calculation determines where to draw the box that
            y = (int)((mouse.y - (vOffset % BSIZE))/BSIZE) * BSIZE + (vOffset % BSIZE); //will snap to the grid
        
        //this solves for the special case when the selection box is on the most left side of the screen,
        //the rectangle must begin to draw the rectangle on a negative coordinate
        if(mouse.x <= hOffset % BSIZE)
            x -= BSIZE;
        return new Point(x, y);
    }
    
    public static boolean breakBlockOnMouse() {
        Point selected = selectionBox();
        Block temp;
        
        for(int i = 0; i < blockMap.size(); i++) {
            
            temp = blockMap.get(i);
            
            //If a block is found at the x and y of the mouse AND it is not BEDROCK(the bottom layer) then...
            if(temp.screenX() == selected.x && temp.screenY() == selected.y && temp.blockID != Block.BEDROCK) {
                blockMap.remove(i);
                entityList.add(new ItemEntity(temp.x, temp.y, temp.blockID)); //create an item that can be picked up for it
                sounds[BREAK_S].stop();
                sounds[BREAK_S].play(); //play breaking sound
                return true; //returns a boolean to inform the caller if the algorithm broke a block
            }
            
        }
        
        return false;
    }
    
    public static boolean placeBlockOnMouse() {
        Point selected = selectionBox();
        boolean exists = false;
        
        //Look for any blocks that may already be where the user is clicking
        for(Block e : blockMap) 
            if(e.screenX() == selected.x && e.screenY() == selected.y) 
                exists = true;
        
        //if there were no blocks found then...
        if(!exists) {
            
            Item item = player.pInven.getSelectedItem();
            
            //if the player has an item to place then...
            if(item != null) {
                
                Block temp = new Block(item.itemID, selected.x - hOffset, selected.y - vOffset);
                
                //if nobody is in the way of the block then...
                if(!temp.obstructsActor()) {
                    player.pInven.removeOneSelectedItem(); //remove from the inventory and add it to the blockList
                    blockMap.add(temp);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static void damageEntityNearCursor() {
        //if the click is farther than 2 and a half blocks, the player cannot damage an entity
        if(Math.sqrt( Math.pow(mouse.x - (player.x + player.getWidth() * BSIZE),
                2) + Math.pow(mouse.y - (player.y + player.getHeight() * BSIZE), 2) ) > 2.5 * BSIZE)
            return;
            
        Entity e = null;
        boolean foundMatch = false;
        int mx = mouse.x - hOffset;
        int my = mouse.y - vOffset;
        
        //We search through our entities to find an entity in the area of the click
        for(int i = 0; i < entityList.size() && !foundMatch; i++) {
            e = entityList.get(i);
            if(e instanceof Actor && mx > e.x && mx < e.x + BSIZE*e.getWidth()  //the entity to attack MUST be an actor
                    && my > e.y && my < e.y + BSIZE*e.getHeight())              //otherwise you could damage things like blood and items
                foundMatch = true;
        }
        if(foundMatch)
            player.swingAt((Actor)e); //damages the target entity with the player's attack if the player hasn't attacked recently
    }
    
    //a full day is 100 so when time is incremented the result can only be less than or equal to 100
    private static void tickTime() {
        if((timeOfDay += .05) > 100)
            timeOfDay = 0;
    }    
    
    //As the player explores to new areas, new terrain must be generated
    private static void updateExplored() {
        
        //Solves for the right and left most columns currently on the screen
        //if the farthest column on either side of the screen is the farthest the player has ever explored
        //that column becomes the farthest column out and a column of terrain is generated there
        
        int rOuterCol = ((canvas.getWidth() - hOffset)/Map.BSIZE) * Map.BSIZE;

        if(rOuterCol > rExplored) {
            rExplored = rOuterCol;
            generateCol(rExplored);
        }

        int lOuterCol = (-(hOffset + Map.BSIZE) / Map.BSIZE) * Map.BSIZE;

        if(lOuterCol < lExplored) {
            lExplored = lOuterCol;
            generateCol(lExplored);
        }

    }
    
    private static void generateCol(int xCoord) {
        int layer;
        for(int y = 0; y <= 17; y++) { //each column that is generated has a height of 17 blocks
            
            layer = Map.layerLookup(y); //gets the correct block to place, on top there's grass and on the bottom there's bedrock etc...
            
            //After getting the layer, I use a trigonmetric function to create an osciliating curve to the terrain I generate
            blockMap.add(new Block(layer, xCoord, (int)(4*Math.sin(xCoord/360.0)) * BSIZE + (y + 10) * BSIZE));
            
        }
    }

        
    //Helpful for generating new terrain
    public static int layerLookup(int x) {
        
        if(x == 0)
            return Block.GRASS;
        else if(x < 6)
            return Block.DIRT;
        else if(x == 17)
            return Block.BEDROCK;
        else
            return Block.STONE;
        
    }
    
    private static void updateClouds() {
        
        //as long as there is less than 10 clouds, we generate clouds at random times
        if(prettyClouds.size() < 10 && (int)(Math.random() * 100) == 0) 
            prettyClouds.add(new Cloud());
        
        //moves each cloud
        Cloud c;
        for(int i = 0; i < prettyClouds.size(); i++) {
            c = prettyClouds.get(i);
            if(c.move()) { //if move returns false(its off the screen)
                prettyClouds.remove(i);
                i--;
            }
        }
    }
    
    private static void updateEntities() {
        genRandomActors();
        
        player.update();
            
        //updates all entites in the list (for things like gravity)
        Entity e;
        for(int i = 0; i < entityList.size(); i++) {
            e = entityList.get(i);
            e.update();
            
            //if the updated entity is an actor and dead, remove it from the game
            if((e instanceof Actor) && ((Actor)e).isDead() ) {
                entityList.remove(e);
                i--;
                ((Actor)e).generateBlood();
                entityList.add(((Actor)e).getItemOnDeath());
            }
        }
    }
    
    
    //Attempts to generate random actors
    //based on the time, only certain actors can spawn
    private static void genRandomActors() {
        
        if(isNight() && (int)(Math.random() * 400) == 0) 
            entityList.add(new Zombie());
        
        if(isDay() && (int)(Math.random() * 350) == 0)
            entityList.add(new Sheep());
        
    }
    
    //reads from the isPressed boolean array which is set by key presses and key release
    private static void readInput() {
        
        //if the player isn't obstructed, move left or right
        if(isPressed[LEFT] && !player.isObstructed(
            player.x - hOffset - Entity.BASE_MOVE_SPEED, player.y - vOffset - 1))
                hOffset += Entity.BASE_MOVE_SPEED;

        if(isPressed[RIGHT] && !player.isObstructed(player.x - hOffset + Entity.BASE_MOVE_SPEED,
                player.y - vOffset - 1))
                    hOffset -= Entity.BASE_MOVE_SPEED;

        //if the player IS obstructed (planted on the ground) the player can jump
        if(isPressed[JUMP]) {
            player.jump();
            if(!player.isObstructed(player.trueX(), player.trueY()))
                vOffset += 3;
        }

        //this is necessary to ensure the character will always face the correct direction
        if(isPressed[LEFT] && !isPressed[RIGHT])
            player.setOrientation(Entity.LEFT);
        else if(!isPressed[LEFT] && isPressed[RIGHT])
            player.setOrientation(Entity.RIGHT);
    }
    
    //timeOfDay is incharge of zombie/sheep spawning, sun/moon, and overall brightness
    public static boolean isDay() {
        return timeOfDay < 45;
    }
    
    public static boolean isNight() {
        return timeOfDay > 50 && timeOfDay < 95;
    }
    
    public static Point getMousePos() {
        return mouse;
    }
    
    public static void setTime(double time) {
        if(time < 100 && time > 0) //time must be within a valid range
            timeOfDay = time;
    }
    
    public static double getTime() {
        return timeOfDay;
    }
    
    public static Point getMouse() {
        return mouse;
    }
}

