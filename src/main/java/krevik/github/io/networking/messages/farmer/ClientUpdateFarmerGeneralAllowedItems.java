package krevik.github.io.networking.messages.farmer;

import com.google.common.collect.ImmutableList;
import krevik.github.io.entity.EntityAutoFarmer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ClientUpdateFarmerGeneralAllowedItems  {

    private net.minecraft.util.math.BlockPos farmerPos;
    private int stacksSize;
    private ItemStack[] stacks;
    boolean shouldUseBonemeal;
    boolean shouldUseCropRotation;
    int workSpeed;
    net.minecraft.util.math.BlockPos workingRadius;
    public ClientUpdateFarmerGeneralAllowedItems(net.minecraft.util.math.BlockPos pos, int workSpeed, net.minecraft.util.math.BlockPos workingRadius, int stacksSize, ArrayList<ItemStack> stacks, boolean shouldUseBonemeal,
                                                 boolean shouldUseCropRotation){
        farmerPos=pos;
        this.workSpeed=workSpeed;
        this.stacksSize=stacksSize;
        this.stacks = new ItemStack[stacks.size()];
        for(int i=0;i<stacks.size();i++){
            this.stacks[i]=stacks.get(i);
        }
        this.shouldUseBonemeal = shouldUseBonemeal;
        this.shouldUseCropRotation = shouldUseCropRotation;
        this.workingRadius=workingRadius;
    }

    public static void encode(ClientUpdateFarmerGeneralAllowedItems msg, PacketBuffer buf)
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

    public static ClientUpdateFarmerGeneralAllowedItems decode(PacketBuffer buf)
    {
        net.minecraft.util.math.BlockPos pos = buf.readBlockPos();
        int workSpeed = buf.readInt();
        net.minecraft.util.math.BlockPos workingRadius = buf.readBlockPos();
        int stacksSize = buf.readInt();
        ItemStack[] stacks = new ItemStack[stacksSize];
        for(int i=1;i<=stacksSize;i++){
            stacks[i-1]=buf.readItemStack();
        }
        return new ClientUpdateFarmerGeneralAllowedItems(pos,workSpeed,workingRadius,stacksSize,new ArrayList<>(ImmutableList.copyOf(stacks)), buf.readBoolean(), buf.readBoolean());
    }

    public static class Handler
    {
        public static void handle(final ClientUpdateFarmerGeneralAllowedItems message, Supplier<NetworkEvent.Context> ctx)
        {
            if(ctx.get().getDirection()== NetworkDirection.PLAY_TO_CLIENT) {

                DistExecutor.runWhenOn(Dist.CLIENT,
                        ()-> (Runnable) () -> {
                    net.minecraft.util.math.BlockPos clickPos = message.farmerPos;
                    int searchRadius = 5;
                    List<Entity> entities = Minecraft.getInstance().player.getEntityWorld().getEntitiesWithinAABB(EntityAutoFarmer.class, new AxisAlignedBB(clickPos.getX() - searchRadius, clickPos.getY() - searchRadius, clickPos.getZ() - searchRadius, clickPos.getX() + searchRadius, clickPos.getY() + searchRadius, clickPos.getZ() + searchRadius));
                    if (entities != null) {
                        if (!entities.isEmpty()) {
                            Entity closestEntity = entities.get(0);
                            for (Entity entity : entities) {
                                if (entity instanceof EntityAutoFarmer) {
                                    Vec3d closestEntityPos = new Vec3d(closestEntity.getPosX(), closestEntity.getPosY(), closestEntity.getPosZ());
                                    Vec3d entityPos = new Vec3d(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                                    Vec3d clickPosVec = new Vec3d(clickPos.getX(), clickPos.getY(), clickPos.getZ());
                                    if (entityPos.distanceTo(clickPosVec) <= closestEntityPos.distanceTo(clickPosVec)) {
                                        closestEntity = entity;
                                    }
                                }
                            }
                            if (closestEntity instanceof EntityAutoFarmer) {
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
                    return;
                });
            }
            ctx.get().setPacketHandled(true);
        }
    }
}