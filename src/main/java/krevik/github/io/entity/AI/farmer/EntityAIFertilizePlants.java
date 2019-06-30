package krevik.github.io.entity.AI.farmer;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.util.FunctionHelper;
import krevik.github.io.util.ItemWithInventoryIndexEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EntityAIFertilizePlants extends EntityAIBase {

    private int runDelay;
    private int actualDelay;
    private EntityAutoFarmer NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> FertilizablePlants;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public EntityAIFertilizePlants(EntityAutoFarmer npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper=LotsOfSteves.getHelper();
        FertilizablePlants = new ArrayList<>();
        setMutexBits(5);
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            if(helper.isBoneMealInInventory(NPC)){
                FertilizablePlants = helper.getFertilizablePlants(NPC);
                if(!FertilizablePlants.isEmpty()){
                    return true;
                }
            }
            actualDelay=0;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        actualDelay=0;
        return !FertilizablePlants.isEmpty()&&helper.isBoneMealInInventory(NPC);
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
                if(world.getBlockState(FertilizablePlants.get(0).up()).has(BlockCrops.AGE)){
                    if(world.getBlockState(FertilizablePlants.get(0).up()).get(BlockCrops.AGE)<7){
                        if(helper.isBoneMealInInventory(NPC)) {
                            ArrayList<ItemWithInventoryIndexEntry> bonemealEntries = helper.getBoneMealWithIndexesInInventory(NPC.getLocalInventory(), NPC);
                            if (!bonemealEntries.isEmpty()) {
                                NPC.setHeldItem(EnumHand.MAIN_HAND,new ItemStack(Items.BONE_MEAL,1));
                                NPC.swingArm(EnumHand.MAIN_HAND);
                                ItemWithInventoryIndexEntry itemEntry = bonemealEntries.get(NPC.getRNG().nextInt(bonemealEntries.size()));
                                if (world.isAirBlock(FertilizablePlants.get(0).up(2))) {
                                    if (world.isAirBlock(FertilizablePlants.get(0).up(3))) {
                                        ((BlockCrops) world.getBlockState(FertilizablePlants.get(0).up()).getBlock()).grow(world, FertilizablePlants.get(0).up(), world.getBlockState(FertilizablePlants.get(0).up()));
                                        NPC.getLocalInventory().getStackInSlot(itemEntry.getInventoryIndex()).shrink(1);
                                    }
                                }
                                NPC.setHeldItem(EnumHand.MAIN_HAND,ItemStack.EMPTY);
                            }
                        }
                    }
                }

                destinationBlock=null;
                pathTimer=0;
                FertilizablePlants.remove(0);
            }
        }else{
            destinationBlock = FertilizablePlants.get(0);
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
