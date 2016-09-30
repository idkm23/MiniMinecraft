
package MiniMinecraft;

public class PlayerInventory {
    private final Item[] inventory = new Item[8];
    private int selectedIndex;
    
    //removes one item from the SELECTED item in the player's inventory bar
    //used when placing a block
    public Item removeOneSelectedItem() {
        Item toReturn = null;
        if(inventory[selectedIndex] != null) { //if there is an item in the slot...
            toReturn = inventory[selectedIndex];
            if(--inventory[selectedIndex].quantity <= 0) //sets the slot to null if the item's resulting quantity is 0
                 inventory[selectedIndex] = null;   
        }
            
        return toReturn;
    }
    
    //Looks through the inventory for any slot that already has the item. 
    //If the item already exists, add 1 to the quantity of the item in that slot.
    //otherwise just add it to the first slot available
    //used when gathering items
    public boolean addItem(Item item) {
            
        Item temp;
        for(int i = 0; i < inventory.length; i++) {
            temp = inventory[i];
            if(temp == null) {
                inventory[i] = item;
                return true;
            }
            else if(temp.itemID == item.itemID && temp.quantity < 100) {
                temp.quantity++;
                return true;
            }
            
        }
        return false;
    }
    
    //used when dismounting the gamestate (exiting the game)
    public void clear() {
        selectedIndex = 0;    
        for(int i = 0; i < inventory.length; i++) 
            inventory[i] = null;
    }
    
    public void scrollRight() {
        if(++selectedIndex > inventory.length-1)
            selectedIndex = 0;
    }
    public void scrollLeft() {
        if(--selectedIndex < 0)
            selectedIndex = inventory.length-1;
    }
    
    public int getSelectedIndex() {
        return selectedIndex;
    }
    
    public Item getSelectedItem() {
        return inventory[selectedIndex];
    }
    
    public Item[] getInventory() {
        return inventory;
    }
}
