package krevik.github.io.util.NPCUtils;

import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.entity.EntityNPC;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;

public class NPCUtils {
    public boolean isFreeSlotInInventory(Inventory inv){
        boolean result=false;
        for(int x=0;x<inv.getSizeInventory();x++){
            if(inv.getStackInSlot(x).isEmpty()){
                result=true;
                break;
            }
        }
        return result;
    }

    public ArrayList<Item> convertInventoryToListOfItems(Inventory inv){
        ArrayList<Item> result = new ArrayList<>();

        if(inv!=null) {
            if (!inv.isEmpty()) {
                for (int c = 0; c < inv.getSizeInventory(); c++) {
                    if (inv.getStackInSlot(c) != null) {
                        if (!inv.getStackInSlot(c).isEmpty()) {
                            if (inv.getStackInSlot(c).getItem() != null) {
                                result.add(inv.getStackInSlot(c).getItem());
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public boolean isInInventory(Inventory inventory, Item item){
        boolean result=false;
        for(int c=0;c<=inventory.getSizeInventory();c++){
            if(!inventory.getStackInSlot(c).isEmpty()){
                if(inventory.getStackInSlot(c).getItem()==item){
                    result=true;
                    break;
                }
            }
        }
        return result;
    }

    public ArrayList<BlockPos> chestPosesWith(EntityNPC npc, Item itemToFind){
        ArrayList<BlockPos> result = new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getX();
        int yRadius = npc.getWorkingRadius().getY();
        int zRadius = npc.getWorkingRadius().getZ();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck = new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getTileEntity(toCheck)!=null){
                        if(world.getTileEntity(toCheck) instanceof ChestTileEntity){
                            ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(toCheck);
                            innerloop: for(int c=0;c<chest.getSizeInventory();c++){
                                if(chest.getStackInSlot(c)!=null){
                                    if(!chest.getStackInSlot(c).isEmpty()){
                                        if(chest.getStackInSlot(c).getItem()!=null){
                                            if(chest.getStackInSlot(c).getItem()==itemToFind){
                                                result.add(toCheck);
                                                break innerloop;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public ArrayList<ItemWithInventoryIndexEntry> getItemsWithIndexesInInventory(Inventory localInventory, Item item){
        ArrayList<ItemWithInventoryIndexEntry> result=new ArrayList<>();
        for(int c=0;c<localInventory.getSizeInventory();c++){
            if(localInventory.getStackInSlot(c)!=null){
                if(!localInventory.getStackInSlot(c).isEmpty()){
                    if(localInventory.getStackInSlot(c).getItem()!=null){
                        if(localInventory.getStackInSlot(c).getItem() == item){
                            ItemWithInventoryIndexEntry entry = new ItemWithInventoryIndexEntry(localInventory.getStackInSlot(c).getItem(),c);
                            result.add(entry);
                        }
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<BlockPos> getChestPosesWithFreeSlots(EntityNPC npc){
        ArrayList<BlockPos> result = new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getX();
        int yRadius = npc.getWorkingRadius().getY();
        int zRadius = npc.getWorkingRadius().getZ();
        ArrayList<BlockPos> allChests = new ArrayList<>();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck = new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getTileEntity(toCheck)!=null){
                        if(world.getTileEntity(toCheck) instanceof ChestTileEntity){
                            allChests.add(toCheck);
                        }
                    }
                }
            }
        }

        if(!allChests.isEmpty()){
            for(BlockPos toCheck:allChests){
                ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(toCheck);
                for(int c=0;c<=15;c++){
                    if(chest.getStackInSlot(c).isEmpty()){
                        result.add(toCheck);
                        break;
                    }
                }
            }
        }

        return result;
    }

    public BlockPos getNearestChestPos(EntityNPC npc, ArrayList<BlockPos> chestPoses){
        BlockPos result = chestPoses.get(0);
        for(BlockPos toCheck:chestPoses){
            Vec3i farmerPos = new Vec3i(Math.abs(npc.getPosition().getX()),Math.abs(npc.getPosition().getY()),Math.abs(npc.getPosition().getZ()));
            Vec3i toCheckPos = new Vec3i(Math.abs(toCheck.getX()),Math.abs(toCheck.getY()),Math.abs(toCheck.getZ()));
            Vec3i resultPos = new Vec3i(Math.abs(result.getX()),Math.abs(result.getY()),Math.abs(result.getZ()));
            if(farmerPos.distanceSq(toCheckPos)<farmerPos.distanceSq(resultPos)) {
                result = toCheck;
            }
        }
        return result;
    }

    public ArrayList<ItemWithInventoryIndexEntry> getItemsWithInventoryIndexes(EntityAutoFarmer npc, Item item){
        ArrayList<ItemWithInventoryIndexEntry> result = new ArrayList<>();
        for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
            if(npc.getLocalInventory().getStackInSlot(c)!=null){
                if(!npc.getLocalInventory().isEmpty()){
                    if(npc.getLocalInventory().getStackInSlot(c).getItem()!=null){
                        if(npc.getLocalInventory().getStackInSlot(c).getItem()==item){
                            ItemWithInventoryIndexEntry itemEntry = new ItemWithInventoryIndexEntry(item,c);
                            result.add(itemEntry);
                        }
                    }
                }
            }
        }
        return result;
    }
}
