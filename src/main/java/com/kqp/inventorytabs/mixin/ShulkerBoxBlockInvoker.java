package com.kqp.inventorytabs.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.ShulkerLidCollisions;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.block.ShulkerBoxBlock.FACING;

//@Mixin(ShulkerBoxBlock.class)
public interface ShulkerBoxBlockInvoker {
	public static boolean invokeCanOpen(BlockState state, World world, BlockPos pos, ShulkerBoxBlockEntity entity) {
		if (entity.getAnimationStage() == ShulkerBoxBlockEntity.AnimationStage.CLOSED) {
			Direction direction = state.get(FACING);
			return world.isSpaceEmpty(ShulkerLidCollisions.getLidCollisionBox(pos, direction));
		} else {
			return true;
		}
	};
}
