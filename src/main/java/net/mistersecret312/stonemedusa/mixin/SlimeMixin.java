package net.mistersecret312.stonemedusa.mixin;

import net.minecraft.world.entity.monster.Slime;
import net.mistersecret312.stonemedusa.data_attachment.PetrificationAttachment;
import net.mistersecret312.stonemedusa.init.AttachmentTypeInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slime.class)
public class SlimeMixin
{
	@Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
	public void stopTick(CallbackInfo ci)
	{
		Slime slime = ((Slime) (Object) this);
		PetrificationAttachment attachment = slime.getData(AttachmentTypeInit.PETRIFICATION);
		if(!attachment.shouldInteract())
			ci.cancel();
	}
}
