package krevik.github.io.util.NPCUtils.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import krevik.github.io.util.NPCUtils.FarmerUtils;
import krevik.github.io.util.NPCUtils.NPCUtils;
import net.minecraft.block.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class AIHarvestPlants extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    NPCUtils npcUtils;
    FarmerUtils farmerUtils;
    ArrayList<BlockPos> plantsReadyToHarvest;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public AIHarvestPlants(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=1000/npc.getWorkSpeed();
        actualDelay=0;
        farmerUtils=LotsOfSteves.getFarmerUtils();
        npcUtils=LotsOfSteves.getNPCUtils();
        plantsReadyToHarvest = new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    public void updateRunDelay(){
        runDelay=1000/NPC.getWorkSpeed();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            plantsReadyToHarvest = farmerUtils.getPlantsReadyToHarvest(NPC);
            if(!plantsReadyToHarvest.isEmpty()){
                return true;
            }
            actualDelay=0;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        return !plantsReadyToHarvest.isEmpty();
    }

    @Override
    public void startExecuting() {
        NPC.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.SHEARS));
    }

    private int getPathTimerTimeout(){
        return 150;
    }


    @Override
    public void tick() {
        if(destinationBlock!=null){
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY()+1D,destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
            if(getIsNearDestination()||pathTimer>=getPathTimerTimeout()){

                if(world.getBlockState(plantsReadyToHarvest.get(0)).getBlock() instanceof CropsBlock){
                    if(world.isAirBlock(plantsReadyToHarvest.get(0).up())){
                        if(world.isAirBlock(plantsReadyToHarvest.get(0).up(2))){
                            CropsBlock crop = (CropsBlock) world.getBlockState(plantsReadyToHarvest.get(0)).getBlock();
                            if(crop.isMaxAge(world.getBlockState(plantsReadyToHarvest.get(0)))){
                                Block harvestedBlock = world.getBlockState(plantsReadyToHarvest.get(0)).getBlock();
                                world.destroyBlock(plantsReadyToHarvest.get(0),true);
                                if(NPC.getCropRotation()){
                                    ArrayList<ItemWithInventoryIndexEntry> seeds = farmerUtils.getAllowedSeedsForCropRotation(NPC,farmerUtils.recreateSeedFromHarvestedBlock(harvestedBlock));
                                    if(seeds!=null){
                                        if(!seeds.isEmpty()){
                                            ItemWithInventoryIndexEntry seed = seeds.get(NPC.getRNG().nextInt(seeds.size()));
                                            BlockState toPlant = farmerUtils.getBlockStateToPlant(seed.getItem());
                                            world.setBlockState(plantsReadyToHarvest.get(0),toPlant,3);
                                            NPC.getLocalInventory().getStackInSlot(seed.getInventoryIndex()).shrink(1);
                                        }
                                    }
                                }else{
                                    if(npcUtils.isInInventory(NPC.getLocalInventory(), farmerUtils.recreateSeedFromHarvestedBlock(harvestedBlock))){
                                        ArrayList<ItemWithInventoryIndexEntry> seeds = farmerUtils.getItemsWithIndexesInInventory(NPC.getLocalInventory(),farmerUtils.recreateSeedFromHarvestedBlock(harvestedBlock));
                                        ItemWithInventoryIndexEntry seed = seeds.get(NPC.getRNG().nextInt(seeds.size()));
                                        BlockState toPlant = farmerUtils.getBlockStateToPlant(seed.getItem());
                                        world.setBlockState(plantsReadyToHarvest.get(0),toPlant,3);
                                        NPC.getLocalInventory().getStackInSlot(seed.getInventoryIndex()).shrink(1);
                                    }
                                }
                            }
                        }
                    }
                }

                if(world.getBlockState(plantsReadyToHarvest.get(0)).getBlock() instanceof SugarCaneBlock){
                    if(world.getBlockState(plantsReadyToHarvest.get(0).up()).getBlock() instanceof SugarCaneBlock && !(world.getBlockState(plantsReadyToHarvest.get(0).down()).getBlock() instanceof SugarCaneBlock)){
                        world.destroyBlock(plantsReadyToHarvest.get(0).up(),true);
                    }
                }

                if(world.getBlockState(plantsReadyToHarvest.get(0)).getBlock() instanceof CocoaBlock){
                    CocoaBlock cocoaBlock = (CocoaBlock) world.getBlockState(plantsReadyToHarvest.get(0)).getBlock();
                    if(world.getBlockState(plantsReadyToHarvest.get(0)).has(cocoaBlock.AGE)){
                        if(world.getBlockState(plantsReadyToHarvest.get(0)).get(cocoaBlock.AGE)>=farmerUtils.getMaxAgeForProperty(cocoaBlock.AGE)){
                            world.destroyBlock(plantsReadyToHarvest.get(0),true);
                            //try to replant
                            if(npcUtils.isInInventory(NPC.getLocalInventory(), Items.COCOA_BEANS)){
                                ArrayList<ItemWithInventoryIndexEntry> seeds = farmerUtils.getItemsWithIndexesInInventory(NPC.getLocalInventory(),Items.COCOA_BEANS);
                                ItemWithInventoryIndexEntry seed = seeds.get(NPC.getRNG().nextInt(seeds.size()));

                                BlockPos freePos = farmerUtils.getAirBlockAround(world,plantsReadyToHarvest.get(0));
                                BlockPos logPos = plantsReadyToHarvest.get(0);
                                BlockState stateToPlant = farmerUtils.getBlockStateToPlant(seed.getItem());
                                if(stateToPlant.has(CocoaBlock.HORIZONTAL_FACING)){
                                    if(freePos.getX() == logPos.west().getX() && freePos.getY() == logPos.west().getY() && freePos.getZ() == logPos.west().getZ()){
                                        stateToPlant = stateToPlant.with(CocoaBlock.HORIZONTAL_FACING, Direction.EAST);
                                    }
                                    if(freePos.getX() == logPos.east().getX() && freePos.getY() == logPos.east().getY() && freePos.getZ() == logPos.east().getZ()){
                                        stateToPlant = stateToPlant.with(CocoaBlock.HORIZONTAL_FACING, Direction.WEST);
                                    }
                                    if(freePos.getX() == logPos.north().getX() && freePos.getY() == logPos.north().getY() && freePos.getZ() == logPos.north().getZ()){
                                        stateToPlant = stateToPlant.with(CocoaBlock.HORIZONTAL_FACING, Direction.SOUTH);
                                    }
                                    if(freePos.getX() == logPos.south().getX() && freePos.getY() == logPos.south().getY() && freePos.getZ() == logPos.south().getZ()){
                                        stateToPlant = stateToPlant.with(CocoaBlock.HORIZONTAL_FACING, Direction.NORTH);
                                    }
                                }
                                CocoaBlock block = (CocoaBlock) Blocks.COCOA;
                                boolean isValidPosition = block.isValidPosition(stateToPlant,world,freePos);
                                if(isValidPosition) {
                                    world.setBlockState(freePos, stateToPlant, 3);
                                    NPC.getLocalInventory().getStackInSlot(seed.getInventoryIndex()).shrink(1);
                                }
                            }
                        }
                    }
                }

                if(world.getBlockState(plantsReadyToHarvest.get(0)).getBlock() instanceof NetherWartBlock){
                    NetherWartBlock netherWartBlock = (NetherWartBlock) world.getBlockState(plantsReadyToHarvest.get(0)).getBlock();
                    if(world.getBlockState(plantsReadyToHarvest.get(0)).has(netherWartBlock.AGE)){
                        if(world.getBlockState(plantsReadyToHarvest.get(0)).get(netherWartBlock.AGE)>=farmerUtils.getMaxAgeForProperty(netherWartBlock.AGE)){
                            world.destroyBlock(plantsReadyToHarvest.get(0),true);
                            //try to replant
                            if(npcUtils.isInInventory(NPC.getLocalInventory(), Items.NETHER_WART)){
                                ArrayList<ItemWithInventoryIndexEntry> seeds = farmerUtils.getItemsWithIndexesInInventory(NPC.getLocalInventory(),Items.NETHER_WART);
                                ItemWithInventoryIndexEntry seed = seeds.get(NPC.getRNG().nextInt(seeds.size()));
                                BlockState toPlant = farmerUtils.getBlockStateToPlant(seed.getItem());
                                world.setBlockState(plantsReadyToHarvest.get(0),toPlant,3);
                                NPC.getLocalInventory().getStackInSlot(seed.getInventoryIndex()).shrink(1);
                            }
                        }
                    }
                }

                destinationBlock=null;
                pathTimer=0;
                plantsReadyToHarvest.remove(0);
            }
        }else{
            if(world.getBlockState(plantsReadyToHarvest.get(0)).getBlock() instanceof CocoaBlock){
                BlockPos floorUnderCocoa = farmerUtils.getFloorUnderCocoa(NPC,plantsReadyToHarvest.get(0));
                if(MathHelper.abs(floorUnderCocoa.getY()-plantsReadyToHarvest.get(0).getY())<=5){
                    destinationBlock=farmerUtils.getFreePosAroundCocoa(world,floorUnderCocoa);
                }else{
                    plantsReadyToHarvest.remove(0);
                }
            }else {
                destinationBlock = plantsReadyToHarvest.get(0);
            }
        }
    }



    public double getTargetDistanceSq() {
        return 6D;
    }

    protected boolean getIsNearDestination() {
        if (this.NPC.getDistanceSq(this.destinationBlock.getX(),this.destinationBlock.getY(),this.destinationBlock.getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}

