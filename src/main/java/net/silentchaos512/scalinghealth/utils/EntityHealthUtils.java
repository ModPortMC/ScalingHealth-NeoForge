package net.silentchaos512.scalinghealth.utils;

import net.minecraft.world.entity.LivingEntity;

/** Local health operations that are no longer provided by the target Silent Lib. */
public final class EntityHealthUtils {
    private EntityHealthUtils() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Restores health without invoking the cancelable heal event, matching the former
     * {@code EntityHelper.heal(entity, amount, false)} behavior.
     */
    public static void healWithoutEvent(LivingEntity entity, float amount) {
        entity.setHealth(entity.getHealth() + amount);
    }
}
