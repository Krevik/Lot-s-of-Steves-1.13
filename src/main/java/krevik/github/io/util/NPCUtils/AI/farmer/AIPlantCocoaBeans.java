package krevik.github.io.util.NPCUtils.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import krevik.github.io.util.NPCUtils.FarmerUtils;
import krevik.github.io.util.NPCUtils.NPCUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.HorizontalBlock;
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

public class AIPlantCocoaBeans extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    FarmerUtils farmerUtils;
    NPCUtils npcUtils;
    ArrayList<BlockPos> plantableLogs;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public AIPlantCocoaBeans(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=1000/npc.getWorkSpeed();
        actualDelay=0;
        farmerUtils = LotsOfSteves.getFarmerUtils();
        npcUtils = LotsOfSteves.getNPCUtils();
        plantableLogs = new ArrayList<>();
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
        if(NPC.getGENERAL_ALLOWED_ITEMS().contains(Items.COCOA_BEANS)) {
            if (actualDelay >= runDelay) {
                if (npcUtils.isInInventory(NPC.getLocalInventory(), Items.COCOA_BEANS)) {
                    plantableLogs = farmerUtils.getAvailableLogsForCocoaBean(NPC);
                    if (!plantableLogs.isEmpty()) {
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
        return !plantableLogs.isEmpty() && farmerUtils.isInInventory(NPC.getLocalInventory(),Items.COCOA_BEANS);
    }

    @Override
    public void startExecuting() {
        NPC.setHeldItem(Hand.MAIN_HAND,new ItemStack(Items.COCOA_BEANS));
    }

    private int getPathTimerTimeout(){
        return (int) (this.NPC.getDistanceSq(this.destinationBlock.getX(),destinationBlock.getY(),destinationBlock.getZ())*50);
    }

    @Override
    public void tick() {
        if(destinationBlock!=null){
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY()+1D,destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
            NPC.setHeldItem(Hand.MAIN_HAND,new ItemStack(Items.COCOA_BEANS));
            if(getIsNearDestination()||pathTimer>=getPathTimerTimeout()){
                if(world.getBlockState(plantableLogs.get(0)).getBlock() == Blocks.JUNGLE_LOG){
                    if(farmerUtils.isAirAround(world, plantableLogs.get(0))){
                        ArrayList<ItemWithInventoryIndexEntry> itemEntries = farmerUtils.getItemsWithIndexesInInventory(NPC.getLocalInventory(),Items.COCOA_BEANS);
                        if(!itemEntries.isEmpty()) {
                            ItemWithInventoryIndexEntry itemEntry = itemEntries.get(NPC.getRNG().nextInt(itemEntries.size()));
                            BlockPos freePos = farmerUtils.getAirBlockAround(world,plantableLogs.get(0));
                            BlockPos logPos = plantableLogs.get(0);
                            BlockState stateToPlant = farmerUtils.getBlockStateToPlant(itemEntry.getItem());
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
                            if(isValidPosition){
                                world.setBlockState(freePos, stateToPlant, 3);
                                NPC.getLocalInventory().getStackInSlot(itemEntry.getInventoryIndex()).shrink(1);
                                NPC.setHeldItem(Hand.MAIN_HAND,ItemStack.EMPTY);
                            }
                        }
                    }
                }

                destinationBlock=null;
                pathTimer=0;
                plantableLogs.remove(0);
                NPC.setHeldItem(Hand.MAIN_HAND,ItemStack.EMPTY);
            }
        }else{
            BlockPos floorUnderCocoa = farmerUtils.getFloorUnderCocoa(NPC,plantableLogs.get(0));
            if(MathHelper.abs(floorUnderCocoa.getY()-plantableLogs.get(0).getY())<=5){
                destinationBlock=farmerUtils.getAirBlockAround(world,floorUnderCocoa);
            }else{
                plantableLogs.remove(0);
            }
        }
    }

    public double getTargetDistanceSq() {
        return 6D;
    }

    protected boolean getIsNearDestination() {
        if (this.NPC.getDistanceSq(this.destinationBlock.getX(),destinationBlock.getY(),destinationBlock.getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}
