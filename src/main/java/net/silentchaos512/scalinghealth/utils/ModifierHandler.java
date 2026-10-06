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

package net.silentchaos512.scalinghealth.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.silentchaos512.scalinghealth.ScalingHealth;

public final class ModifierHandler {
    // Keep the former stable UUID values in the new namespaced modifier identity.
    private static final Identifier MODIFIER_ID_HEALTH = Identifier.fromNamespaceAndPath(ScalingHealth.MOD_ID, "c0bef565-35f6-4dc5-bb4c-3644c382e6ce");
    private static final Identifier MODIFIER_ID_DAMAGE = Identifier.fromNamespaceAndPath(ScalingHealth.MOD_ID, "d3560b15-c459-451c-86a8-0247015ae899");

    private ModifierHandler() { throw new IllegalAccessError("Utility class"); }

    public static void setModifier(LivingEntity entity, Holder<Attribute> attribute, Identifier id, double amount, AttributeModifier.Operation op) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        instance.addOrReplacePermanentModifier(new AttributeModifier(id, amount, op));
    }

    public static void setMaxHealth(LivingEntity entity, double amount, AttributeModifier.Operation op) {
        double oldMax = entity.getMaxHealth();
        setModifier(entity, Attributes.MAX_HEALTH, MODIFIER_ID_HEALTH, amount, op);
        double newMax = entity.getMaxHealth();

        // Heal entity when increasing max health
        if (newMax > oldMax) {
            float healAmount = (float) (newMax - oldMax);
            EntityHealthUtils.healWithoutEvent(entity, healAmount);
        } else if (entity.getHealth() > newMax) {
            entity.setHealth((float) newMax);
        }
    }

    public static void addAttackDamage(LivingEntity entity, double amount, AttributeModifier.Operation op) {
        setModifier(entity, Attributes.ATTACK_DAMAGE, MODIFIER_ID_DAMAGE, amount, op);
    }
}
