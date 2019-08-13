package krevik.github.io.util;

import com.google.common.collect.ImmutableSet;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.entity.EntityAutoLumberjack;
import net.minecraft.block.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import org.antlr.v4.runtime.misc.Array2DHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FunctionHelper {

    //--------------------------------------------------------FARMER START----------------------------------------------------

    public boolean isFreeSlotInInventory(Inventory inv){
        boolean is=false;
        for(int x=0;x<inv.getSizeInventory();x++){
            if(inv.getStackInSlot(x).isEmpty()){
                is=true;
                break;
            }
        }
        return is;
    }


    //NEW
    public boolean NEWareAllSeedsOfInterestInInventory(EntityAutoFarmer farmer){
        int required=farmer.SEEDS.size();
        int alreadyHaving=0;
        for(Item item: farmer.SEEDS){
            for(int c=0;c<farmer.getLocalInventory().getSizeInventory();c++){
                if(!farmer.getLocalInventory().getStackInSlot(c).isEmpty()) {
                    if(item == farmer.getLocalInventory().getStackInSlot(c).getItem()){
                        alreadyHaving++;
                        break;
                    }
                }
            }
        }
        return alreadyHaving>=required;
    }

    public boolean areAllSeedsOfInterestInInventory(EntityAutoFarmer farmer){
        boolean areCarrotsHere=false;
        boolean areWheatSeedsHere=false;
        boolean arePotatoesHere=false;
        boolean areBeetrotsSeedsHere=false;
        for(int c=0;c<farmer.getLocalInventory().getSizeInventory();c++){
            if(!farmer.getLocalInventory().getStackInSlot(c).isEmpty()) {
                Item item = farmer.getLocalInventory().getStackInSlot(c).getItem();
                if(item==Items.CARROT || (isSeedAtAll(item) && !farmer.SEEDS.contains(item)) ) {areCarrotsHere=true;}
                if(item==Items.POTATO || (isSeedAtAll(item) && !farmer.SEEDS.contains(item))) {arePotatoesHere=true;}
                if(item==Items.WHEAT_SEEDS || (isSeedAtAll(item) && !farmer.SEEDS.contains(item))) {areWheatSeedsHere=true;}
                if(item==Items.BEETROOT_SEEDS || (isSeedAtAll(item) && !farmer.SEEDS.contains(item))) {areBeetrotsSeedsHere=true;}
            }
        }
        return areCarrotsHere&&areWheatSeedsHere&&arePotatoesHere&&areBeetrotsSeedsHere;
    }

    private boolean isSeedAtAll(Item item){
        boolean result=false;
        if(item==Items.CARROT||item==Items.POTATO||item==Items.WHEAT_SEEDS||item==Items.BEETROOT_SEEDS){
            result=true;
        }
        return result;
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


    public ArrayList<ItemWithInventoryIndexEntry> getBoneMealWithIndexesInInventory(Inventory localInventory, EntityAutoFarmer farmer){
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

    public BlockState getBlockStateToPlant(Item item){
        BlockState result = Blocks.WHEAT.getDefaultState();
        if(item == Items.WHEAT_SEEDS) return Blocks.WHEAT.getDefaultState();
        if(item == Items.BEETROOT_SEEDS) return Blocks.BEETROOTS.getDefaultState();
        if(item == Items.CARROT) return Blocks.CARROTS.getDefaultState();
        if(item == Items.POTATO) return Blocks.POTATOES.getDefaultState();
        return result;
    }

    //NEW
    public ArrayList<BlockPos> NEWchestPosesWithSeedsThatAreNotInInventoryButAreInInterest(EntityAutoFarmer npc){
        ArrayList<Item> all_required_seeds = new ArrayList<>();
        all_required_seeds.addAll(npc.SEEDS);
        ArrayList<Item> actual_having_seeds = new ArrayList<>();
        for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
            if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()) {
                Item item = npc.getLocalInventory().getStackInSlot(c).getItem();
                if(all_required_seeds.contains(item)){
                    actual_having_seeds.add(item);
                }
            }
        }
        ArrayList<Item> required_seeds = new ArrayList<>();
        for(Item item: all_required_seeds){
            if(!actual_having_seeds.contains(item)){
                required_seeds.add(item);
            }
        }

        ArrayList<BlockPos> result=new ArrayList<>();
        World world = npc.getEntityWorld();
        int xRadius=npc.getWorkingRadius().getXRadius();
        int yRadius=npc.getWorkingRadius().getYRadius();
        int zRadius=npc.getWorkingRadius().getZRadius();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck = new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getTileEntity(toCheck)!=null){
                        if(world.getTileEntity(toCheck) instanceof ChestTileEntity){
                            ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(toCheck);
                            for(int c=0;c<=15;c++){
                                if(!chest.getStackInSlot(c).isEmpty()){
                                    Item item = chest.getStackInSlot(c).getItem();
                                    if(required_seeds.contains(item)){
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

    public ArrayList<BlockPos> chestPosesWithSeedsThatAreNotInInventoryButAreInInterest(EntityAutoFarmer NPC){
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
        int xRadius=NPC.getWorkingRadius().getXRadius();
        int yRadius=NPC.getWorkingRadius().getYRadius();
        int zRadius=NPC.getWorkingRadius().getZRadius();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck = new BlockPos(NPC.getPosition().getX()+x,NPC.getPosition().getY()+y,NPC.getPosition().getZ()+z);
                    if(world.getTileEntity(toCheck)!=null){
                        if(world.getTileEntity(toCheck) instanceof ChestTileEntity){
                            ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(toCheck);
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
        int xRadius = npc.getWorkingRadius().getXRadius();
        int yRadius = npc.getWorkingRadius().getYRadius();
        int zRadius = npc.getWorkingRadius().getZRadius();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck = new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getTileEntity(toCheck)!=null){
                        if(world.getTileEntity(toCheck) instanceof ChestTileEntity){
                            ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(toCheck);
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
                if(npc.SEEDS.contains(item)){
                    result=true;
                    break;
                }
            }
        }

        return result;
    }

    public ArrayList<BlockPos> getAvailableFarmlands(EntityAutoFarmer npc){
        ArrayList<BlockPos> result=new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getXRadius();
        int yRadius = npc.getWorkingRadius().getYRadius();
        int zRadius = npc.getWorkingRadius().getZRadius();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck=new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getBlockState(toCheck).getBlock() == Blocks.FARMLAND){
                        if(world.getBlockState(toCheck).get(FarmlandBlock.MOISTURE)>2){
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
        int xRadius = npc.getWorkingRadius().getXRadius();
        int yRadius = npc.getWorkingRadius().getYRadius();
        int zRadius = npc.getWorkingRadius().getZRadius();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck=new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getBlockState(toCheck).has(CropsBlock.AGE)){
                        if(world.getBlockState(toCheck).get(CropsBlock.AGE)<7){
                            if(world.isAirBlock(toCheck.up())){
                                if(world.isAirBlock(toCheck.up(2))){
                                    Block blockToCheck=world.getBlockState(toCheck).getBlock();
                                    if(npc.CROPS_ALLOWED.contains(blockToCheck)) {
                                        result.add(toCheck.down());
                                    }
                                }
                            }
                        }
                    }
                    if(world.getBlockState(toCheck).has(BeetrootBlock.BEETROOT_AGE)){
                        if(world.getBlockState(toCheck).get(BeetrootBlock.BEETROOT_AGE)<3){
                            if(world.isAirBlock(toCheck.up())){
                                if(world.isAirBlock(toCheck.up(2))){
                                    Block blockToCheck=world.getBlockState(toCheck).getBlock();
                                    if(npc.CROPS_ALLOWED.contains(blockToCheck)) {
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

    public ArrayList<ItemEntity> getPickableLoot(EntityAutoFarmer npc){
        ArrayList<ItemEntity> result=new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getXRadius();
        int yRadius = npc.getWorkingRadius().getYRadius();
        int zRadius = npc.getWorkingRadius().getZRadius();
        World world = npc.getEntityWorld();

        List<ItemEntity> e = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(npc.getPosition().getX() - xRadius, npc.getPosition().getY() - yRadius, npc.getPosition().getZ() - zRadius, npc.getPosition().getX() + xRadius, npc.getPosition().getY() + yRadius, npc.getPosition().getZ() + zRadius));
        if(!e.isEmpty()){
            for(ItemEntity item:e){
                if(npc.ITEMS_OF_INTEREST.contains(item.getItem().getItem())) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    public ArrayList<BlockPos> getPlantsReadyToHarvest(EntityAutoFarmer npc){
        ArrayList<BlockPos> result=new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getXRadius();
        int yRadius = npc.getWorkingRadius().getYRadius();
        int zRadius = npc.getWorkingRadius().getZRadius();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck=new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getBlockState(toCheck).has(CropsBlock.AGE) ){
                        if( (world.getBlockState(toCheck).get(CropsBlock.AGE)>=7) ){
                            if(world.isAirBlock(toCheck.up())){
                                if(world.isAirBlock(toCheck.up(2))){
                                    Block blockToCheck = world.getBlockState(toCheck).getBlock();
                                    if(npc.CROPS_ALLOWED.contains(blockToCheck)) {
                                        result.add(toCheck.down());
                                    }
                                }
                            }
                        }
                    }
                    if(world.getBlockState(toCheck).has(BeetrootBlock.BEETROOT_AGE)){
                        if(world.getBlockState(toCheck).get(BeetrootBlock.BEETROOT_AGE)>=3){
                            if(world.isAirBlock(toCheck.up())){
                                if(world.isAirBlock(toCheck.up(2))){
                                    Block blockToCheck = world.getBlockState(toCheck).getBlock();
                                    if(npc.CROPS_ALLOWED.contains(blockToCheck)) {
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
                if(npc.SEEDS.contains(item)){
                    ItemWithInventoryIndexEntry itemEntry = new ItemWithInventoryIndexEntry(item,c);
                    result.add(itemEntry);
                }
            }
        }
        return result;
    }

    public BlockPos getNearestChestPos(EntityAutoFarmer npc, ArrayList<BlockPos> chestPoses){
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


    public ArrayList<BlockPos> getChestPosesWithFreeSlots(EntityAutoFarmer npc){
        ArrayList<BlockPos> result = new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getXRadius();
        int yRadius = npc.getWorkingRadius().getYRadius();
        int zRadius = npc.getWorkingRadius().getZRadius();
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
                    }
                }
            }
        }

        return result;
    }


    public ArrayList<ItemWithInventoryIndexEntry> NEWgetExcessItems(EntityAutoFarmer npc) {
        ArrayList<ItemWithInventoryIndexEntry> result = new ArrayList<>();
        for(Item item: npc.ITEMS_OF_INTEREST){
            boolean foundThatItemInInventory=false;
            for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
                if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                    Item itemInCurrentSlot = npc.getLocalInventory().getStackInSlot(c).getItem();
                    if(foundThatItemInInventory){
                        result.add(new ItemWithInventoryIndexEntry(item,c));
                    }
                    if(item == itemInCurrentSlot){
                        foundThatItemInInventory=true;
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
                if((!areBeetrotSeedsThere ) && item==Items.BEETROOT_SEEDS){
                    areBeetrotSeedsThere=true;
                }
                else if((!areCarrotsThere ) && item == Items.CARROT){
                    areCarrotsThere=true;
                }
                else if((!arePotatoesThere ) && item==Items.POTATO){
                    arePotatoesThere=true;
                }
                else if((!areWheatSeedsThere ) && item==Items.WHEAT_SEEDS){
                    areWheatSeedsThere=true;
                }
                else if((!isBoneMealHere ) && item==Items.BONE_MEAL){
                    isBoneMealHere=true;
                }else{
                    result.add(new ItemWithInventoryIndexEntry(npc.getLocalInventory().getStackInSlot(c).getItem(),c));
                }
            }

        }
        return result;
    }

    //--------------------------------------------------------FARMER END----------------------------------------------------

    //--------------------------------------------------------LUMBERJACK START----------------------------------------------

    public boolean areToolsInInventory(EntityAutoLumberjack npc){
        boolean result=false;
        for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
            if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                if(npc.getLocalInventory().getStackInSlot(c).getItem() instanceof AxeItem){
                    result=true;
                }
            }
        }

        return result;
    }

    public ArrayList<ItemEntity> getPickableLootLumberjack(EntityAutoLumberjack npc){
        ArrayList<ItemEntity> result=new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getXRadius();
        int yRadius = npc.getWorkingRadius().getYRadius();
        int zRadius = npc.getWorkingRadius().getZRadius();
        World world = npc.getEntityWorld();

        List<ItemEntity> e = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(npc.getPosition().getX() - xRadius, npc.getPosition().getY() - yRadius, npc.getPosition().getZ() - zRadius, npc.getPosition().getX() + xRadius, npc.getPosition().getY() + yRadius, npc.getPosition().getZ() + zRadius));
        if(!e.isEmpty()){
            for(ItemEntity item:e){
                if(npc.ITEMS_OF_INTEREST.contains(item.getItem().getItem()) || item.getItem().getItem() instanceof AxeItem) {
                    int posY=npc.getEntityWorld().getHeight(Heightmap.Type.WORLD_SURFACE,item.getPosition()).getY();
                    if((item.getPosition().getY()-posY)<5) {
                        result.add(item);
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<BlockPos> getChestPosesWithTools(EntityAutoLumberjack npc){
        ArrayList<BlockPos> result = new ArrayList<>();
        int radiusX = npc.getWorkingRadius().getXRadius();
        int radiusY = npc.getWorkingRadius().getYRadius();
        int radiusZ = npc.getWorkingRadius().getZRadius();
        World world = npc.getEntityWorld();
        for(int x=-radiusX;x<=radiusX;x++){
            for(int y=-radiusY;y<=radiusY;y++){
                for(int z=-radiusZ;z<=radiusZ;z++){
                    BlockPos toCheck = new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getTileEntity(toCheck) != null){
                        if(world.getTileEntity(toCheck) instanceof ChestTileEntity){
                            ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(toCheck);
                            for(int c=0;c<16;c++){
                                if(!chest.getStackInSlot(c).isEmpty()){
                                    if(chest.getStackInSlot(c).getItem() instanceof AxeItem){
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

    public boolean isToolEquipped(EntityAutoLumberjack npc){
        boolean result=false;
        if(npc.getHeldItem(Hand.MAIN_HAND).getItem() instanceof AxeItem){
            result=true;
        }
        return result;
    }

    public ArrayList<ItemWithInventoryIndexEntry> getAvailableToolsInInventory(EntityAutoLumberjack npc){
        ArrayList<ItemWithInventoryIndexEntry> result = new ArrayList<>();
        for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
            if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                if(npc.getLocalInventory().getStackInSlot(c).getItem() instanceof AxeItem){
                    ItemWithInventoryIndexEntry itemEntry = new ItemWithInventoryIndexEntry(npc.getLocalInventory().getStackInSlot(c).getItem(),c);
                    result.add(itemEntry);
                }
            }
        }
        return result;
    }

    public ArrayList<BlockPos> getLogsToHarvest(EntityAutoLumberjack npc){
        ArrayList<BlockPos> result=new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getXRadius();
        int yRadius = npc.getWorkingRadius().getYRadius();
        int zRadius = npc.getWorkingRadius().getZRadius();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int z=-zRadius;z<=zRadius;z++){
                for(int y=-yRadius;y<=yRadius;y++){
                    BlockPos toCheck=new BlockPos(npc.getHomePosition().getX()+x,npc.getHomePosition().getY()+y,npc.getHomePosition().getZ()+z);
                    Block blockToCheck = world.getBlockState(toCheck).getBlock();
                    if(npc.LOGS_ALLOWED.contains(blockToCheck)){
                        if((toCheck.getY()-npc.getPosition().getY())<12) {
                            result.add(toCheck);
                        }
                    }
                }
            }
        }
        return result;
    }

    public BlockPos getNearestTree(EntityAutoLumberjack npc,ArrayList<BlockPos> logs){
        BlockPos result=logs.get(0);
        int constantY=60;
        for(BlockPos toCheck:logs){
            Vec3d resultVecPos = new Vec3d(result.getX(),constantY,result.getZ());
            Vec3d toCheckVecPos = new Vec3d(toCheck.getX(),constantY,toCheck.getZ());
            if(npc.getDistanceSq(toCheckVecPos)<npc.getDistanceSq(resultVecPos)){
                result=toCheck;
            }
        }
        return result;
    }

    public Block getSaplingFromLogBlock(Block block){
        Block result = Blocks.OAK_SAPLING;
        if(block==Blocks.ACACIA_LOG){
            result=Blocks.ACACIA_SAPLING;
        }
        if(block==Blocks.BIRCH_LOG){
            result=Blocks.BIRCH_SAPLING;
        }
        if(block==Blocks.DARK_OAK_LOG){
            result=Blocks.DARK_OAK_SAPLING;
        }
        if(block==Blocks.JUNGLE_LOG){
            result=Blocks.JUNGLE_SAPLING;
        }
        if(block==Blocks.SPRUCE_LOG){
            result=Blocks.SPRUCE_SAPLING;
        }
        return result;
    }

    public boolean isInInventoryLumberjack(EntityAutoLumberjack npc, Item item){
        boolean result=false;
        for(int c=0;c<=npc.getLocalInventory().getSizeInventory();c++){
            if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                if(npc.getLocalInventory().getStackInSlot(c).getItem()==item){
                    result=true;
                }
            }
        }
        return result;
    }

    public ItemWithInventoryIndexEntry getItemWithInventoryIndexLumberjack(EntityAutoLumberjack npc,Item item){
        ItemWithInventoryIndexEntry result=null;
        for(int c=0;c<=npc.getLocalInventory().getSizeInventory();c++){
            if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                Item toCheck = npc.getLocalInventory().getStackInSlot(c).getItem();
                if(toCheck==item){
                    result = new ItemWithInventoryIndexEntry(toCheck,c);
                }
            }
        }

        return result;
    }

    public ArrayList<BlockPos> getChestPosesWithFreeSlotsLumberjack(EntityAutoLumberjack npc){
        ArrayList<BlockPos> result = new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getXRadius();
        int yRadius = npc.getWorkingRadius().getYRadius();
        int zRadius = npc.getWorkingRadius().getZRadius();
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
                    }
                }
            }
        }

        return result;
    }

    public ArrayList<ItemWithInventoryIndexEntry> getExcessItemsLumberjack(EntityAutoLumberjack npc){
        ArrayList<ItemWithInventoryIndexEntry> result = new ArrayList<>();
        boolean areOakSaplingsThere=false;
        boolean areAcaciaSaplingsThere=false;
        boolean areDarkOakSaplingsThere=false;
        boolean areBirchSaplingsThere=false;
        boolean areSpruceSaplingsThere=false;
        boolean areJungleSaplingsThere=false;
        boolean isBoneMealHere=false;
        for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
            if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                Item item = npc.getLocalInventory().getStackInSlot(c).getItem();
                if((!areOakSaplingsThere ) && item==Items.OAK_SAPLING){
                    areOakSaplingsThere=true;
                }
                else if((!areAcaciaSaplingsThere ) && item == Items.ACACIA_SAPLING){
                    areAcaciaSaplingsThere=true;
                }
                else if((!areDarkOakSaplingsThere ) && item==Items.DARK_OAK_SAPLING){
                    areDarkOakSaplingsThere=true;
                }
                else if((!areBirchSaplingsThere ) && item==Items.BIRCH_SAPLING){
                    areBirchSaplingsThere=true;
                }
                else if((!areSpruceSaplingsThere ) && item==Items.SPRUCE_SAPLING){
                    areSpruceSaplingsThere=true;
                }
                else if((!areJungleSaplingsThere ) && item==Items.JUNGLE_SAPLING){
                    areJungleSaplingsThere=true;
                }
                else if((!isBoneMealHere ) && item==Items.BONE_MEAL){
                    isBoneMealHere=true;
                }
                else if(item instanceof AxeItem){
                    //do nothing
                }
                else{
                    result.add(new ItemWithInventoryIndexEntry(npc.getLocalInventory().getStackInSlot(c).getItem(),c));
                }
            }

        }
        return result;
    }

    public BlockPos getNearestChestPosLumberjack(EntityAutoLumberjack npc, ArrayList<BlockPos> chestPoses){
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

    public boolean isBoneMealInInventoryLumberjack(EntityAutoLumberjack farmer){
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

    public ArrayList<BlockPos> getFertilizableSaplings(EntityAutoLumberjack npc){
        ArrayList<BlockPos> result=new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getXRadius();
        int yRadius = npc.getWorkingRadius().getYRadius();
        int zRadius = npc.getWorkingRadius().getZRadius();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck=new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getBlockState(toCheck).getBlock() instanceof SaplingBlock){
                        result.add(toCheck);
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<ItemWithInventoryIndexEntry> getBoneMealWithIndexesInInventoryLumberjack(Inventory localInventory){
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

    public ArrayList<BlockPos> chestPosesWithBonemealLumberjack(EntityAutoLumberjack npc){
        ArrayList<BlockPos> result = new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getXRadius();
        int yRadius = npc.getWorkingRadius().getYRadius();
        int zRadius = npc.getWorkingRadius().getZRadius();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck = new BlockPos(npc.getPosition().getX()+x,npc.getPosition().getY()+y,npc.getPosition().getZ()+z);
                    if(world.getTileEntity(toCheck)!=null){
                        if(world.getTileEntity(toCheck) instanceof ChestTileEntity){
                            ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(toCheck);
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

    public boolean areAllSaplingsOfInterestInInventory(EntityAutoLumberjack jack){
        boolean birchHere=false;
        boolean oakHere=false;
        boolean darkOakHere=false;
        boolean acaciaHere=false;
        boolean jungleHere=false;
        boolean spruceHere=false;
        for(int c=0;c<jack.getLocalInventory().getSizeInventory();c++){
            if(!jack.getLocalInventory().getStackInSlot(c).isEmpty()) {
                Item item = jack.getLocalInventory().getStackInSlot(c).getItem().asItem();
                if(item==Items.JUNGLE_SAPLING || (isSaplingAtAll(item) && !jack.SAPLINGS.contains(item)) ) {jungleHere=true;}
                if(item==Items.SPRUCE_SAPLING || (isSaplingAtAll(item) && !jack.SAPLINGS.contains(item))) {spruceHere=true;}
                if(item==Items.BIRCH_SAPLING || (isSaplingAtAll(item) && jack.SAPLINGS.contains(item))) {birchHere=true;}
                if(item==Items.DARK_OAK_SAPLING || (isSaplingAtAll(item) && !jack.SAPLINGS.contains(item))) {darkOakHere=true;}
                if(item==Items.ACACIA_SAPLING || (isSaplingAtAll(item) && !jack.SAPLINGS.contains(item))) {acaciaHere=true;}
                if(item==Items.OAK_SAPLING || (isSaplingAtAll(item) && !jack.SAPLINGS.contains(item))) {oakHere=true;}
            }
        }
        return birchHere&&oakHere&&darkOakHere&&acaciaHere&&jungleHere&&spruceHere;
    }

    private boolean isSaplingAtAll(Item item){
        boolean result=false;
        if(item==Items.JUNGLE_SAPLING||item==Items.SPRUCE_SAPLING||item==Items.BIRCH_SAPLING
            || item==Items.DARK_OAK_SAPLING || item==Items.ACACIA_SAPLING ||item==Items.OAK_SAPLING){
            result=true;
        }
        return result;
    }

    public ArrayList<BlockPos> chestPosesWithSaplingsThatAreNotInInventoryButAreInInterest(EntityAutoLumberjack NPC){
        ArrayList<BlockPos> result=new ArrayList<>();
        boolean hasOak=false;
        boolean hasBirch=false;
        boolean hasSpruce=false;
        boolean hasJungle=false;
        boolean hasAcacia=false;
        boolean hasDarkOak=false;
        for(int c=0;c<NPC.getLocalInventory().getSizeInventory();c++){
            if(!NPC.getLocalInventory().getStackInSlot(c).isEmpty()) {
                Item item = NPC.getLocalInventory().getStackInSlot(c).getItem();
                if(item==Item.getItemFromBlock(Blocks.JUNGLE_SAPLING)) {hasJungle=true;}
                if(item==Item.getItemFromBlock(Blocks.ACACIA_SAPLING)) {hasAcacia=true;}
                if(item==Item.getItemFromBlock(Blocks.OAK_SAPLING)) {hasOak=true;}
                if(item==Item.getItemFromBlock(Blocks.DARK_OAK_SAPLING)) {hasDarkOak=true;}
                if(item==Item.getItemFromBlock(Blocks.BIRCH_SAPLING)) {hasBirch=true;}
                if(item==Item.getItemFromBlock(Blocks.SPRUCE_SAPLING)) {hasSpruce=true;}

            }
        }

        World world = NPC.getEntityWorld();
        int xRadius=NPC.getWorkingRadius().getXRadius();
        int yRadius=NPC.getWorkingRadius().getYRadius();
        int zRadius=NPC.getWorkingRadius().getZRadius();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck = new BlockPos(NPC.getPosition().getX()+x,NPC.getPosition().getY()+y,NPC.getPosition().getZ()+z);
                    if(world.getTileEntity(toCheck)!=null){
                        if(world.getTileEntity(toCheck) instanceof ChestTileEntity){
                            ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(toCheck);
                            for(int c=0;c<=15;c++){
                                if(!chest.getStackInSlot(c).isEmpty()){
                                    Item item = chest.getStackInSlot(c).getItem();
                                    if(item == Items.JUNGLE_SAPLING){
                                        if(!hasJungle){
                                            result.add(toCheck);
                                        }
                                    }
                                    if(item == Items.OAK_SAPLING){
                                        if(!hasOak){
                                            result.add(toCheck);
                                        }
                                    }
                                    if(item == Items.DARK_OAK_SAPLING){
                                        if(!hasDarkOak){
                                            result.add(toCheck);
                                        }
                                    }
                                    if(item == Items.BIRCH_SAPLING){
                                        if(!hasBirch){
                                            result.add(toCheck);
                                        }
                                    }
                                    if(item == Items.ACACIA_SAPLING){
                                        if(!hasAcacia){
                                            result.add(toCheck);
                                        }
                                    }
                                    if(item == Items.SPRUCE_SAPLING){
                                        if(!hasSpruce){
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

    public boolean areAnySaplingsInInventory(EntityAutoLumberjack npc){
        boolean result=false;
        for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
            if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                Item item = npc.getLocalInventory().getStackInSlot(c).getItem();
                if(npc.SAPLINGS.contains(item)){
                    result=true;
                    break;
                }
            }
        }

        return result;
    }

    public ArrayList<BlockPos> getEmptyPlantPlaces(EntityAutoLumberjack npc){
        ArrayList<BlockPos> result=new ArrayList<>();
        int xRadius = npc.getWorkingRadius().getXRadius();
        int yRadius = npc.getWorkingRadius().getYRadius();
        int zRadius = npc.getWorkingRadius().getZRadius();
        World world = npc.getEntityWorld();
        for(int x=-xRadius;x<=xRadius;x++){
            for(int y=-yRadius;y<=yRadius;y++){
                for(int z=-zRadius;z<=zRadius;z++){
                    BlockPos toCheck=new BlockPos(npc.getHomePosition().getX()+x,npc.getHomePosition().getY()+y,npc.getHomePosition().getZ()+z);
                    Block blockToCheck = world.getBlockState(toCheck).getBlock();
                    if(blockToCheck==Blocks.GRASS_BLOCK||blockToCheck==Blocks.COARSE_DIRT||blockToCheck==Blocks.PODZOL||
                    blockToCheck==Blocks.DIRT){
                            if(world.isAirBlock(toCheck.up())){
                                if(world.isAirBlock(toCheck.up(2))){
                                    if(world.isAirBlock(toCheck.up(3))) {
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

    public ArrayList<ItemWithInventoryIndexEntry> getSaplingsWithInventoryIndexes(EntityAutoLumberjack npc){
        ArrayList<ItemWithInventoryIndexEntry> result = new ArrayList<>();
        for(int c=0;c<npc.getLocalInventory().getSizeInventory();c++){
            if(!npc.getLocalInventory().getStackInSlot(c).isEmpty()){
                Item item = npc.getLocalInventory().getStackInSlot(c).getItem();
                if(npc.SAPLINGS.contains(item)){
                    ItemWithInventoryIndexEntry itemEntry = new ItemWithInventoryIndexEntry(item,c);
                    result.add(itemEntry);
                }
            }
        }
        return result;
    }
}
