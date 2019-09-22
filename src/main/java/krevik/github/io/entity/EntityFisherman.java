package krevik.github.io.entity;

import com.google.common.collect.ImmutableSet;
import krevik.github.io.entity.AI.fisherman.EntityAIEquipRod;
import krevik.github.io.entity.AI.fisherman.GoalCastARod;
import krevik.github.io.entity.AI.fisherman.GoalGoNearWater;
import krevik.github.io.entity.AI.fisherman.GoalLookForRod;
import krevik.github.io.entity.AI.lumberjack.*;
import krevik.github.io.init.ModEntities;
import krevik.github.io.init.ModItems;
import krevik.github.io.util.WorkingRadius;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class EntityFisherman extends AnimalEntity {

    private final Inventory localInventory = new Inventory(64);
    private WorkingRadius workingRadius;
    private String whatIAmActuallyDoing;
    @Nullable
    public EntityCustomFishingBobber fishingBobber;

    public EntityFisherman(World p_i48568_2_) {
        super((EntityType<? extends AnimalEntity>) ModEntities.ENTITY_FISHERMAN, p_i48568_2_);
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
        workingRadius=new WorkingRadius(15,15,15);
    }

    public EntityFisherman(EntityType<EntityFisherman> entityAutoLumberjackEntityType, World world) {
        super(entityAutoLumberjackEntityType, world);
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
        workingRadius=new WorkingRadius(15,15,15);
    }


    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    public WorkingRadius getWorkingRadius(){
        return workingRadius;
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3,new GoalLookForRod(this));
        goalSelector.addGoal(3,new EntityAIEquipRod(this));
        goalSelector.addGoal(4,new GoalGoNearWater(this));
        goalSelector.addGoal(4,new GoalCastARod(this));
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        for(int c=0;c<=getLocalInventory().getSizeInventory();c++){
            if(!getLocalInventory().getStackInSlot(c).isEmpty()){
                ItemEntity itemEntity = new ItemEntity(world,posX,posY,posZ);
                itemEntity.setItem(getLocalInventory().getStackInSlot(c));
                itemEntity.setPosition(posX+0.5,posY+2,posZ+0.5);
                itemEntity.setMotion(new Vec3d(-0.5+getRNG().nextFloat(),-0.1f,-0.5+getRNG().nextFloat()));
                world.addEntity(itemEntity);
            }
        }
    }

    public String getWhatIAmActuallyDoing() {
        return whatIAmActuallyDoing;
    }

    public void setWhatIAmActuallyDoing(String text){
        whatIAmActuallyDoing = text;
    }

    @Override
    public void tick() {
        super.tick();
        setAIMoveSpeed(1f);
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public Inventory getLocalInventory() {
        return this.localInventory;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3000000417232513D);
    }

    @Nullable
    @Override
    public AgeableEntity createChild(AgeableEntity entityAgeable) {
        return null;
    }


    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        ListNBT nbttaglist = new ListNBT();
        for(int i = 0; i < this.localInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.localInventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                nbttaglist.add(itemstack.write(new CompoundNBT()));
            }
        }
        tag.putDouble("homeX",getHomePosition().getX());
        tag.putDouble("homeY",getHomePosition().getY());
        tag.putDouble("homeZ",getHomePosition().getZ());
        tag.put("Inventory", nbttaglist);
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);

        ListNBT nbttaglist = tag.getList("Inventory", 10);

        for(int i = 0; i < nbttaglist.size(); ++i) {
            ItemStack itemstack = ItemStack.read(nbttaglist.getCompound(i));
            if (!itemstack.isEmpty()) {
                this.localInventory.addItem(itemstack);
            }
        }
        setHomePosAndDistance(new BlockPos(tag.getDouble("homeX"),tag.getDouble("homeY"),tag.getDouble("homeZ")),15);
    }

    @Override
    protected void registerData() {
        super.registerData();
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
        return ActionResultType.SUCCESS;
    }
}
