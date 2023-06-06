package snownee.snow.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import snownee.snow.CoreModule;
import snownee.snow.entity.FallingSnowEntity;

import javax.annotation.Nonnull;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class FallingSnowRenderer extends EntityRenderer<FallingSnowEntity> {
	public FallingSnowRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
		shadowSize = 0.5F;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(FallingSnowEntity entity, float p_225623_2_, float p_225623_3_, @Nonnull MatrixStack matrixstack, @Nonnull IRenderTypeBuffer buffer, int p_225623_6_) {
		if (entity.getLayers() <= 0 && entity.getLayers() > 8) {
			return;
		}
		BlockState blockstate = CoreModule.BLOCK.getDefaultState().with(SnowBlock.LAYERS, entity.getLayers());
		if (blockstate.getRenderType() != BlockRenderType.MODEL) {
			return;
		}
		World world = entity.getWorldObj();

		matrixstack.push();
		BlockPos blockpos = new BlockPos(entity.getPosX(), entity.getBoundingBox().maxY, entity.getPosZ());
		matrixstack.translate(-0.5D, 0.0D, -0.5D);
		BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		for (RenderType type : RenderType.getBlockRenderTypes()) {
			if (RenderTypeLookup.canRenderInLayer(blockstate, type)) {
				ForgeHooksClient.setRenderLayer(type);
				blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(blockstate), blockstate, blockpos, matrixstack, buffer.getBuffer(type), false, new Random(), blockstate.getPositionRandom(entity.getOrigin()), OverlayTexture.NO_OVERLAY);
			}
		}
		ForgeHooksClient.setRenderLayer(null);
		matrixstack.pop();
		super.render(entity, p_225623_2_, p_225623_3_, matrixstack, buffer, p_225623_6_);
	}

    @Nonnull
    @SuppressWarnings("deprecation")
	@Override
	public ResourceLocation getEntityTexture(@Nonnull FallingSnowEntity entity) {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}
