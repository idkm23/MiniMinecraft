
package MiniMinecraft;

public class Item {
    protected final int itemID;
    protected int quantity = 1;
    
    public Item(int itemID) {
        this.itemID = itemID;
    }
    
    public Item(int itemID, int quantity) {
        this.itemID = itemID;
        this.quantity = quantity;
    }
}
