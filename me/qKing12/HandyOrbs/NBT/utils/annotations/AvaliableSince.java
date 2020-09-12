package me.qKing12.HandyOrbs.NBT.utils.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.qKing12.HandyOrbs.NBT.utils.MinecraftVersion;

@Retention(RUNTIME)
@Target({ METHOD })
public @interface AvaliableSince {

	MinecraftVersion version();

}