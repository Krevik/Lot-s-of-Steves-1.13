package krevik.github.io.entity.AI.lumberjack;

import krevik.github.io.LotsOfSteves;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.entity.EntityAutoLumberjack;
import krevik.github.io.util.FunctionHelper;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EntityAILookForTools extends EntityAIBase {

    private int runDelay;
    private int actualDelay;
    private EntityAutoLumberjack NPC;
    FunctionHelper helper;
    ArrayList<BlockPos> chestsWithTools;
    BlockPos destinationBlock;
    int pathTimer;
    World world;
    public EntityAILookForTools(EntityAutoLumberjack npc){
        NPC=npc;
        runDelay=100;
        actualDelay=0;
        helper=LotsOfSteves.getHelper();
        chestsWithTools =new ArrayList<>();
        setMutexBits(5);
        destinationBlock=null;
        pathTimer=0;
        world=npc.getEntityWorld();
    }

    @Override
    public boolean shouldExecute() {
        actualDelay++;
        if(actualDelay>=runDelay){
            if(!helper.areToolsInInventory(NPC) && helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                chestsWithTools = helper.getChestPosesWithTools(NPC);
                if(!chestsWithTools.isEmpty()){
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
        return !chestsWithTools.isEmpty()&&helper.isFreeSlotInInventory(NPC.getLocalInventory());
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
                if(world.getTileEntity(chestsWithTools.get(0))!=null){
                    if(world.getTileEntity(chestsWithTools.get(0)) instanceof TileEntityChest && helper.isFreeSlotInInventory(NPC.getLocalInventory())){
                        TileEntityChest chest = (TileEntityChest) world.getTileEntity(chestsWithTools.get(0));
                        for(int c=0;c<16;c++){
                            if(!chest.getStackInSlot(c).isEmpty()){
                                if(chest.getStackInSlot(c).getItem() instanceof ItemAxe){
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
                chestsWithTools.remove(0);
            }
        }else{
            destinationBlock = chestsWithTools.get(0);
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
