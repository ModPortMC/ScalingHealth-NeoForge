package net.silentchaos512.scalinghealth.capability;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.silentchaos512.scalinghealth.utils.ModifierHandler;

public class PetHealthCapability implements IPetData, ValueIOSerializable {
    public static final AttachmentType<PetHealthCapability> INSTANCE = AttachmentType
            .serializable(PetHealthCapability::new)
            .build();

    private static final String NBT_HEALTH = "SHPetBonusHealth";

    private float bonusHealth;
    private boolean refreshed = false;

    @Override
    public void addHealth(double hp, TamableAnimal pet ) {
        bonusHealth += hp;
        ModifierHandler.setMaxHealth(pet, bonusHealth, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public float getBonusHealth() {
        return bonusHealth;
    }

    @Override
    public void tick(TamableAnimal pet) {
        if(!refreshed && pet.tickCount > 2){
            refreshed = true;
            ModifierHandler.setMaxHealth(pet, getBonusHealth(), AttributeModifier.Operation.ADD_VALUE);
        }
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putFloat(NBT_HEALTH, bonusHealth);
    }

    @Override
    public void deserialize(ValueInput input) {
        bonusHealth = input.getFloatOr(NBT_HEALTH, 0);
    }
}
