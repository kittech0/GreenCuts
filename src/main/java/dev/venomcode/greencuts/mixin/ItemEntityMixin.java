package dev.venomcode.greencuts.mixin;

import dev.venomcode.greencuts.GreenCuts;
import dev.venomcode.greencuts.api.GreenCutsUtils;
import dev.venomcode.greencuts.ifaces.AutoPlantable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements AutoPlantable
{
    @Shadow
    public abstract ItemStack getStack();

    @Shadow private int itemAge;

    @Inject( method = "tick", at = @At("HEAD"))
    void onTick(CallbackInfo ci)
    {
        if(!(!getWorld().isClient && GreenCuts.getConfig().getEnabled() && !triedPlanting())) return;
        if(!GreenCutsUtils.isSaplingStack(this.getStack())) return;
        if(this.itemAge < GreenCuts.getConfig().getAutoPlantTicks()) return;
        
        ServerWorld srvWorld = (ServerWorld) getWorld();
        BlockState state = Block.getBlockFromItem(this.getStack().getItem()).getDefaultState();
        
        if(!(state.getBlock().canPlaceAt(state, getWorld(), getBlockPos()) && getWorld().getBlockState(getBlockPos()).isAir())) return;
        
        setTriedPlanting(true);
        
        if (GreenCutsUtils.tryPlanting(state.getBlock(), srvWorld, this.getBlockPos())) return;
        
        if(this.getStack().getCount() > 1)
        {
            this.getStack().setCount(this.getStack().getCount() - 1);
            return;
        }
        
        this.discard();
                 
    }

    @Inject( method = "writeCustomDataToNbt", at = @At("HEAD"))
    void onWriteData(NbtCompound nbt, CallbackInfo ci)
    {
        nbt.putBoolean("greencuts_tried_plant", this.triedPlanting);
    }

    @Inject( method = "readCustomDataFromNbt", at = @At("HEAD"))
    void onReadData(NbtCompound nbt, CallbackInfo ci)
    {
        this.triedPlanting = nbt.getBoolean("greencuts_tried_plant");
    }

    @Override
    public boolean triedPlanting()
    {
        return this.triedPlanting;
    }

    @Override
    public void setTriedPlanting(boolean value)
    {
        this.triedPlanting = value;
    }

    boolean triedPlanting = false;

    public ItemEntityMixin(EntityType<?> type, World world)
    {
        super(type, world);
    }
}
