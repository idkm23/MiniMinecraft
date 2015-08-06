package MiniMinecraft;

import javax.swing.*;
import java.awt.*;

public class GameScreen extends JPanel {
    protected static ImageIcon[] images = new ImageIcon[50];
    private FontMetrics bButtonFM; //font metrics for the back button is stored to make an accurate hit box for the back button
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        setBackground(getSky()); //sky color changes based on the time
        
        drawMoonSun(g);
        drawClouds(g);
        drawPlayer(g);
        drawEntities(g);
        drawBlocks(g);
        drawUI(g);
        drawSelectionBox(g);
        drawNightTint(g); //makes the game appear to get darker when it's night
        
        if(Map.player.isDead()) 
            drawDeathOverlay(g);
            
        drawBackButton(g);
    }
    
    //button used to return to the menu
    private void drawBackButton(Graphics g) {
        
        //draws the back button with italics if the button is being hovered over
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", ( onBackButton(Map.getMousePos()) ?  Font.ITALIC: Font.PLAIN ), 20));
        
        bButtonFM = g.getFontMetrics();
        g.drawString("Return to main menu", getWidth() - bButtonFM.stringWidth("Return to main menu") - 20, bButtonFM.getAscent() + 20);
    }
    
    //determines if the mouse is in the hitbox of the back button
    public boolean onBackButton(Point mouse) {
        if(mouse == null)
            return false;
        
        return (mouse.y < bButtonFM.getAscent() + 20 && mouse.x > Map.canvas.getWidth() - bButtonFM.stringWidth("Return to main menu") - 20
                && mouse.y > 20 && mouse.x < Map.canvas.getWidth() - 20);
    }
    
    //draws two objects, the moon and sun, revolving on the screen based on the time of day
    private void drawMoonSun(Graphics g) {
        
        double theta = (Map.getTime()) / 100.0 * 2 * Math.PI;
        
        //Draws the circles with a radius of one block
        //the x and y are determined by the cos and sin
        //I think of it in a similiar to the way the unit circle works
        
        g.setColor(Color.YELLOW);
        g.fillArc((int)(getWidth()/2 - getWidth()/2 * Math.cos(theta)) - Map.BSIZE, 
                (int)(getHeight() - getHeight() * Math.sin(theta)) - Map.BSIZE, 
                Map.BSIZE * 2, Map.BSIZE * 2, 0, 360);

        g.drawImage(images[Map.MOON].getImage(), (int)(getWidth()/2 + getWidth()/2 * Math.cos(theta)) - Map.BSIZE, 
                (int)(getHeight() + getHeight() * Math.sin(theta)) - Map.BSIZE, 
                Map.BSIZE * 2, Map.BSIZE * 2, this);
        
    }
    
    private void drawClouds(Graphics g) {
        for(Cloud c : Map.prettyClouds) 
            g.drawImage(images[c.cloudID].getImage(), (int)c.getX() + Map.hOffset, (int)c.getY() + Map.vOffset,
                    images[c.cloudID].getIconWidth(), images[c.cloudID].getIconHeight(), this);
    }
    
    private void drawEntities(Graphics g) {
        for(Entity e : Map.entityList) 
            g.drawImage(images[e.imageID].getImage(), (int)(e.screenX() + (e.getOrientation() ? Map.BSIZE * e.getWidth() : 0)), (int)e.screenY(), 
                    (e.getOrientation() ? -1 : 1) * (int)(Map.BSIZE * e.getWidth()), (int)(Map.BSIZE * e.getHeight()), this); 
    }
    
    private void drawPlayer(Graphics g) {
        Map.player.x = (getWidth() - Map.BSIZE * Map.player.getWidth())/2;
        Map.player.y = (getHeight() - Map.BSIZE * Map.player.getHeight())/2;
        
        //draws the player, the ternary operators determine which way to flip the image
        //so if the character changes the direction he is walking then the image will flip
        g.drawImage(images[Map.player.imageID].getImage(), (int)(Map.player.x + (Map.player.getOrientation() ? Map.BSIZE * Map.player.getWidth() : 0)), //draws player
                (int)Map.player.y, (int) ((Map.player.getOrientation() ? -1 : 1) * Map.BSIZE * Map.player.getWidth()), (int)(Map.BSIZE * Map.player.getHeight()), this);

    }
    
    private void drawBlocks(Graphics g) {
        
        for(Block b : Map.blockMap) 
            g.drawImage(images[b.blockID].getImage(), b.screenX(),
                    b.screenY(), Map.BSIZE, Map.BSIZE, this);

    }
    
    //draws the health bar and inventory bar
    private void drawUI(Graphics g) {
        
        for(int i = 8; i >= 1; i--) 
            g.drawImage(images[(Map.player.getHealth() < i)? Map.EMPTY_HEALTH_IMG : Map.FULL_HEALTH_IMG].getImage(),
                    getWidth() - 35*i - 20, getHeight() - 50, 30, 30, this);

        g.drawImage(images[Map.INVENTORY_BAR].getImage(), 25, getHeight() - images[Map.INVENTORY_BAR].getIconHeight() * 3 - 10, 
                images[Map.INVENTORY_BAR].getIconWidth() * 3, images[Map.INVENTORY_BAR].getIconHeight() * 3, this);

        g.setColor(Color.WHITE);
        Item[] itemBar = Map.player.pInven.getInventory();

        
        //I don't like the code in this for loop
        //I made it very specific and the code looks ugly but I was pressed for time
        for(int i = 0; i < itemBar.length; i++) {

            //draws each item's picture in the box it is in the array
            if(itemBar[i] != null) {
                int x = 40 + (i * 54), y = getHeight() - 20 * 3 + 5;
                g.drawImage(images[itemBar[i].itemID].getImage(), x, y, 30, 30, this);
                g.drawString(itemBar[i].quantity + "", x - 2, y - 2);
            }

            //if the current item is the player's selected item, draw a small rectangle around that spot
            if(i == Map.player.pInven.getSelectedIndex()) {
                    g.setColor(new Color(230, 230, 230));
                    g.drawRect((images[Map.INVENTORY_BAR].getIconHeight() - 2) * 3 * i + 27, getHeight() - (images[Map.INVENTORY_BAR].getIconHeight() - 2) * 3 - 15, 
                            (images[Map.INVENTORY_BAR].getIconHeight() - 2) * 3, (images[Map.INVENTORY_BAR].getIconHeight() - 1) * 3);
                    g.setColor(Color.WHITE);
            }
        }
        
    }
    
    //draws the gray box around the block the mouse is on
    private void drawSelectionBox(Graphics g) {
        Color c = g.getColor();
        g.setColor(Color.GRAY);
        if(Map.getMouse() != null) { 
            Point box = Map.selectionBox();
            g.drawRect(box.x, box.y, Map.BSIZE, Map.BSIZE);           
        }
        g.setColor(c);
    }
    
    //draws a transparent black on the screen to emulate the darkness of night
    private void drawNightTint(Graphics g) {                        
                                                                        //this calculation here gets a value that ranges from 0 to 27 and back to 0
                                                                        //for times between 45 to 100
        g.setColor(new Color(0f, 0f, 0f, (float)(.6 * ( !Map.isDay() ? Math.abs(Math.abs(Map.getTime() - 45 - 27.5) - 27.5) / 27.5 : 0))));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    private void drawDeathOverlay(Graphics g) {
        //draws red tint
        g.setColor(new Color(1f, 0f, 0f, .4f));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Monospaced", Font.BOLD, 60));
        FontMetrics fm = g.getFontMetrics();

        //Draws "Dead" in the center of the screen with a shadow
        g.setColor(Color.BLACK);
        g.drawString("Dead", (getWidth() - fm.stringWidth("Dead"))/2 - 1, 199);
        g.setColor(Color.WHITE);
        g.drawString("Dead", (getWidth() - fm.stringWidth("Dead"))/2, 200);

        //draws the time until the player respawns
        g.setFont(new Font("Monospaced", Font.BOLD, 30));
        fm = g.getFontMetrics();
        g.setColor(Color.BLACK);
        g.drawString(String.format("%.2f", Map.player.getRespawnTimer()), (getWidth() - fm.stringWidth(String.format("%.2f", Map.player.getRespawnTimer())))/2 - 1, 209 + fm.getAscent());
        g.setColor(Color.WHITE);
        g.drawString(String.format("%.2f", Map.player.getRespawnTimer()), (getWidth() - fm.stringWidth(String.format("%.2f", Map.player.getRespawnTimer())))/2, 210 + fm.getAscent());
    }
    
    //creates a variance of blue based on the current time
    private Color getSky() {
        if(Map.getTime() < 45)
            return new Color(180, 200, 255);
        else if(Map.getTime() < 50)
            return new Color(180 - (int)((Map.getTime() - 45) * 17), 200 - (int)((Map.getTime() - 45) * 17), 255 - (int)((Map.getTime() - 45) * 17));
        else if(Map.getTime() < 95)
            return new Color(95, 115, 170);
        else
            return new Color(95 + (int)((Map.getTime() - 95) * 17), 115 + (int)((Map.getTime() - 95) * 17), 170 + (int)((Map.getTime() - 95) * 17));
    }
}
