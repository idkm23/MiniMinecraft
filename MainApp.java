//Program: MiniMinecraft
//By: Christopher Munroe
//Time spent: too much

//My program is inspired by a game I play called Minecraft. It has a similar concept, except it's 3D. The player can
//build, explore, and kill zombies or sheep. There is no real objective to win the game. My favorite part of this program was
//developing the collison detection for a constantly changing environment. Also, I always love finding ways to involve math in my programs.
//Within this program, there was trigonometry for the land generation and for the movement of the sun/moon. 
//I used a small amount of basic physics for my simulation of gravity. I enjoyed writing this program.


package MiniMinecraft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainApp extends JApplet {
    private static Map currentMap;
    private static final CardLayout cl = new CardLayout();
    
    private static final JPanel menu = new JPanel(new BorderLayout());
    private static final JButton quit = new JButton("Quit");
    private final JLabel  h1, h2;
    private final JPanel  buttons      = new JPanel(new GridLayout(2, 2, 20, 50)),
                          headPanel    = new JPanel(new GridLayout(2, 1));
    private final JButton mapChooser[] = new JButton[4];
    private final JLabel  mapLabels[]  = new JLabel[mapChooser.length];
    private static String currentMapName;
    
    public MainApp() {
        getContentPane().setLayout(cl);
        
        JPanel temp = new JPanel();
        temp.add(h1 = new JLabel("MiniMinecraft"));
        h1.setFont(new Font("Monospaced", Font.BOLD, 30));
        headPanel.add(temp);
        
        temp = new JPanel();
        temp.add(h2 = new JLabel("Main Menu"));
        h2.setFont(new Font("Monospaced", Font.BOLD, 20));
        headPanel.add(temp);
        
        menu.add(headPanel, BorderLayout.NORTH);
        
        for(int i = 0; i < mapChooser.length; i++) {
            temp = new JPanel();
            temp.add(mapChooser[i] = new JButton("Map " + (i+1)));
            buttons.add(temp);
        }
        
        temp = new JPanel();
        temp.add(buttons);
        menu.add(temp, BorderLayout.CENTER);
        
        temp = new JPanel();
        temp.add(quit);
        menu.add(temp, BorderLayout.SOUTH);
        
        add(menu);
        
        currentMap = new Map(this);
        add(Map.canvas);
        
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                currentMapName = ((JButton)ae.getSource()).getText();
                startGamePanel();
            }
        };
        
        for (JButton mapChoose : mapChooser) 
            mapChoose.addActionListener(al);
        
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });
        
    }
    
    @Override
    public void init() {
        setSize(300, 300);
        Frame c = (Frame)this.getParent().getParent();
        c.setTitle("MiniMinecraft");
        c.setResizable(false);
        loadRes(); //loads our images
    }
    
    //brings the game panel to the front of the cardLayout
    //and starts the game
    public void startGamePanel() {
        setSize(1000, 700);
        cl.next(getContentPane());
        Map.mountGameState();
    }
    
    //Brings the menu panel to the front of the CardLayout
    public void switchToMenu() {
        setSize(300, 300);
        cl.next(getContentPane());
        currentMapName = null;
    }
    
    //Unmounts gamestate for saving before the applet is closed 
    //incase the user exits by closing the applet 
    @Override
    public void destroy() {
        if(currentMapName != null)
            Map.unMountGameState();
    }
    
    public static String getCurrentMap() {
        return currentMapName;
    }
    
    //Loads all images and sounds
    private void loadRes() {
        GameScreen.images[Entity.PLAYER]       = new ImageIcon(getClass().getResource("/res/person.png"));
        GameScreen.images[Entity.HURT_PLAYER]  = new ImageIcon(getClass().getResource("/res/hurt_person.png"));
        GameScreen.images[Entity.HLESS_PLAYER] = new ImageIcon(getClass().getResource("/res/headless_person.png"));
        GameScreen.images[Entity.ZOMBIE]       = new ImageIcon(getClass().getResource("/res/zombie.png"));
        GameScreen.images[Entity.HURT_ZOMB]    = new ImageIcon(getClass().getResource("/res/hurt_zombie.png"));
        GameScreen.images[Entity.BURNING_ZOMB] = new ImageIcon(getClass().getResource("/res/burning_zombie.png"));
        GameScreen.images[Entity.BLOOD]        = new ImageIcon(getClass().getResource("/res/blood.png"));
        GameScreen.images[Entity.SHEEP]        = new ImageIcon(getClass().getResource("/res/sheep.png"));
        GameScreen.images[Entity.HURT_SHEEP]   = new ImageIcon(getClass().getResource("/res/hurt_sheep.png"));
        
        GameScreen.images[Block.DIRT]          = new ImageIcon(getClass().getResource("/res/dirt.png"));
        GameScreen.images[Block.STONE]         = new ImageIcon(getClass().getResource("/res/stone.png"));
        GameScreen.images[Block.GRASS]         = new ImageIcon(getClass().getResource("/res/grass.png"));
        GameScreen.images[Block.BEDROCK]       = new ImageIcon(getClass().getResource("/res/bedrock.png"));
        GameScreen.images[Block.WOOL]          = new ImageIcon(getClass().getResource("/res/wool.png"));
        GameScreen.images[Block.PLAYER_HEAD]   = new ImageIcon(getClass().getResource("/res/player_head.png"));
        
        GameScreen.images[Map.FULL_HEALTH_IMG] = new ImageIcon(getClass().getResource("/res/fullCan.png"));
        GameScreen.images[Map.EMPTY_HEALTH_IMG]= new ImageIcon(getClass().getResource("/res/emptyCan.png"));
        GameScreen.images[Map.INVENTORY_BAR]   = new ImageIcon(getClass().getResource("/res/inventoryBar.png"));
        GameScreen.images[Cloud.CLOUD1]        = new ImageIcon(getClass().getResource("/res/cloud1.png"));
        GameScreen.images[Cloud.CLOUD2]        = new ImageIcon(getClass().getResource("/res/cloud2.png"));
        GameScreen.images[Map.MOON]            = new ImageIcon(getClass().getResource("/res/moon.png"));

        Map.sounds[Map.THEME_S]                = getAudioClip(getClass().getResource("/res/menusong1.wav"));
        Map.sounds[Map.BREAK_S]                = getAudioClip(getClass().getResource("/res/break.wav"));
        Map.sounds[Player.P_DAMAGE1]           = getAudioClip(getClass().getResource("/res/playerDamage1.wav"));
        Map.sounds[Player.Z_DAMAGE1]           = getAudioClip(getClass().getResource("/res/zombieDamage1.wav"));
        Map.sounds[Player.SHEEP_BAA]           = getAudioClip(getClass().getResource("/res/sheepNoise1.wav"));
    }
}
