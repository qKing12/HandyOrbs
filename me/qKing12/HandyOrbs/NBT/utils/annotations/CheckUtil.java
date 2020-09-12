package me.qKing12.HandyOrbs.NBT.utils.annotations;

import java.lang.reflect.Method;

import me.qKing12.HandyOrbs.NBT.NbtApiException;
import me.qKing12.HandyOrbs.NBT.utils.MinecraftVersion;

public class CheckUtil {

	public static boolean isAvaliable(Method method) {
		if(MinecraftVersion.getVersion().getVersionId() < method.getAnnotation(AvaliableSince.class).version().getVersionId())
			throw new NbtApiException("The Method '" + method.getName() + "' is only avaliable for the Versions " + method.getAnnotation(AvaliableSince.class).version() + "+, but still got called!");
		return true;
	}
	
}
