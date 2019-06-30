package krevik.github.io.util;

import net.minecraft.item.Item;

public class ItemWithInventoryIndexEntry {

    private Item item;
    private int inventoryIndex;
    public ItemWithInventoryIndexEntry(Item seedItem, int index){
        item=seedItem;
        inventoryIndex=index;
    }

    public Item getItem(){
        return item;
    }

    public int getInventoryIndex(){
        return inventoryIndex;
    }
}
