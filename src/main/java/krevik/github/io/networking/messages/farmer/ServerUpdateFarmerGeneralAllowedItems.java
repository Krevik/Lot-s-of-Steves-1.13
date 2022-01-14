package krevik.github.io.networking.messages.farmer;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Vector3d;
import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.networking.PacketsHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ServerUpdateFarmerGeneralAllowedItems  {

    private BlockPos farmerPos;
    private int stacksSize;
    private ItemStack[] stacks;
    boolean shouldUseBonemeal;
    boolean shouldUseCropRotation;
    int workSpeed;
    BlockPos workingRadius;
    public ServerUpdateFarmerGeneralAllowedItems(BlockPos pos, int workSpeed, BlockPos workingRadius, int stacksSize, ArrayList<ItemStack> stacks, boolean shouldUseBonemeal,
                                                 boolean shouldUseCropRotation){
        farmerPos=pos;
        this.workSpeed = workSpeed;
        this.stacksSize=stacksSize;
        this.stacks = new ItemStack[stacks.size()];
        for(int i=0;i<stacks.size();i++){
            this.stacks[i]=stacks.get(i);
        }
        this.shouldUseBonemeal = shouldUseBonemeal;
        this.shouldUseCropRotation = shouldUseCropRotation;
        this.workingRadius = workingRadius;
    }

    public static void encode(ServerUpdateFarmerGeneralAllowedItems msg, PacketBuffer buf)
    {
        buf.writeBlockPos(msg.farmerPos);
        buf.writeInt(msg.workSpeed);
        buf.writeBlockPos(msg.workingRadius);
        buf.writeInt(msg.stacksSize);
        for(int i=1; i<=msg.stacksSize;i++){
            buf.writeItemStack(msg.stacks[i-1]);
        }
        buf.writeBoolean(msg.shouldUseBonemeal);
        buf.writeBoolean(msg.shouldUseCropRotation);
    }

    public static ServerUpdateFarmerGeneralAllowedItems decode(PacketBuffer buf)
    {
        net.minecraft.util.math.BlockPos pos = buf.readBlockPos();
        int workSpeed = buf.readInt();
        net.minecraft.util.math.BlockPos workingRadius = buf.readBlockPos();
        int stacksSize = buf.readInt();
        ItemStack[] stacks = new ItemStack[stacksSize];
        for(int i=1;i<=stacksSize;i++){
            stacks[i-1]=buf.readItemStack();
        }

        return new ServerUpdateFarmerGeneralAllowedItems(pos,workSpeed,workingRadius,stacksSize,new ArrayList<>(ImmutableList.copyOf(stacks)), buf.readBoolean(), buf.readBoolean());
    }

    public static class Handler
    {
        public static void handle(final ServerUpdateFarmerGeneralAllowedItems message, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> {
                BlockPos clickPos = message.farmerPos;
                int searchRadius = 5;
                List<Entity> entities = ctx.get().getSender().getEntity().getEntityWorld().getEntitiesWithinAABB(EntityAutoFarmer.class,new AxisAlignedBB(clickPos.getX()-searchRadius,clickPos.getY()-searchRadius,clickPos.getZ()-searchRadius,clickPos.getX()+searchRadius,clickPos.getY()+searchRadius,clickPos.getZ()+searchRadius));
                if(entities!=null){
                    if(!entities.isEmpty()){
                        Entity closestEntity=entities.get(0);
                        for(Entity entity:entities){
                            if(entity instanceof EntityAutoFarmer){
                                Vector3d closestEntityPos = new Vector3d(closestEntity.getPosX(),closestEntity.getPosY(),closestEntity.getPosZ());
                                Vector3d entityPos = new Vector3d(entity.getPosX(),entity.getPosY(),entity.getPosZ());
                                Vector3d clickPosVec = new Vector3d(clickPos.getX(),clickPos.getY(),clickPos.getZ());
                                if(entityPos.distanceTo(clickPosVec) <= closestEntityPos.distanceTo(clickPosVec)){
                                    closestEntity = entity;
                                }
                            }
                        }
                        if(closestEntity instanceof EntityAutoFarmer){
                            EntityAutoFarmer farmer = (EntityAutoFarmer) closestEntity;
                            farmer.setGENERAL_ALLOWED_ITEMS(message.stacks);
                            farmer.updateTasks();
                            farmer.setShouldUseBonemeal(message.shouldUseBonemeal);
                            farmer.setCropRotation(message.shouldUseCropRotation);
                            farmer.setWorkSpeed(message.workSpeed);
                            farmer.setWorkingRadius(message.workingRadius);
                        }
                    }
                }
                //update the same thing on all the clients
                PacketsHandler.sendToAll(new ClientUpdateFarmerGeneralAllowedItems(message.farmerPos,message.workSpeed,message.workingRadius,message.stacksSize,new ArrayList<>(ImmutableList.copyOf(message.stacks)),message.shouldUseBonemeal,message.shouldUseCropRotation));
                return;
            });
            ctx.get().setPacketHandled(true);
        }
    }
}