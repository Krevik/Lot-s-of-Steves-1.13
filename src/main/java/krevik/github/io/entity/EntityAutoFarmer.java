package krevik.github.io.entity;

import com.google.common.collect.ImmutableSet;
import krevik.github.io.entity.AI.farmer.*;
import krevik.github.io.init.ModEntities;
import krevik.github.io.init.ModItems;
import krevik.github.io.util.WorkingRadius;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
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

public class EntityAutoFarmer extends AnimalEntity {

    private final Inventory localInventory = new Inventory(64);
    private static final DataParameter<Integer> MODE = EntityDataManager.createKey(EntityAutoFarmer.class, DataSerializers.VARINT);

    public ArrayList<Block> CROPS_ALLOWED = new ArrayList<>();
    public ArrayList<Item> ITEMS_OF_INTEREST = new ArrayList<>();
    public ArrayList<Item> SEEDS = new ArrayList<>();
    private WorkingRadius workingRadius;

    public EntityAutoFarmer(World world) {
        super((EntityType<? extends AnimalEntity>) ModEntities.ENTITY_AUTO_FARMER, world);
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
        workingRadius=new WorkingRadius(15,15,15);
        SEEDS.addAll(ImmutableSet.of(Items.WHEAT_SEEDS, Items.CARROT,
                Items.POTATO, Items.BEETROOT_SEEDS));
        ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.WHEAT,Items.BONE_MEAL, Items.WHEAT_SEEDS, Items.CARROT,
                Items.POTATO, Items.BEETROOT, Items.BEETROOT_SEEDS));
        CROPS_ALLOWED.addAll(ImmutableSet.of(Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES,
                Blocks.BEETROOTS));
    }

    public EntityAutoFarmer(EntityType<EntityAutoFarmer> entityAutoFarmerEntityType, World world) {
        super(entityAutoFarmerEntityType, world);
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
        workingRadius=new WorkingRadius(15,15,15);
        SEEDS.addAll(ImmutableSet.of(Items.WHEAT_SEEDS, Items.CARROT,
                Items.POTATO, Items.BEETROOT_SEEDS));
        ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.WHEAT,Items.BONE_MEAL, Items.WHEAT_SEEDS, Items.CARROT,
                Items.POTATO, Items.BEETROOT, Items.BEETROOT_SEEDS));
        CROPS_ALLOWED.addAll(ImmutableSet.of(Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES,
                Blocks.BEETROOTS));
    }

    public ItemStack getItemStackToRender(int mode){
        ItemStack result = new ItemStack(Items.WHEAT);
        if(mode==0) result = new ItemStack(ModItems.MULTI_CROP);
        if(mode==1) result = new ItemStack(Items.WHEAT);
        if(mode==2) result = new ItemStack(Items.CARROT);
        if(mode==3) result = new ItemStack(Items.POTATO);
        if(mode==4) result = new ItemStack(Items.BEETROOT);
        return result;
    }

    private void handleOperatableItems(){
        int mode = getMode();
        SEEDS.clear();
        ITEMS_OF_INTEREST.clear();
        CROPS_ALLOWED.clear();
        if(mode==0){
            SEEDS.addAll(ImmutableSet.of(Items.WHEAT_SEEDS, Items.CARROT,
                    Items.POTATO, Items.BEETROOT_SEEDS));
            ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.WHEAT,Items.BONE_MEAL, Items.WHEAT_SEEDS, Items.CARROT,
                    Items.POTATO, Items.BEETROOT, Items.BEETROOT_SEEDS));
            CROPS_ALLOWED.addAll(ImmutableSet.of(Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES,
                    Blocks.BEETROOTS));
        }
        if(mode==1){
            SEEDS.addAll(ImmutableSet.of(Items.WHEAT_SEEDS));
            ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.WHEAT,Items.BONE_MEAL, Items.WHEAT_SEEDS));
            CROPS_ALLOWED.addAll(ImmutableSet.of(Blocks.WHEAT));
        }
        if(mode==2){
            SEEDS.addAll(ImmutableSet.of(Items.CARROT));
            ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.BONE_MEAL, Items.CARROT));
            CROPS_ALLOWED.addAll(ImmutableSet.of(Blocks.CARROTS));
        }
        if(mode==3){
            SEEDS.addAll(ImmutableSet.of(Items.POTATO));
            ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.BONE_MEAL, Items.POTATO, Items.POISONOUS_POTATO));
            CROPS_ALLOWED.addAll(ImmutableSet.of(Blocks.POTATOES));
        }
        if(mode==4){
            SEEDS.addAll(ImmutableSet.of(Items.BEETROOT_SEEDS));
            ITEMS_OF_INTEREST.addAll(ImmutableSet.of(Items.BONE_MEAL, Items.BEETROOT, Items.BEETROOT_SEEDS));
            CROPS_ALLOWED.addAll(ImmutableSet.of(Blocks.BEETROOTS));
        }
    }


    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(MODE, 0);
    }

    public int getMode()
    {
        return MathHelper.clamp(getDataManager().get(MODE), 0, 16);
    }

    public void setMode(int meta) {
        getDataManager().set(MODE, meta);
        handleOperatableItems();
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
        if(!world.isRemote) {
            if (getMode() < 4) {
                setMode(getMode() + 1);
            } else {
                setMode(0);
            }
        }
        return ActionResultType.SUCCESS;
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

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        setHomePosAndDistance(getPosition(),30);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public WorkingRadius getWorkingRadius(){
        return workingRadius;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        setAIMoveSpeed(1f);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(5,new EntityAILookForSeeds(this));
        goalSelector.addGoal(5,new EntityAILookForBonemeal(this));
        goalSelector.addGoal(5,new EntityAIPlantSeeds(this));
        goalSelector.addGoal(6,new EntityAIFertilizePlants(this));
        goalSelector.addGoal(5,new EntityAIHarvestFarmland(this));
        goalSelector.addGoal(5,new AIFarmerCollectLoot(this));
        goalSelector.addGoal(6,new EntityAIDeliverExcessToChest(this));
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
        tag.putInt("Mode", getMode());
        ListNBT nbttaglist = new ListNBT();
        for(int i = 0; i < this.localInventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.localInventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                nbttaglist.add(itemstack.write(new CompoundNBT()));
            }
        }
        tag.put("Inventory", nbttaglist);

        tag.putDouble("homeX",getHomePosition().getX());
        tag.putDouble("homeY",getHomePosition().getY());
        tag.putDouble("homeZ",getHomePosition().getZ());
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        setMode(tag.getInt("Mode"));
        ListNBT nbttaglist = tag.getList("Inventory", 10);

        for(int i = 0; i < nbttaglist.size(); ++i) {
            ItemStack itemstack = ItemStack.read(nbttaglist.getCompound(i));
            if (!itemstack.isEmpty()) {
                this.localInventory.addItem(itemstack);
            }
        }
        setHomePosAndDistance(new BlockPos(tag.getDouble("homeX"),tag.getDouble("homeY"),tag.getDouble("homeZ")),30);
    }
}
