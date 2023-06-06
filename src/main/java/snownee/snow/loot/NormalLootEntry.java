package snownee.snow.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import snownee.snow.CoreModule;
import snownee.snow.block.SnowTile;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class NormalLootEntry extends StandaloneLootEntry {

	private NormalLootEntry(int weightIn, int qualityIn, ILootCondition[] conditionsIn, ILootFunction[] functionsIn) {
		super(weightIn, qualityIn, conditionsIn, functionsIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void func_216154_a(@Nonnull Consumer<ItemStack> consumer, LootContext context) {
		TileEntity tile = context.get(LootParameters.BLOCK_ENTITY);
		if (tile instanceof SnowTile) {
			BlockState state = ((SnowTile) tile).getState();
			if (!state.isAir()) {
				ResourceLocation resourcelocation = state.getBlock().getLootTable();
				if (resourcelocation != LootTables.EMPTY) {
					LootContext.Builder builder = new LootContext.Builder(context);
					LootContext lootcontext = builder.withParameter(LootParameters.BLOCK_STATE, state).build(LootParameterSets.BLOCK);
					ServerWorld serverworld = lootcontext.getWorld();
					LootTable loottable = serverworld.getServer().getLootTableManager().getLootTableFromLocation(resourcelocation);
					loottable.generate(lootcontext).forEach(consumer);
				}
			}
		}
	}

	public static StandaloneLootEntry.Builder<?> builder(IItemProvider itemIn) {
		return builder(NormalLootEntry::new);
	}

	@Nonnull
	@Override
	public LootPoolEntryType func_230420_a_() {
		return CoreModule.NORMAL;
	}

	public static class Serializer extends StandaloneLootEntry.Serializer<NormalLootEntry> {
		@Override
		public void doSerialize(@Nonnull JsonObject json, @Nonnull NormalLootEntry lootEntry, @Nonnull JsonSerializationContext context) {
			super.doSerialize(json, lootEntry, context);
		}

		@Nonnull
		@Override
		protected NormalLootEntry deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context, int weightIn, int qualityIn, @Nonnull ILootCondition[] conditionsIn, @Nonnull ILootFunction[] functionsIn) {
			return new NormalLootEntry(weightIn, qualityIn, conditionsIn, functionsIn);
		}
	}
}
