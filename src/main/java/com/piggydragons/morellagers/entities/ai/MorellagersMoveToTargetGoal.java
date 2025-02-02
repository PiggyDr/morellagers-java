package com.piggydragons.morellagers.entities.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class MorellagersMoveToTargetGoal extends Goal {

    protected final float wanted_range_sqr;
    protected final Mob mob;

    public MorellagersMoveToTargetGoal(Mob mob, float wanted_range) {
        wanted_range_sqr = wanted_range * wanted_range;
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null && mob.distanceToSqr(mob.getTarget()) > wanted_range_sqr;
    }

    @Override
    public void tick() {
        mob.getNavigation().moveTo(mob.getTarget(), 1);
        if (mob.distanceToSqr(mob.getTarget()) < wanted_range_sqr) stop();
    }
}
