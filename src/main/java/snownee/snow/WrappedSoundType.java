package snownee.snow;

import com.google.common.collect.Maps;
import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nonnull;
import java.util.Map;

public final class WrappedSoundType extends SoundType {

	private final SoundType type;

	@SuppressWarnings("deprecation")
	private WrappedSoundType(SoundType type) {
		super(type.getVolume(), type.getPitch(), null, null, null, null, null);
		this.type = type;
	}

	@Nonnull
	@Override
	public SoundEvent getStepSound() {
		return SoundType.SNOW.getStepSound();
	}

	@Nonnull
	@Override
	public SoundEvent getFallSound() {
		return SoundType.SNOW.getFallSound();
	}

	@Nonnull
	@Override
	public SoundEvent getBreakSound() {
		return type.getBreakSound();
	}

	@Nonnull
	@Override
	public SoundEvent getPlaceSound() {
		return type.getPlaceSound();
	}

	@Nonnull
	@Override
	public SoundEvent getHitSound() {
		return type.getHitSound();
	}

	private static final Map<SoundType, SoundType> wrappedSounds = Maps.newConcurrentMap();

	public static SoundType get(SoundType soundType) {
		if (soundType == SoundType.SNOW || soundType instanceof WrappedSoundType) {
			return soundType;
		}
		return wrappedSounds.computeIfAbsent(soundType, WrappedSoundType::new);
	}
}
