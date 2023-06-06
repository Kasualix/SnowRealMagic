package snownee.snow.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import snownee.snow.ModUtil;
import snownee.snow.SnowCommonConfig;

import javax.annotation.Nonnull;
import java.util.Random;

public class SnowSlabBlock extends Block implements WaterLoggableSnowVariant {
	protected static final VoxelShape BOTTOM_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape BOTTOM_RENDER_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);

	public SnowSlabBlock(Properties builder) {
		super(builder);
	}

	@Nonnull
	@Override
	public ActionResultType onBlockActivated(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (!(tile instanceof SnowTextureTile)) {
			return ActionResultType.PASS;
		}
		SnowTextureTile snowTile = (SnowTextureTile) tile;
		ItemStack stack = player.getHeldItem(handIn);
		if (stack.isEmpty() && player.getHeldItemOffhand().isEmpty()) {
			snowTile.options.renderOverlay = !snowTile.options.renderOverlay;
			snowTile.refresh();
			return ActionResultType.SUCCESS;
		}
		if (hit.getFace() == Direction.UP && snowTile.getState().getBlock().asItem() == stack.getItem() && stack.getItem() instanceof BlockItem && stack.getItem().isIn(ItemTags.SLABS)) {
			Block block = ((BlockItem) stack.getItem()).getBlock();
			if (block instanceof SlabBlock) {
				BlockState state2 = block.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE);
				if (!worldIn.isRemote) {
					worldIn.setBlockState(pos, state2);
					if (!player.isCreative()) {
						stack.shrink(1);
					}
					if (player instanceof ServerPlayerEntity) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, pos, stack);
					}
				}
				SoundType soundtype = state2.getSoundType(worldIn, pos, player);
				worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
		return BOTTOM_SHAPE;
	}

	@Nonnull
	@Override
	public VoxelShape getRenderShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
		return BOTTOM_RENDER_SHAPE;
	}

	@Override
	public boolean allowsMovement(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull PathType type) {
		return false;
	}

	@Override
	public void randomTick(@Nonnull BlockState state, @Nonnull ServerWorld worldIn, @Nonnull BlockPos pos, @Nonnull Random random) {
		if (SnowCommonConfig.retainOriginalBlocks || ModUtil.shouldMelt(worldIn, pos)) {
			worldIn.setBlockState(pos, getRaw(state, worldIn, pos));
		}
	}

	@Override
	public boolean isTransparent(@Nonnull BlockState state) {
		return true;
	}

	@Override
	public float getPlayerRelativeBlockHardness(@Nonnull BlockState state, @Nonnull PlayerEntity player, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
		return getRaw(state, worldIn, pos).getPlayerRelativeBlockHardness(player, worldIn, pos);
	}

}
