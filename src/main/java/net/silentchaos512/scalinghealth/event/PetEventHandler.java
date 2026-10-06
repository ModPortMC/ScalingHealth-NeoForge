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

package net.silentchaos512.scalinghealth.event;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.objects.item.HeartCrystal;
import net.silentchaos512.scalinghealth.resources.mechanics.SHMechanics;
import net.silentchaos512.scalinghealth.utils.config.EnabledFeatures;

@EventBusSubscriber(modid = ScalingHealth.MOD_ID)
public class PetEventHandler {
    @SubscribeEvent
    public static void onLivingUpdate(EntityTickEvent.Pre event) {
        double regenDelay = SHMechanics.getMechanics().mobMechanics().pets().petsRegenDelay();
        if (regenDelay <= 0)
            return;

        if (event.getEntity() instanceof LivingEntity entity && !entity.level().isClientSide()) {
            boolean fullHp = entity.getHealth() == entity.getMaxHealth();
            boolean isTamed = entity instanceof TamableAnimal && ((TamableAnimal) entity).isTame();
            boolean isRegenTime = entity.invulnerableTime <= 0 && entity.tickCount % regenDelay == 0;
            if (isTamed && isRegenTime && !fullHp)
                entity.heal(2f);
        }
    }

    @SubscribeEvent
    public static void onPetInteraction(PlayerInteractEvent.EntityInteractSpecific event){
        if(!EnabledFeatures.petBonusHpEnabled() ||
                !(event.getItemStack().getItem() instanceof HeartCrystal) ||
                !(event.getTarget() instanceof TamableAnimal))
            return;

        TamableAnimal pet = (TamableAnimal) event.getTarget();
        if(!pet.isTame())
            return;

        event.setCancellationResult(InteractionResult.SUCCESS);
        if(pet.level().isClientSide()) {
            event.setCanceled(true);
            return;
        }

        HeartCrystal heart = (HeartCrystal) event.getItemStack().getItem();
        heart.increasePetHp(event.getEntity(), pet, event.getItemStack());
        event.setCanceled(true);
    }
}
