package com.piggydragons.morellagers.mixin;

import com.piggydragons.morellagers.capability.SummonedMinionCap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(
            method = "isSunBurnTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void dontBurnMinions(CallbackInfoReturnable<Boolean> cir) {
        if (SummonedMinionCap.isMinion((Entity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}
