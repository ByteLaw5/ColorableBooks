package com.bytelaw.common.registry;

import com.bytelaw.common.ClientHandlers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ColoringTableTile extends TileEntity implements INamedContainerProvider {
    private int color = 0;
    private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(this::createItems);
    private IFormattableTextComponent customName;

    public ColoringTableTile() {
        super(RegistryList.coloring_table_te);
    }

    @Override
    public ITextComponent getDisplayName() {
        return !hasCustomName() ? new TranslationTextComponent("colorablebooks.coloring_table.name") : customName;
    }

    public boolean hasCustomName() {
        return customName != null;
    }

    public void setCustomName(IFormattableTextComponent component) {
        customName = component;
        markDirty();
    }

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return new ColoringTableContainer(p_createMenu_1_, p_createMenu_2_, getPos(), IWorldPosCallable.of(world, getPos()));
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        handler.ifPresent(h -> h.deserializeNBT(nbt.getCompound("Inventory")));
        readLight(nbt);
        if(nbt.contains("CustomName", Constants.NBT.TAG_STRING)) {
            customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
        }
    }

    public void readLight(CompoundNBT nbt) {
        setColor(nbt.getInt("Color"));
    }

    @Override
    public CompoundNBT write(final CompoundNBT nbt) {
        super.write(nbt);
        handler.ifPresent(h -> nbt.put("Inventory", h.serializeNBT()));
        writeLight(nbt);
        if(hasCustomName()) {
            nbt.putString("CustomName", ITextComponent.Serializer.toJson(customName));
        }
        return nbt;
    }

    public void writeLight(CompoundNBT nbt) {
        nbt.putInt("Color", getColor());
    }

    private ItemStackHandler createItems() {
        return new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if(!this.removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return handler.cast();
        return super.getCapability(cap);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
    }

    public void setColor(int color) {
        this.color = color;
        markDirty();
    }

    public int getColor() {
        return color;
    }

    public void spawnColorParticles() {
        if(world.isRemote)
            ClientHandlers.spawnColorParticles(getPos(), getColor() / 10);
    }
}
