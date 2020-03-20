package krevik.github.io.entity;

import krevik.github.io.init.ModEntities;
import krevik.github.io.util.NPCUtils.AI.animal_farmer.AIScanForAnimals;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class EntityAnimalFarmer extends EntityNPC{
    protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(TameableEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    private Inventory localInventory;
    private ArrayList<Item> GENERAL_ALLOWED_ITEMS;
    private ArrayList<LivingEntity> scanned_Living_Entities;
    private ArrayList<LivingEntity> allowed_entities;
    private BlockPos workingRadius;
    private int workSpeed = 1;


    private AIScanForAnimals scanForAnimalsGoal = new AIScanForAnimals(this);
    private void initGoals(){
        this.goalSelector.addGoal(5,scanForAnimalsGoal);
    }

    public void updateTasks(){

    }


    public EntityAnimalFarmer(World world) {
        super(ModEntities.AUTO_FARMER, world);
        workSpeed=1;
        initInventory();
        initLists();
        initGoals();
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
        workingRadius=new BlockPos(15,15,15);
    }

    public EntityAnimalFarmer(EntityType<EntityAnimalFarmer> entityAutoFarmerEntityType, World world) {
        super(entityAutoFarmerEntityType, world);
        workSpeed=1;
        initInventory();
        initLists();
        initGoals();
        setAIMoveSpeed(1f);
        setCanPickUpLoot(false);
        workingRadius=new BlockPos(15,15,15);
        workSpeed=1;
    }

    private void initLists(){
        if(GENERAL_ALLOWED_ITEMS==null) {
            GENERAL_ALLOWED_ITEMS = new ArrayList<>();
        }
        if(scanned_Living_Entities==null) {
            scanned_Living_Entities = new ArrayList<>();
        }
        if(allowed_entities==null) {
            allowed_entities = new ArrayList<>();
        }
    }

    public ArrayList<LivingEntity> getScanned_Living_Entities() {
        return scanned_Living_Entities;
    }

    public void setScanned_Living_Entities(ArrayList<LivingEntity> scanned_Living_Entities) {
        this.scanned_Living_Entities = scanned_Living_Entities;
    }

    public ArrayList<LivingEntity> getAllowed_entities() {
        return allowed_entities;
    }

    public void setAllowed_entities(ArrayList<LivingEntity> allowed_entities) {
        this.allowed_entities = allowed_entities;
    }

    private void initInventory(){
        localInventory = new Inventory(64);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
    }

    public void setWorkingRadius(BlockPos workingRadius) {
        this.workingRadius = workingRadius;
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vec3d vec, Hand hand) {
        if(!world.isRemote) {
            if(isOwner(player)) {
                if (player instanceof ServerPlayerEntity) {
                    ItemStack stacksToAllow[] = new ItemStack[getGENERAL_ALLOWED_ITEMS().size()];
                    for(int c=0;c<getGENERAL_ALLOWED_ITEMS().size();c++){
                        stacksToAllow[c]=new ItemStack(getGENERAL_ALLOWED_ITEMS().get(c));
                    }
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    public ArrayList<Item> getGENERAL_ALLOWED_ITEMS() {
        return GENERAL_ALLOWED_ITEMS;
    }

    public int getWorkSpeed() {
        return workSpeed;
    }

    public void setWorkSpeed(int workSpeed) {
        this.workSpeed = workSpeed;
    }

    public void setGENERAL_ALLOWED_ITEMS(ItemStack... stacks) {
        ArrayList<Item> items = new ArrayList<>();
        GENERAL_ALLOWED_ITEMS.clear();
        for(ItemStack stack: stacks){
            items.add(stack.getItem());
        }
        this.GENERAL_ALLOWED_ITEMS = items;
    }


    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        for(int c=0;c<=getLocalInventory().getSizeInventory();c++){
            if(!getLocalInventory().getStackInSlot(c).isEmpty()){
                ItemEntity itemEntity = new ItemEntity(world,getPosition().getY(),getPosition().getY(),getPosition().getZ());
                itemEntity.setItem(getLocalInventory().getStackInSlot(c));
                itemEntity.setPosition(getPosition().getX()+0.5,getPosition().getY()+2,getPosition().getZ()+0.5);
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

    public BlockPos getWorkingRadius(){
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

    @Nullable
    public UUID getOwnerId() {
        return this.dataManager.get(OWNER_UNIQUE_ID).orElse((UUID)null);
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : this.world.getPlayerByUuid(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public boolean isOwner(LivingEntity entityIn) {
        return entityIn == this.getOwner();
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
        tag.put("Inventory", nbttaglist);

        ListNBT nbttaglist2 = new ListNBT();
        for(int i = 0; i < this.GENERAL_ALLOWED_ITEMS.size(); ++i) {
            ItemStack itemstack = new ItemStack(this.GENERAL_ALLOWED_ITEMS.get(i));
            if (!itemstack.isEmpty()) {
                nbttaglist2.add(itemstack.write(new CompoundNBT()));
            }
        }
        tag.put("general_allowed_items", nbttaglist2);

        tag.putDouble("homeX",getHomePosition().getX());
        tag.putDouble("homeY",getHomePosition().getY());
        tag.putDouble("homeZ",getHomePosition().getZ());

        if (this.getOwnerId() == null) {
            tag.putString("OwnerUUID", "");
        } else {
            tag.putString("OwnerUUID", this.getOwnerId().toString());
        }
        tag.putInt("work_speed",getWorkSpeed());
        tag.putInt("working_radius_x",getWorkingRadius().getX());
        tag.putInt("working_radius_y",getWorkingRadius().getY());
        tag.putInt("working_radius_z",getWorkingRadius().getZ());

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

        ListNBT nbttaglist2 = tag.getList("general_allowed_items", 10);
        this.GENERAL_ALLOWED_ITEMS = new ArrayList<>();
        for(int i = 0; i < nbttaglist2.size(); ++i) {
            ItemStack itemstack = ItemStack.read(nbttaglist2.getCompound(i));
            if (!itemstack.isEmpty()) {
                if(!this.GENERAL_ALLOWED_ITEMS.contains(itemstack.getItem())){
                    this.GENERAL_ALLOWED_ITEMS.add(itemstack.getItem());
                }
            }
        }

        setHomePosAndDistance(new net.minecraft.util.math.BlockPos(tag.getDouble("homeX"),tag.getDouble("homeY"),tag.getDouble("homeZ")),30);

        String s;
        if (tag.contains("OwnerUUID", 8)) {
            s = tag.getString("OwnerUUID");
        } else {
            String s1 = tag.getString("Owner");
            s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
        }

        if (!s.isEmpty()) {
            try {
                this.setOwnerId(UUID.fromString(s));
            } catch (Throwable var4) {
            }
        }
        setWorkSpeed(tag.getInt("work_speed"));
        setWorkingRadius(new BlockPos(tag.getInt("working_radius_x"),tag.getInt("working_radius_y"),tag.getInt("working_radius_z")));
    }






}

