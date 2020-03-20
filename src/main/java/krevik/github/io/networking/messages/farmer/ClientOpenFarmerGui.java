package krevik.github.io.networking.messages.farmer;

import krevik.github.io.client.gui.GuiFarmer;
import krevik.github.io.entity.EntityAutoFarmer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class ClientOpenFarmerGui  {

    private BlockPos posClicked;
    private int workSpeed;
    private ItemStack[] allowed_Items;
    private BlockPos workingRadius;

    public ClientOpenFarmerGui(BlockPos clickedAt,int workSpeed,BlockPos workingRadius,ItemStack... stacks){
       posClicked=clickedAt;
       this.workSpeed=workSpeed;
       allowed_Items = stacks;
       this.workingRadius=workingRadius;
    }

    public static void encode(ClientOpenFarmerGui msg, PacketBuffer buf)
    {
        buf.writeBlockPos(msg.posClicked);
        buf.writeInt(msg.workSpeed);
        buf.writeBlockPos(msg.workingRadius);
        buf.writeInt(msg.allowed_Items.length);
        for(int i=0;i<msg.allowed_Items.length;i++){
            buf.writeItemStack(msg.allowed_Items[i]);
        }
    }

    public static ClientOpenFarmerGui decode(PacketBuffer buf)
    {
        BlockPos pos = buf.readBlockPos();
        int workSpeed = buf.readInt();
        BlockPos workingRadius = buf.readBlockPos();
        int stacksSize = buf.readInt();
        ItemStack[] stacks = new ItemStack[stacksSize];
        for(int i=0;i<stacksSize;i++){
            stacks[i] = buf.readItemStack();
        }
        return new ClientOpenFarmerGui(pos,workSpeed,workingRadius,stacks);
    }

    public static class Handler
    {
        public static void handle(final ClientOpenFarmerGui message, Supplier<NetworkEvent.Context> ctx)
        {
            if(ctx.get().getDirection()== NetworkDirection.PLAY_TO_CLIENT){
                BlockPos clickPos = message.posClicked;
                int searchRadius = 5;

                List<Entity> entities = Minecraft.getInstance().player.getEntityWorld().getEntitiesWithinAABB(EntityAutoFarmer.class,new AxisAlignedBB(clickPos.getX()-searchRadius,clickPos.getY()-searchRadius,clickPos.getZ()-searchRadius,clickPos.getX()+searchRadius,clickPos.getY()+searchRadius,clickPos.getZ()+searchRadius));
                if(entities!=null){
                    if(!entities.isEmpty()){
                        Entity closestEntity=entities.get(0);
                        for(Entity entity:entities){
                            if(entity instanceof EntityAutoFarmer){
                                Vec3d closestEntityPos = new Vec3d(closestEntity.getPosX(),closestEntity.getPosY(),closestEntity.getPosZ());
                                Vec3d entityPos = new Vec3d(entity.getPosX(),entity.getPosY(),entity.getPosZ());
                                Vec3d clickPosVec = new Vec3d(clickPos.getX(),clickPos.getY(),clickPos.getZ());
                                if(entityPos.distanceTo(clickPosVec) <= closestEntityPos.distanceTo(clickPosVec)){
                                    closestEntity = entity;
                                }
                            }
                        }
                        if(closestEntity instanceof EntityAutoFarmer){
                            EntityAutoFarmer farmer = (EntityAutoFarmer) closestEntity;
                            DistExecutor.runWhenOn(Dist.CLIENT,
                                    ()-> (Runnable) () -> Minecraft.getInstance().displayGuiScreen(new GuiFarmer(ctx.get().getSender(),farmer,message.workSpeed,message.workingRadius, message.allowed_Items)));
                        }
                    }
                }

            }
            ctx.get().setPacketHandled(true);
        }
    }
}

