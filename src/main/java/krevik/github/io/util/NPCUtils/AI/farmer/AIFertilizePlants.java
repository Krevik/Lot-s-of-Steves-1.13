package krevik.github.io.util.NPCUtils.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import krevik.github.io.util.NPCUtils.FarmerUtils;
import krevik.github.io.util.NPCUtils.NPCUtils;
import net.minecraft.block.Block;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class AIFertilizePlants extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    NPCUtils npcUtils;
    FarmerUtils farmerUtils;
    ArrayList<BlockPos> FertilizablePlants;
    ArrayList<Block> interestingPlantBlocks;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public AIFertilizePlants(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=1000/npc.getWorkSpeed();
        actualDelay=0;
        npcUtils = LotsOfSteves.getNPCUtils();
        farmerUtils = LotsOfSteves.getFarmerUtils();
        interestingPlantBlocks = farmerUtils.getInterestingPlantBlocks(NPC);
        FertilizablePlants = new ArrayList<>();
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
        if(NPC.isShouldUseBonemeal()) {
            if (actualDelay >= runDelay) {
                if (npcUtils.isInInventory(NPC.getLocalInventory(), Items.BONE_MEAL)) {
                    FertilizablePlants = farmerUtils.getFertilizablePlantsThatAreInInterest(NPC);
                    if (!FertilizablePlants.isEmpty()) {
                        interestingPlantBlocks = farmerUtils.getInterestingPlantBlocks(NPC);
                        return true;
                    }
                }
                actualDelay = 0;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        interestingPlantBlocks = farmerUtils.getInterestingPlantBlocks(NPC);
        return !FertilizablePlants.isEmpty()&&(npcUtils.isInInventory(NPC.getLocalInventory(), Items.BONE_MEAL));
    }

    @Override
    public void startExecuting() {
        NPC.setHeldItem(Hand.MAIN_HAND,new ItemStack(Items.BONE_MEAL));
    }

    private int getPathTimerTimeout(){
        return 150;
    }

    @Override
    public void tick() {
        if(destinationBlock!=null){
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY()+1D,destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
            NPC.setHeldItem(Hand.MAIN_HAND,new ItemStack(Items.BONE_MEAL,1));
            if(getIsNearDestination()||pathTimer>=getPathTimerTimeout()){
                NPC.setHeldItem(Hand.MAIN_HAND,ItemStack.EMPTY);

                if(world.getBlockState(FertilizablePlants.get(0).up())!=null){
                    if(world.getBlockState(FertilizablePlants.get(0).up()).getBlock() instanceof CropsBlock){
                        CropsBlock crop = (CropsBlock) world.getBlockState(FertilizablePlants.get(0).up()).getBlock();
                        if (interestingPlantBlocks.contains(crop)) {
                            if(!crop.isMaxAge(world.getBlockState(FertilizablePlants.get(0).up()))){
                                if(world.isAirBlock(FertilizablePlants.get(0).up().up())){
                                    if(world.isAirBlock(FertilizablePlants.get(0).up().up(2))){
                                        if (npcUtils.isInInventory(NPC.getLocalInventory(), Items.BONE_MEAL)) {
                                            ArrayList<ItemWithInventoryIndexEntry> bonemealEntries = npcUtils.getItemsWithIndexesInInventory(NPC.getLocalInventory(), Items.BONE_MEAL);
                                            if(bonemealEntries!=null){
                                                if(!bonemealEntries.isEmpty()){
                                                    ItemWithInventoryIndexEntry itemEntry = bonemealEntries.get(NPC.getRNG().nextInt(bonemealEntries.size()));
                                                    if (!NPC.getEntityWorld().isRemote()) {
                                                        NPC.getEntityWorld().playEvent(2005, FertilizablePlants.get(0).up(), 0);
                                                    }
                                                    ((CropsBlock) world.getBlockState(FertilizablePlants.get(0).up()).getBlock()).grow(world, FertilizablePlants.get(0).up(), world.getBlockState(FertilizablePlants.get(0).up()));
                                                    NPC.getLocalInventory().getStackInSlot(itemEntry.getInventoryIndex()).shrink(1);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }else if(world.getBlockState(FertilizablePlants.get(0)).getBlock() instanceof CocoaBlock){
                        CocoaBlock cocoaBlock = (CocoaBlock) world.getBlockState(FertilizablePlants.get(0)).getBlock();
                        if (interestingPlantBlocks.contains(cocoaBlock)) {
                            if(world.getBlockState(FertilizablePlants.get(0)).has(cocoaBlock.AGE)){
                                if(world.getBlockState(FertilizablePlants.get(0)).get(cocoaBlock.AGE)<farmerUtils.getMaxAgeForProperty(cocoaBlock.AGE)){

                                    if (npcUtils.isInInventory(NPC.getLocalInventory(), Items.BONE_MEAL)) {
                                        ArrayList<ItemWithInventoryIndexEntry> bonemealEntries = npcUtils.getItemsWithIndexesInInventory(NPC.getLocalInventory(), Items.BONE_MEAL);
                                        if(bonemealEntries!=null){
                                            if(!bonemealEntries.isEmpty()){
                                                ItemWithInventoryIndexEntry itemEntry = bonemealEntries.get(NPC.getRNG().nextInt(bonemealEntries.size()));
                                                if (!NPC.getEntityWorld().isRemote()) {
                                                    NPC.getEntityWorld().playEvent(2005, FertilizablePlants.get(0), 0);
                                                }
                                                ((CocoaBlock) world.getBlockState(FertilizablePlants.get(0)).getBlock()).grow(NPC.getEntityWorld().getWorld().getServer().getWorld(NPC.getEntityWorld().getDimension().getType()), NPC.getRNG(),FertilizablePlants.get(0), world.getBlockState(FertilizablePlants.get(0)));
                                                NPC.getLocalInventory().getStackInSlot(itemEntry.getInventoryIndex()).shrink(1);
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
                destinationBlock=null;
                pathTimer=0;
                FertilizablePlants.remove(0);
                NPC.setHeldItem(Hand.MAIN_HAND,ItemStack.EMPTY);
            }
        }else{
            if(NPC.getEntityWorld().getBlockState(FertilizablePlants.get(0)).getBlock() instanceof CocoaBlock){
                BlockPos floorUnderCocoa = farmerUtils.getFloorUnderCocoa(NPC,FertilizablePlants.get(0));
                if(MathHelper.abs(floorUnderCocoa.getY()-FertilizablePlants.get(0).getY())<=5){
                    destinationBlock=floorUnderCocoa;
                }else{
                    FertilizablePlants.remove(0);
                }
            }else {
                destinationBlock = FertilizablePlants.get(0);
            }
        }
    }

    public double getTargetDistanceSq() {
        return 6D;
    }

    protected boolean getIsNearDestination() {
        if (this.NPC.getDistanceSq(this.destinationBlock.getX(),this.destinationBlock.up().getY(),this.destinationBlock.getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}
