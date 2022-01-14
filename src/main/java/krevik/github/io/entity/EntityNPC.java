package krevik.github.io.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class EntityNPC extends Animal {
    private SimpleContainer localInventory;
    private BlockPos workingRadius;
    private BlockPos homePosition;

    public EntityNPC(EntityType<? extends EntityNPC> entityType, Level world) {
        super(entityType, world);
        initInventory();
        setSpeed(1f);
        setCanPickUpLoot(false);
        workingRadius=new BlockPos(15,15,15);
    }

    private void initInventory(){
        localInventory = new SimpleContainer(64);
    }

    public void updateTasks(){
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor par1, DifficultyInstance par2, MobSpawnType par3, @Nullable SpawnGroupData par4, @Nullable CompoundTag par5) {
        homePosition=new BlockPos(position());
        return super.finalizeSpawn(par1, par2, par3, par4, par5);
    }

    public BlockPos getWorkingRadius(){
        return workingRadius;
    }

    @Override
    public void checkDespawn() {
        //we do not want despawning
    }

    @Override
    public void tick() {
        super.tick();
        setSpeed(1f);
    }

    @Override
    protected void registerGoals() {

    }

    public SimpleContainer getLocalInventory() {
        return this.localInventory;
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Inventory", this.localInventory.createTag());

        tag.putDouble("homeX",homePosition.getX());
        tag.putDouble("homeY",homePosition.getY());
        tag.putDouble("homeZ",homePosition.getZ());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.localInventory.fromTag(tag.getList("Inventory", 10));
        homePosition = new BlockPos(tag.getDouble("homeX"),tag.getDouble("homeY"),tag.getDouble("homeZ"));
    }



    @org.jetbrains.annotations.Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }
}