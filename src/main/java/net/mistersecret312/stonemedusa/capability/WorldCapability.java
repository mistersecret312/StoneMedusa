package net.mistersecret312.stonemedusa.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.mistersecret312.stonemedusa.config.MedusaConfig;
import net.mistersecret312.stonemedusa.entity.MedusaProjectile;
import net.mistersecret312.stonemedusa.init.ItemInit;
import net.mistersecret312.stonemedusa.item.MedusaItem;

import java.util.Random;

public class WorldCapability implements INBTSerializable<CompoundTag>
{
    public static final String PETRIFICATION_RAIN_TICKER = "petrification_rain_ticker";

    private int petrificationRainTicker = 0;

    public void tick(Level level)
    {
        Random random = new Random();
        if(this.getPetrificationRainTicker() == MedusaConfig.generation_period.get()*20)
        {
            for(Player player : level.players())
            {
                if (random.nextDouble() > MedusaConfig.generation_chance.get())
                {
                    for(int i = 0; i < random.nextInt(MedusaConfig.min_generated_amount.get(), MedusaConfig.max_generated_amount.get()); i++)
                    {
                        ItemStack stack = MedusaItem.getMedusa(ItemInit.MEDUSA.get(), MedusaConfig.max_energy.get(), 5f, 20);
                        MedusaProjectile medusa = new MedusaProjectile(level, MedusaConfig.max_energy.get(), 5f, 20, false, false, "", true);
                        medusa.setItem(stack);
                        medusa.setPos(player.position().x + random.nextFloat(-72, 72), player.position().y + 350 + random.nextFloat(-72, 72), player.position().z + random.nextInt(-72, 72));
                        medusa.setDeltaMovement(new Vec3(random.nextFloat(0, 0.01f), random.nextFloat(-3f, -0.25f), random.nextFloat(0, 0.01f)));
                        level.addFreshEntity(medusa);
                    }

                }
            }
            this.setPetrificationRainTicker(0);
        } else this.setPetrificationRainTicker(this.getPetrificationRainTicker()+1);
    }

    public int getPetrificationRainTicker()
    {
        return petrificationRainTicker;
    }

    public void setPetrificationRainTicker(int petrificationRainTicker)
    {
        this.petrificationRainTicker = petrificationRainTicker;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putInt(PETRIFICATION_RAIN_TICKER, this.getPetrificationRainTicker());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        this.setPetrificationRainTicker(nbt.getInt(PETRIFICATION_RAIN_TICKER));
    }
}
