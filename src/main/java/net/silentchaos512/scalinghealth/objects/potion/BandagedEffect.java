/*
 * Scaling Health
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.scalinghealth.objects.potion;

import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.silentchaos512.scalinghealth.ScalingHealth;

public class BandagedEffect extends MobEffect {
    private static final float BASE_HEAL_RATE = 0.005f;
    public static final double SPEED_MODIFIER = -0.25;
    // Preserve the old stable UUID value as the target namespaced modifier ID.
    public static final Identifier MODIFIER_ID = Identifier.fromNamespaceAndPath(ScalingHealth.MOD_ID, "732486d8-f730-41a2-868f-eb988738986f");

    public BandagedEffect(MobEffectCategory type, int color) {
        super(type, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entityLiving, int amplifier) {
        // Remove effect if fully healed.
        if (entityLiving.getHealth() >= entityLiving.getMaxHealth()) {
            entityLiving.removeEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(this));
        }

        float healAmount = BASE_HEAL_RATE * entityLiving.getMaxHealth() * (amplifier + 1);
        // Preserve the removed helper's call to LivingEntity#heal, including its heal-event behavior.
        entityLiving.heal(healAmount);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
        // Heal every second.
        return tickCount % 20 == 0;
    }
}
