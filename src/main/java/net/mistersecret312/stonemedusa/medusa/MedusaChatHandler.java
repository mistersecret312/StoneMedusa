package net.mistersecret312.stonemedusa.medusa;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.mistersecret312.stonemedusa.StoneMedusa;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import net.mistersecret312.stonemedusa.init.BeamSourceInit;
import net.mistersecret312.stonemedusa.items.MedusaItem;
import net.mistersecret312.stonemedusa.medusa.source.MedusaSource;
import net.mistersecret312.stonemedusa.util.MedusaUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientChatEvent;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.event.ServerChatEvent;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EventBusSubscriber(modid = StoneMedusa.MODID, bus = EventBusSubscriber.Bus.GAME)
public class MedusaChatHandler {

    public static final Pattern MEDUSA_COMMAND = Pattern.compile(
            "(\\d+(?:\\.\\d+)?)\\s*meters?\\s*(\\d+(?:\\.\\d+)?)\\s*(seconds?|minutes?|hours?)",
            Pattern.CASE_INSENSITIVE
    );

    @SubscribeEvent
    public static void onServerChat(ServerChatEvent event)
    {
        String message = event.getRawText();
        Matcher matcher = MEDUSA_COMMAND.matcher(message);
        if (matcher.matches())
        {
            double meters = Double.parseDouble(matcher.group(1));
            double timeVal = Double.parseDouble(matcher.group(2));
            String unit = matcher.group(3).toLowerCase();

            double seconds = timeVal;
            if (unit.startsWith("min"))
                seconds = timeVal * 60.0;
            else if (unit.startsWith("hour"))
                seconds = timeVal * 3600.0;

            if(seconds < 0)
                seconds = 0;
            if(meters < 0d)
                meters = 0.1d;

            ServerPlayer player = event.getPlayer();
            if(player.getData(AttachmentTypeInit.PETRIFICATION).getPetrificationProgress() > 75)
                return;

            AABB box = new AABB(player.blockPosition()).inflate(3);
            List<MedusaSource> sourceList = MedusaUtil.scanForMedusas(player.serverLevel(), box);
            for(MedusaSource source : sourceList)
            {
                ItemStack stack = source.getMedusaItem(player.serverLevel());
                IMedusa medusa = source.resolve(player.serverLevel());
                if(medusa != null && stack != null && stack.getItem() instanceof MedusaItem)
                {
                    if(MedusaItem.isActive(stack))
                        continue;

                    UUID id = MedusaItem.getDeviceId(stack);
                    MedusaSettings.MedusaPosition position = source.providePosition(player.serverLevel());
                    if(position == null)
                        position = new MedusaSettings.MedusaPosition(player.position().toVector3f(),
                                player.serverLevel().dimension());

                    double speed = 0.01 + (0.15d * Mth.sqrt((float) meters));
                    MedusaSettings settings = new MedusaSettings(meters, speed, 0x00FF00,
                            position, id, source);
                    MedusaBeam beam = new MedusaBeam(settings, player.getUUID());
                    int ticks = (int) (seconds*20);
                    beam.setMaxDelay(ticks);
                    beam.setDelayTicker(ticks);
                    medusa.emitBeam(player.serverLevel(), beam);
                }
            }
        }
    }
}