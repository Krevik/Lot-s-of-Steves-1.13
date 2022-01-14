package krevik.github.io.block;

import krevik.github.io.entity.EntityAutoFarmer;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockAutoFarmerSpawner extends Block {
    public BlockAutoFarmerSpawner(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    public void playerDestroy(Level level, Player player, BlockPos blockpos, BlockState state, @Nullable BlockEntity p_49831_, ItemStack stack) {
        super.playerDestroy(level,player,blockpos,state,p_49831_,stack);
        if(!level.isClientSide()){
            LivingEntity entity = new EntityAutoFarmer(level);
            entity.setPos(blockpos.getX()+0.5f,blockpos.getY()+0.5f,blockpos.getZ()+0.5f);
            worldIn.addEntity(entity);
            ((EntityAutoFarmer) entity).setHomePosAndDistance(new BlockPos(entity),15);
            ((EntityAutoFarmer) entity).setOwnerId(player.getUniqueID());
        }
    }
}
