
package MiniMinecraft;

public class ItemEntity extends Entity {
    protected final Item itemForm;
    
    public ItemEntity(int x, int y, int blockID) {
        super(x, y, .5, .5, blockID);
        itemForm = new Item(blockID);

        vSpeed = Math.random()*7 + 8;
        hSpeed = Math.random()*8 - 4;
    }

}
