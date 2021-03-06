package com.bytelaw.common.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class ColoringTableBlock extends Block {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 12, 16);
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public ColoringTableBlock(Properties properties) {
        super(properties);
        setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity te = worldIn.getTileEntity(pos);
        if(!worldIn.isRemote && te instanceof ColoringTableTile) {
            NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if(te instanceof ColoringTableTile) {
                ColoringTableTile coloringTable = (ColoringTableTile)te;
                if(stack.hasTag() && stack.getTag().contains("TileInfo", Constants.NBT.TAG_COMPOUND)) {
                    CompoundNBT tileInfo = stack.getTag().getCompound("TileInfo");
                    coloringTable.readLight(tileInfo);
                }
                if(stack.hasDisplayName())
                    coloringTable.setCustomName((IFormattableTextComponent)stack.getDisplayName());
            }
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
        return rotate(state, direction);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if(te instanceof ColoringTableTile) {
                te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                    NonNullList<ItemStack> list = NonNullList.withSize(2, ItemStack.EMPTY);
                    list.set(0, h.getStackInSlot(0));
                    list.set(1, h.getStackInSlot(1));
                    InventoryHelper.dropItems(worldIn, pos, list);
                });
            }
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> stacks = super.getDrops(state, builder);
        ItemStack stack = stacks.get(0).copy();
        TileEntity te = builder.assertPresent(LootParameters.BLOCK_ENTITY);
        if(te instanceof ColoringTableTile) {
            ColoringTableTile coloringTable = (ColoringTableTile)te;
            CompoundNBT teData = new CompoundNBT();
            coloringTable.writeLight(teData);
            stack.setTagInfo("TileInfo", teData);
            if(coloringTable.hasCustomName())
                stack.setDisplayName(coloringTable.getDisplayName());
        }
        stacks.set(0, stack);
        return stacks;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ColoringTableTile();
    }
}
