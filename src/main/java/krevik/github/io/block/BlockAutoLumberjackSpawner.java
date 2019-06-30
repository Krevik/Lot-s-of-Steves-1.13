package krevik.github.io.block;

import krevik.github.io.entity.EntityAutoFarmer;
import krevik.github.io.entity.EntityAutoLumberjack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockAutoLumberjackSpawner extends Block {
    public BlockAutoLumberjackSpawner(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(worldIn,pos,state,player);
        if(!worldIn.isRemote()){
            EntityLivingBase entity = new EntityAutoLumberjack(worldIn);
            entity.setPosition(pos.getX()+0.5f,pos.getY()+0.5f,pos.getZ()+0.5f);
            worldIn.spawnEntity(entity);
        }
    }
}

