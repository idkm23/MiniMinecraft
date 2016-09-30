
package MiniMinecraft;

import java.io.*;

public class MapReader {
    
    public static final char BLOCKS = 'B', ITEMS = 'I', MAP_INFO = 'M';
    
    public static void saveMap() {
        try {
            PrintWriter writer = new PrintWriter( MainApp.getCurrentMap() + ".txt", "UTF-8");
            
            //writes the character flag
            //and then writes data for that type of flag
            
            //Blocks
            writer.println("B "); //char flag
            for(Block b : Map.blockMap) //block data
                writer.println(b.blockID + " " + b.x + " " + b.y);
            
            //Inventory
            writer.println("I "); //char flag
            for(Item i : Map.player.pInven.getInventory()) //item data
                writer.print( (i == null ? "?" : i.itemID + " " + i.quantity) + "|");
            writer.println();
            
            //Map info
            writer.println("M "); //char flag
            writer.println(Map.hOffset + " " + Map.vOffset + " " + Map.lExplored //map data
                    + " " + Map.rExplored + " " + Map.getTime() + " " + Map.player.getHealth() + " " + Map.player.vSpeed);
            
            writer.close();
        } catch( UnsupportedEncodingException | FileNotFoundException e) {
            System.out.print(e);
        }
    }
    
    public static void readMap() throws FileNotFoundException {
        try{

            FileReader inputFile = new FileReader( MainApp.getCurrentMap() + ".txt");
            BufferedReader bufferReader = new BufferedReader(inputFile);

            //Variable to hold the one line data
            String line;
            char c = ' ';
            // Read file line by line
            while ((line = bufferReader.readLine()) != null)   {
                
                //If a char flag is hit, store it and skip the current iteration
                if(isCharFlag(line.charAt(0))) {
                    c = line.charAt(0);
                    continue;
                }
                    
                switch(c) {
                    case BLOCKS:
                        addBlockString(line);
                        break;
                    case ITEMS:
                        addItemString(line);
                        break;
                    case MAP_INFO:
                        configureMapString(line);
                }
            }
            
            bufferReader.close();
            
        } catch(IOException e) {
            
            //this is thrown if there is no file that has been created yet
            //this exception is rethrown and caught in Map to generate a new world
            if(e instanceof FileNotFoundException)
                throw (FileNotFoundException)e;
            
            System.err.println(e);
        }

    }
    
    
    //Reads in a string of three integers seperated by one space
    //the for loop will seperate the numbers into the string array sProperty
    //a block is then added with the three attributes read into the sProperty array
    public static void addBlockString(String b) {
        String[] sProperty = {"", "", ""};
        int currentProp = 0;
        
        for(int i = 0; i < b.length(); i++) {
            
            if(b.charAt(i) == ' ') {
                currentProp++;
            } else {
                sProperty[currentProp] += b.charAt(i);
            }
            
        }
        
        Map.blockMap.add(new Block(Integer.parseInt(sProperty[0]),
                Integer.parseInt(sProperty[1]), Integer.parseInt(sProperty[2]) ));
    }
    
    //Takes in a line representing the player's inventory (something like this: ?|?|?|?|?|?|1 2|
    //A question mark means there is no item in that inventory slot
    //if it hits numbers then it takes in the number so it can create an item the next time it hits '|'
    public static void addItemString(String i) {
        String[] itemProperties = {"", ""};
        int itemSlot = 0;
        int currentProp = 0;
        
        for(int j = 0; j < i.length(); j++) {
            
            switch( i.charAt(j) ) {
            
                case '|':
                    Map.player.pInven.addItem(new Item(Integer.parseInt(itemProperties[0]), Integer.parseInt(itemProperties[1])) );

                    currentProp = 0;
                    itemSlot++;
                    itemProperties[0] = "";
                    itemProperties[1] = "";
                    break; 
                    
                case '?': 
                    j++;
                    break;
                    
                case ' ':
                    currentProp++;
                    break;
                
                default:
                    itemProperties[currentProp] += i.charAt(j);
                
            }
        }
    }
    
    //The map line is somewhat unorganized. It holds 7 variables that are important when saving the game state
    //Things like location of the player and the player's speed
    //the process is similiar to the other two methods
    public static void configureMapString(String m) {
        String[] itemProperties = {"", "", "", "", "", "", ""};
        int currentProp = 0;
        
        for(int i = 0; i < m.length(); i++) {
            if(m.charAt(i) == ' ') {
                currentProp++;
            } else {
                itemProperties[currentProp] += m.charAt(i);
            }
        }
        
        Map.hOffset = Integer.parseInt(itemProperties[0]);
        Map.vOffset = Integer.parseInt(itemProperties[1]);
        Map.lExplored = Integer.parseInt(itemProperties[2]);
        Map.rExplored = Integer.parseInt(itemProperties[3]);
        Map.setTime(Double.parseDouble(itemProperties[4]));
        Map.player.setHealth(Integer.parseInt(itemProperties[5]));
        Map.player.vSpeed = Double.parseDouble(itemProperties[6]);
        
    }
    
    public static boolean isCharFlag(char c) {
        return c == 'B' || c == 'I' || c == 'M';
    }
}
