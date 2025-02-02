package dev.venomcode.greencuts.api;

import dev.venomcode.greencuts.GreenCuts;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class GreenCutsUtils
{
    public static boolean isSaplingStack(ItemStack item)
    {
        return Block.getBlockFromItem(item.getItem()) instanceof SaplingBlock;
    }

    public static boolean tryPlanting(Block block, ServerWorld world, BlockPos pos)
    {
        BlockState state = block.getDefaultState();
        int randInt = world.random.nextInt(101);
        int plantChance = GreenCuts.getConfig().getAutoPlantChance();
        
        if(plantChance >= 100) return true;
        if(randInt >= plantChance) return false;

        world.setBlockState(pos, state);
        return true;
    }
}

