package krevik.github.io.entity.AI.fisherman;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoLumberjack;
import krevik.github.io.entity.EntityFisherman;
import krevik.github.io.util.FunctionHelper;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.AxeItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;

public class GoalLookForRod extends Goal {

    private int runDelay;
    private int actualDelay;
    private EntityFisherman NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> chestsWithRod;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public GoalLookForRod(EntityFisherman npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper=LotsOfSteves.getHelper();
        chestsWithRod =new ArrayList<>();
        this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            if(!helper.isRodInInventory(NPC)&& !helper.isRodEquipped(NPC) && helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                chestsWithRod = helper.getChestPosesWithRods(NPC);
                if(!chestsWithRod.isEmpty()){
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
        return !chestsWithRod.isEmpty()&&helper.isFreeSlotInInventory(NPC.getLocalInventory());
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
            NPC.setWhatIAmActuallyDoing("Going for a rod! YAY!");
            NPC.getNavigator().tryMoveToXYZ(destinationBlock.getX()+0.5D,destinationBlock.getY()+1D,destinationBlock.getZ()+0.5D,NPC.getAIMoveSpeed());
            pathTimer++;
            if(getIsAboveDestination()||pathTimer>=getPathTimerTimeout()){
                if(world.getTileEntity(chestsWithRod.get(0))!=null){
                    if(world.getTileEntity(chestsWithRod.get(0)) instanceof ChestTileEntity && helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                        ChestTileEntity chest = (ChestTileEntity) world.getTileEntity(chestsWithRod.get(0));
                        for(int c=0;c<16;c++){
                            if(!chest.getStackInSlot(c).isEmpty()){
                                if(chest.getStackInSlot(c).getItem() instanceof FishingRodItem){
                                    ItemStack stackToTransfer = chest.getStackInSlot(c);
                                    NPC.getLocalInventory().addItem(stackToTransfer);
                                    chest.getStackInSlot(c).setCount(0);
                                }
                            }
                        }

                    }
                }

                destinationBlock=null;
                pathTimer=0;
                chestsWithRod.remove(0);
            }
        }else{
            destinationBlock = chestsWithRod.get(0);
        }
    }

    public double getTargetDistanceSq() {
        return 6D;
    }

    protected boolean getIsAboveDestination() {
        if (this.NPC.getDistanceSq(this.destinationBlock.up().getX(),destinationBlock.up().getY(),destinationBlock.up().getZ()) > this.getTargetDistanceSq()) {
            return false;
        } else {
            return true;
        }
    }

}
