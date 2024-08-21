package net.mistersecret312.stonemedusa.event;

import joptsimple.util.RegexMatcher;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.minecraftforge.fml.common.Mod;
import net.mistersecret312.stonemedusa.item.MedusaItem;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber(modid = StoneMedusa.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventBusEvents
{

    @SubscribeEvent
    public static void chatEvent(ServerChatEvent event)
    {
        String message = event.getRawText();
        ServerPlayer player = event.getPlayer();
        Level level = player.level();
        if ((message.toLowerCase().contains("meter") || message.toLowerCase().contains("metre")) && message.toLowerCase().contains("second"))
        {
            String[] parts = message.replace("'", "").replace("seconds", "").replace("meter", "-").replace("meters", "-").replace("metre", "-").replace("metres", "-").split("-");
            float meters = Float.parseFloat(parts[0].replaceAll("[^1234567890.]", ""));
            int seconds = Integer.parseInt(parts[1].replaceAll("[^1234567890]", ""))*20;

            if(meters > 0f && seconds > 0)
            {
                List<ItemEntity> fallenItems = level.getEntities(EntityType.ITEM, new AABB(player.blockPosition().offset(new Vec3i(-3, -3, -3)), player.blockPosition().offset(new Vec3i(3, 3, 3))), item -> item.getItem().getItem() instanceof MedusaItem);
                List<Player> nearbyPlayers = level.getEntities(EntityType.PLAYER, new AABB(player.blockPosition().offset(new Vec3i(-3, -3, -3)), player.blockPosition().offset(new Vec3i(3, 3, 3))), playerEntity -> playerEntity.getInventory().hasAnyMatching(item -> item.getItem() instanceof MedusaItem));

                for(ItemEntity item : fallenItems)
                {
                    MedusaItem medusa = ((MedusaItem) item.getItem().getItem());
                    medusa.setStartDelay(item.getItem(), seconds);
                    medusa.setDelay(item.getItem(), seconds);
                    medusa.setRadius(item.getItem(), meters);
                    medusa.setCountdownActive(item.getItem(), true);
                }
                for(Player playerEntity : nearbyPlayers)
                {
                    for(ItemStack stack : playerEntity.getInventory().items)
                    {
                        MedusaItem medusa = ((MedusaItem) stack.getItem());
                        medusa.setStartDelay(stack, seconds);
                        medusa.setDelay(stack, seconds);
                        medusa.setRadius(stack, meters);
                        medusa.setCountdownActive(stack, true);
                    }
                }
            }
        }
    }


}
