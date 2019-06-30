package krevik.github.io.util;

import krevik.github.io.entity.EntityAutoFarmer;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBoneMeal;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class FunctionHelper {

    //--------------------------------------------------------FARMER START----------------------------------------------------

    public boolean isFreeSlotInInventory(InventoryBasic inv){
        boolean is=false;
        for(int x=0;x<inv.getSizeInventory();x++){
            if(inv.getStackInSlot(x).isEmpty()){
                is=true;
            }
        }
        return is;
    }


    public boolean areAllSeedsInInventory(EntityAutoFarmer farmer){
        boolean areCarrotsHere=false;
        boolean areWheatSeedsHere=false;
        boolean arePotatoesHere=false;
        boolean areBeetrotsSeedsHere=false;
        for(int c=0;c<farmer.getLocalInventory().getSizeInventory();c++){
            if(!farmer.getLocalInventory().getStackInSlot(c).isEmpty()) {
                Item item = farmer.getLocalInventory().getStackInSlot(c).getItem();
                if(item==Items.CARROT) {areCarrotsHere=true;}
                if(item==Items.POTATO) {arePotatoesHere=true;}
                if(item==Items.WHEAT_SEEDS) {areWheatSeedsHere=true;}
                if(item==Items.BEETROOT_SEEDS) {areBeetrotsSeedsHere=true;}
            }
        }
        return areCarrotsHere&&areWheatSeedsHere&&arePotatoesHere&&areBeetrotsSeedsHere;
    }

    public boolean isBoneMealInInventory(EntityAutoFarmer farmer){
        boolean isBoneMealHere=false;
        for(int c=0;c<farmer.getLocalInventory().getSizeInventory();c++){
            if(!farmer.getLocalInventory().getStackInSlot(c).isEmpty()) {
                Item item = farmer.getLocalInventory().getStackInSlot(c).getItem();
                if (item==Items.BONE_MEAL) {
                    isBoneMealHere = true;
                }
            }
        }
        return isBoneMealHere;
    }


    public ArrayList<ItemWithInventoryIndexEntry> getBoneMealWithIndexesInInventory(InventoryBasic localInventory, EntityAutoFarmer farmer){
        ArrayList<ItemWithInventoryIndexEntry> result=new ArrayList<>();
        for(int c=0;c<localInventory.getSizeInventory();c++){
            if(!localInventory.getStackInSlot(c).isEmpty()){
                if(localInventory.getStackInSlot(c).getItem()==Items.BONE_MEAL){
                    ItemWithInventoryIndexEntry entry = new ItemWithInventoryIndexEntry(localInventory.getStackInSlot(c).getItem(),c);
                    result.add(entry);
                }
            }
        }
        return result;
    }

    public IBlockState getBlockStateToPlant(Item item){
        IBlockState result= Blocks.WHEAT.getDefaultState();
        if(item == Items.WHEAT_SEEDS) return Blocks.WHEAT.getDefaultState();
        if(item == Items.BEETROOT_SEEDS) return Blocks.BEETROOTS.getDefaultState();
        if(item == Items.CARROT) return Blocks.CARROTS.getDefaultState();
        if(item == Items.POTATO) return Blocks.POTATOES.getDefaultState();
        return result;
    }

    public ArrayList<BlockPos> chestPosesWithSeedsThatAreNotInInventory(EntityAutoFarmer NPC){
        ArrayList<BlockPos> result=new ArrayList<>();
        boolean doesNPCHaveWheatSeeds=false;
        boolean doesNPCHaveBeetrotSeeds=false;
        boolean doesNPCHaveCarrots=false;
        boolean doesNPCHavePotatoes=false;
        for(int c=0;c<NPC.getLocalInventory().getSizeInventory();c++){
            if(!NPC.getLocalInventory().getStackInSlot(c).isEmpty()) {
                Item item = NPC.getLocalInventory().getStackInSlot(c).getItem();
                if(item==Items.CARROT) {doesNPCHaveCarrots=true;}
                if(item==Items.POTATO) {doesNPCHavePotatoes=true;}
                if(item==Items.WHEAT_SEEDS) {doesNPCHaveWheatSeeds=true;}
                if(item==Items.BEETROOT_SEEDS) {doesNPCHaveBeetrotSeeds=true;}
            }
        }

        World world = NPC.getEntityWorld();
        int radius=NPC.getWorkRadius();
        for(int x=-radius;x<=radius;x++){
            for(int y=-radius;y<=radius;y++){
                for(int z=-radius;z<=radius;z++){
                    BlockPos toCheck = new BlockPos(NPC.getPosition().getX()+x,NPC.getPosition().getY()+y,NPC.getPosition().getZ()+z);
                    if(world.getTileEntity(toCheck)!=null){
                        if(world.getTileEntity(toCheck) instanceof TileEntityChest){
                            TileEntityChest chest = (TileEntityChest) world.getTileEntity(toCheck);
                            for(int c=0;c<=15;c++){
                                if(!chest.getStackInSlot(c).isEmpty()){
                                    Item item = chest.getStackInSlot(c).getItem();
                                    if(item == Items.WHEAT_SEEDS){
                                        if(!doesNPCHaveWheatSeeds){
                                            result.add(toCheck);
                                        }
                                    }
                                    if(item == Items.CARROT){
                                        if(!doesNPCHaveCarrots){
                                            result.add(toCheck);
                                        }
                                    }
                                    if(item == Items.POTATO){
                                        if(!doesNPCHavePotatoes){
                                            result.add(toCheck);
                                        }
                                    }
                                    if(item == Items.BEETROOT_SEEDS){
                                        if(!doesNPCHaveBeetrotSeeds){
                                            result.add(toCheck);
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

    public ArrayList<BlockPos> chestPosesWithBonemeal(EntityAutoFarmer npc){
        ArrayList<BlockPos> result = new ArrayList<>();
        int radius=npc.getWorkRadius();
        World world = npc.getEntityWorld();
        for(int x=-radius;x<=radius;x++){
            for(int y=-radius;y<=radius;y++){
                for(int z=-radius;z<=radius;z++){
                    BlockPos toCheck = new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getTileEntity(toCheck)!=null){
                        if(world.getTileEntity(toCheck) instanceof TileEntityChest){
                            TileEntityChest chest = (TileEntityChest) world.getTileEntity(toCheck);
                            for(int c=0;c<=15;c++){
                                if(!chest.getStackInSlot(c).isEmpty()){
                                    Item item = chest.getStackInSlot(c).getItem();
                                    if(item == Items.BONE_MEAL){
                                        result.add(toCheck);
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

    public boolean areAnySeedsInInventory(EntityAutoFarmer npc){
        boolean result=false;
        for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
            if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                Item item = npc.getLocalInventory().getStackInSlot(c).getItem();
                if(item==Items.POTATO||item==Items.CARROT||item==Items.BEETROOT_SEEDS||item==Items.WHEAT_SEEDS){
                    result=true;
                    break;
                }
            }
        }

        return result;
    }

    public ArrayList<BlockPos> getAvailableFarmlands(EntityAutoFarmer npc){
        ArrayList<BlockPos> result=new ArrayList<>();
        int radius = npc.getWorkRadius();
        World world = npc.getEntityWorld();
        for(int x=-radius;x<=radius;x++){
            for(int y=-radius;y<=radius;y++){
                for(int z=-radius;z<=radius;z++){
                    BlockPos toCheck=new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getBlockState(toCheck).getBlock() == Blocks.FARMLAND){
                        if(world.getBlockState(toCheck).get(BlockFarmland.MOISTURE)>2){
                            if(world.isAirBlock(toCheck.up())){
                                if(world.isAirBlock(toCheck.up(2))){
                                    result.add(toCheck);
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }


    public ArrayList<BlockPos> getFertilizablePlants(EntityAutoFarmer npc){
        ArrayList<BlockPos> result=new ArrayList<>();
        int radius = npc.getWorkRadius();
        World world = npc.getEntityWorld();
        for(int x=-radius;x<=radius;x++){
            for(int y=-radius;y<=radius;y++){
                for(int z=-radius;z<=radius;z++){
                    BlockPos toCheck=new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getBlockState(toCheck).has(BlockCrops.AGE)){
                        if(world.getBlockState(toCheck).get(BlockCrops.AGE)<7){
                            if(world.isAirBlock(toCheck.up())){
                                if(world.isAirBlock(toCheck.up(2))){
                                    result.add(toCheck.down());
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<EntityItem> getPickableLoot(EntityAutoFarmer npc){
        ArrayList<EntityItem> result=new ArrayList<>();
        int radius = npc.getWorkRadius();
        World world = npc.getEntityWorld();

        List<EntityItem> e = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(npc.getPosition().getX() - radius, npc.getPosition().getY() - radius, npc.getPosition().getZ() - radius, npc.getPosition().getX() + radius, npc.getPosition().getY() + radius, npc.getPosition().getZ() + radius));
        if(!e.isEmpty()){
            for(EntityItem item:e){
                result.add(item);
            }
        }
        return result;
    }

    public ArrayList<BlockPos> getPlantsReadyToHarvest(EntityAutoFarmer npc){
        ArrayList<BlockPos> result=new ArrayList<>();
        int radius = npc.getWorkRadius();
        World world = npc.getEntityWorld();
        for(int x=-radius;x<=radius;x++){
            for(int y=-radius;y<=radius;y++){
                for(int z=-radius;z<=radius;z++){
                    BlockPos toCheck=new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getBlockState(toCheck).getBlock() instanceof BlockCrops){
                        if(((BlockCrops)world.getBlockState(toCheck).getBlock()).isMaxAge(world.getBlockState(toCheck))){
                            if(world.isAirBlock(toCheck.up())){
                                if(world.isAirBlock(toCheck.up(2))){
                                    result.add(toCheck.down());
                                }
                            }
                        }
                        if(world.getBlockState(toCheck).getBlock() instanceof BlockBeetroot){
                            if(((BlockBeetroot)world.getBlockState(toCheck).getBlock()).isMaxAge(world.getBlockState(toCheck))){
                                if(world.isAirBlock(toCheck.up())){
                                    if(world.isAirBlock(toCheck.up(2))){
                                        result.add(toCheck.down());
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

    public ArrayList<ItemWithInventoryIndexEntry> getSeedsWithInventoryIndexes(EntityAutoFarmer npc){
        ArrayList<ItemWithInventoryIndexEntry> result = new ArrayList<>();
        for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
            if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                Item item = npc.getLocalInventory().getStackInSlot(c).getItem();
                if(item==Items.WHEAT_SEEDS||item==Items.BEETROOT_SEEDS||item==Items.CARROT||item==Items.POTATO){
                    ItemWithInventoryIndexEntry itemEntry = new ItemWithInventoryIndexEntry(item,c);
                    result.add(itemEntry);
                }
            }
        }
        return result;
    }

    public ArrayList<BlockPos> getChestPosesWithFreeSlots(EntityAutoFarmer npc){
        ArrayList<BlockPos> result = new ArrayList<>();
        int radius=npc.getWorkRadius();
        boolean isFreeSlotThere=false;
        World world = npc.getEntityWorld();
        for(int x=-radius;x<=radius;x++){
            for(int y=-radius;y<=radius;y++){
                for(int z=-radius;z<=radius;z++){
                    BlockPos toCheck = new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getTileEntity(toCheck)!=null){
                        if(world.getTileEntity(toCheck) instanceof TileEntityChest){
                            TileEntityChest chest = (TileEntityChest) world.getTileEntity(toCheck);
                            for(int c=0;c<=15;c++){
                                if(chest.getStackInSlot(c).isEmpty()){
                                    isFreeSlotThere=true;
                                }
                            }
                        }
                    }
                    if(isFreeSlotThere){
                        result.add(toCheck);
                    }
                }
            }
        }

        return result;
    }

    public ArrayList<ItemWithInventoryIndexEntry> getExcessItems(EntityAutoFarmer npc){
        ArrayList<ItemWithInventoryIndexEntry> result = new ArrayList<>();
        boolean areBeetrotSeedsThere=false;
        boolean areCarrotsThere=false;
        boolean arePotatoesThere=false;
        boolean areWheatSeedsThere=false;
        boolean isBoneMealHere=false;
        for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
            if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                Item item = npc.getLocalInventory().getStackInSlot(c).getItem();
                if(!areBeetrotSeedsThere && item==Items.BEETROOT_SEEDS){
                        areBeetrotSeedsThere=true;
                }
                else if(!areCarrotsThere && item == Items.CARROT){
                        areCarrotsThere=true;
                }
                else if(!arePotatoesThere && item==Items.POTATO){
                        arePotatoesThere=true;
                }
                else if(!areWheatSeedsThere && item==Items.WHEAT_SEEDS){
                    areWheatSeedsThere=true;
                }
                else if(!isBoneMealHere && item==Items.BONE_MEAL){
                    isBoneMealHere=true;
                }else{
                    result.add(new ItemWithInventoryIndexEntry(npc.getLocalInventory().getStackInSlot(c).getItem(),c));
                }
            }

        }
        return result;
    }

    //--------------------------------------------------------FARMER END----------------------------------------------------

}
