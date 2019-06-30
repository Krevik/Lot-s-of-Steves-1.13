package krevik.github.io.entity.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EntityAIHarvestFarmland extends EntityAIBase {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> plantsReadyToHarvest;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public EntityAIHarvestFarmland(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper=LotsOfSteves.getHelper();
        plantsReadyToHarvest = new ArrayList<>();
        setMutexBits(5);
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
                plantsReadyToHarvest = helper.getPlantsReadyToHarvest(NPC);
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

    }

    private int getPathTimerTimeout(){
        return 150;
    }


    @Override
    public void tick() {
        if(destinationBlock!=null){
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY()+1D,destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
            if(getIsAboveDestination()||pathTimer>=getPathTimerTimeout()){
                if(world.getBlockState(plantsReadyToHarvest.get(0).up()).getBlock() instanceof BlockCrops){
                    if(((BlockCrops)world.getBlockState(plantsReadyToHarvest.get(0).up()).getBlock()).isMaxAge(world.getBlockState(plantsReadyToHarvest.get(0).up()))){
                        if(helper.isFreeSlotInInventory(NPC.getLocalInventory())) {
                                if (world.isAirBlock(plantsReadyToHarvest.get(0).up(2))) {
                                    if (world.isAirBlock(plantsReadyToHarvest.get(0).up(3))) {
                                        world.destroyBlock(plantsReadyToHarvest.get(0).up(),true);
                                        if(helper.areAnySeedsInInventory(NPC)){
                                            ArrayList<ItemWithInventoryIndexEntry> seeds = helper.getSeedsWithInventoryIndexes(NPC);
                                            ItemWithInventoryIndexEntry seed = seeds.get(NPC.getRNG().nextInt(seeds.size()));
                                            IBlockState toPlant = helper.getBlockStateToPlant(seed.getItem());
                                            world.setBlockState(plantsReadyToHarvest.get(0).up(),toPlant,3);
                                            NPC.getLocalInventory().getStackInSlot(seed.getInventoryIndex()).shrink(1);
                                        }
                                    }
                                }
                        }
                    }
                    if(world.getBlockState(plantsReadyToHarvest.get(0).up()).getBlock() instanceof BlockBeetroot) {
                        if (((BlockBeetroot) world.getBlockState(plantsReadyToHarvest.get(0).up()).getBlock()).isMaxAge(world.getBlockState(plantsReadyToHarvest.get(0).up()))) {
                            if (helper.isFreeSlotInInventory(NPC.getLocalInventory())) {
                                if (world.isAirBlock(plantsReadyToHarvest.get(0).up(2))) {
                                    if (world.isAirBlock(plantsReadyToHarvest.get(0).up(3))) {
                                        world.destroyBlock(plantsReadyToHarvest.get(0).up(), true);
                                        if(helper.areAnySeedsInInventory(NPC)){
                                            ArrayList<ItemWithInventoryIndexEntry> seeds = helper.getSeedsWithInventoryIndexes(NPC);
                                            ItemWithInventoryIndexEntry seed = seeds.get(NPC.getRNG().nextInt(seeds.size()));
                                            IBlockState toPlant = helper.getBlockStateToPlant(seed.getItem());
                                            world.setBlockState(plantsReadyToHarvest.get(0).up(),toPlant,3);
                                            NPC.getLocalInventory().getStackInSlot(seed.getInventoryIndex()).shrink(1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                destinationBlock=null;
                pathTimer=0;
                plantsReadyToHarvest.remove(0);
            }
        }else{
            destinationBlock = plantsReadyToHarvest.get(0);
        }
    }

    public double getTargetDistanceSq() {
        return 1.25D;
    }

    protected boolean getIsAboveDestination() {
        if (this.NPC.getDistanceSqToCenter(this.destinationBlock.up()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}