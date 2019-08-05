package krevik.github.io.block;

import krevik.github.io.entity.EntityAutoFarmer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAutoFarmerSpawner extends Block {
    public BlockAutoFarmerSpawner(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn,pos,state,player);
        if(!worldIn.isRemote()){
            LivingEntity entity = new EntityAutoFarmer(worldIn);
            entity.setPosition(pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f);
            worldIn.addEntity(entity);
        }
    }
}
