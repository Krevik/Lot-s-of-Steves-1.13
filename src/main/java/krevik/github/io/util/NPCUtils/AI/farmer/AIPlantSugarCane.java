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
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class AIPlantSugarCane extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    FarmerUtils farmerUtils;
    NPCUtils npcUtils;
    ArrayList<BlockPos> plantableBlocksNearWater;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public AIPlantSugarCane(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=1000/npc.getWorkSpeed();
        actualDelay=0;
        farmerUtils = LotsOfSteves.getFarmerUtils();
        npcUtils = LotsOfSteves.getNPCUtils();
        plantableBlocksNearWater = new ArrayList<>();
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
        if(NPC.getGENERAL_ALLOWED_ITEMS().contains(Items.SUGAR_CANE)) {
            if (actualDelay >= runDelay) {
                if (npcUtils.isInInventory(NPC.getLocalInventory(), Items.SUGAR_CANE)) {
                    plantableBlocksNearWater = farmerUtils.getAvailableFarmBlocksForSugarCane(NPC);
                    if (!plantableBlocksNearWater.isEmpty()) {
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
        return !plantableBlocksNearWater.isEmpty() && farmerUtils.isInInventory(NPC.getLocalInventory(),Items.SUGAR_CANE);
    }

    @Override
    public void startExecuting() {
        NPC.setHeldItem(Hand.MAIN_HAND,new ItemStack(Items.SUGAR_CANE));
    }

    private int getPathTimerTimeout(){
        return 150;
    }

    @Override
    public void tick() {
        if(destinationBlock!=null){
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY()+1D,destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
            NPC.setHeldItem(Hand.MAIN_HAND,new ItemStack(Items.SUGAR_CANE));
            if(getIsNearDestination()||pathTimer>=getPathTimerTimeout()){
                Block block = world.getBlockState(plantableBlocksNearWater.get(0).down()).getBlock();
                if(block instanceof GrassBlock || block instanceof SandBlock || block==Blocks.DIRT ||
                        block == Blocks.SAND || block == Blocks.PODZOL || block == Blocks.COARSE_DIRT || block == Blocks.RED_SAND
                        || block == Blocks.GRASS_BLOCK) {
                    if (world.getBlockState(plantableBlocksNearWater.get(0).east().down()).getBlock() == Blocks.WATER ||
                            world.getBlockState(plantableBlocksNearWater.get(0).west().down()).getBlock() == Blocks.WATER ||
                            world.getBlockState(plantableBlocksNearWater.get(0).south().down()).getBlock() == Blocks.WATER ||
                            world.getBlockState(plantableBlocksNearWater.get(0).north().down()).getBlock() == Blocks.WATER) {
                        ArrayList<ItemWithInventoryIndexEntry> itemEntries = farmerUtils.getItemsWithIndexesInInventory(NPC.getLocalInventory(), Items.SUGAR_CANE);
                        if (!itemEntries.isEmpty()) {
                            ItemWithInventoryIndexEntry itemEntry = itemEntries.get(NPC.getRNG().nextInt(itemEntries.size()));
                            BlockState stateToPlant = farmerUtils.getBlockStateToPlant(itemEntry.getItem());
                            if (world.isAirBlock(plantableBlocksNearWater.get(0))) {
                                if (world.isAirBlock(plantableBlocksNearWater.get(0).up(1))) {
                                    world.setBlockState(plantableBlocksNearWater.get(0), stateToPlant, 3);
                                    NPC.getLocalInventory().getStackInSlot(itemEntry.getInventoryIndex()).shrink(1);
                                }
                            }
                            NPC.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                        }
                    }
                }

                destinationBlock=null;
                pathTimer=0;
                plantableBlocksNearWater.remove(0);
                NPC.setHeldItem(Hand.MAIN_HAND,ItemStack.EMPTY);
            }
        }else{
            destinationBlock= plantableBlocksNearWater.get(0);
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
