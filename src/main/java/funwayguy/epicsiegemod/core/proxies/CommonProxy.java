package funwayguy.epicsiegemod.core.proxies;

import net.minecraftforge.common.MinecraftForge;
import funwayguy.epicsiegemod.ai.additions.AdditionAnimalAttack;
import funwayguy.epicsiegemod.ai.additions.AdditionAnimalRetaliate;
import funwayguy.epicsiegemod.ai.additions.AdditionAvoidExplosives;
import funwayguy.epicsiegemod.ai.additions.AdditionDemolition;
import funwayguy.epicsiegemod.ai.additions.AdditionDigger;
import funwayguy.epicsiegemod.ai.additions.AdditionGrief;
import funwayguy.epicsiegemod.ai.additions.AdditionPillaring;
import funwayguy.epicsiegemod.ai.modifiers.ModifierAttackMelee;
import funwayguy.epicsiegemod.ai.modifiers.ModifierBowAttack;
import funwayguy.epicsiegemod.ai.modifiers.ModifierCreeperSwell;
import funwayguy.epicsiegemod.ai.modifiers.ModifierNearestAttackable;
import funwayguy.epicsiegemod.ai.modifiers.ModifierNoPanic;
import funwayguy.epicsiegemod.ai.modifiers.ModifierRangedAttack;
import funwayguy.epicsiegemod.ai.modifiers.ModifierSwimming;
import funwayguy.epicsiegemod.ai.modifiers.ModifierVillagerAvoid;
import funwayguy.epicsiegemod.ai.modifiers.ModifierZombieAttack;
import funwayguy.epicsiegemod.api.TaskRegistry;
import funwayguy.epicsiegemod.capabilities.combat.CapabilityAttackerHandler;
import funwayguy.epicsiegemod.capabilities.modified.CapabilityModifiedHandler;
import funwayguy.epicsiegemod.client.UpdateNotification;
import funwayguy.epicsiegemod.handlers.MainHandler;
import funwayguy.epicsiegemod.handlers.entities.CreeperHandler;
import funwayguy.epicsiegemod.handlers.entities.GeneralEntityHandler;
import funwayguy.epicsiegemod.handlers.entities.PlayerHandler;
import funwayguy.epicsiegemod.handlers.entities.SkeletonHandler;
import funwayguy.epicsiegemod.handlers.entities.SpiderHandler;
import funwayguy.epicsiegemod.handlers.entities.WitchHandler;
import funwayguy.epicsiegemod.handlers.entities.ZombieHandler;

public class CommonProxy
{
	public boolean isClient()
	{
		return false;
	}
	
	public void registerHandlers()
	{
		MinecraftForge.EVENT_BUS.register(new UpdateNotification());
		
		CapabilityAttackerHandler.register();
		CapabilityModifiedHandler.register();
		MinecraftForge.EVENT_BUS.register(new MainHandler());
		MinecraftForge.EVENT_BUS.register(new CreeperHandler());
		MinecraftForge.EVENT_BUS.register(new SkeletonHandler());
		MinecraftForge.EVENT_BUS.register(new WitchHandler());
		MinecraftForge.EVENT_BUS.register(new SpiderHandler());
		MinecraftForge.EVENT_BUS.register(new PlayerHandler());
		MinecraftForge.EVENT_BUS.register(new ZombieHandler());
		MinecraftForge.EVENT_BUS.register(new GeneralEntityHandler());
		
		TaskRegistry.INSTANCE.registerTaskModifier(new ModifierSwimming());
		TaskRegistry.INSTANCE.registerTaskModifier(new ModifierNearestAttackable());
		TaskRegistry.INSTANCE.registerTaskModifier(new ModifierNoPanic());
		TaskRegistry.INSTANCE.registerTaskModifier(new ModifierCreeperSwell());
		TaskRegistry.INSTANCE.registerTaskModifier(new ModifierVillagerAvoid());
		TaskRegistry.INSTANCE.registerTaskModifier(new ModifierAttackMelee());
		TaskRegistry.INSTANCE.registerTaskModifier(new ModifierZombieAttack());
		TaskRegistry.INSTANCE.registerTaskModifier(new ModifierRangedAttack());
		TaskRegistry.INSTANCE.registerTaskModifier(new ModifierBowAttack());
		
		TaskRegistry.INSTANCE.registerTaskAddition(new AdditionAnimalRetaliate());
		TaskRegistry.INSTANCE.registerTaskAddition(new AdditionAnimalAttack());
		TaskRegistry.INSTANCE.registerTaskAddition(new AdditionAvoidExplosives());
		TaskRegistry.INSTANCE.registerTaskAddition(new AdditionDigger());
		TaskRegistry.INSTANCE.registerTaskAddition(new AdditionDemolition());
		TaskRegistry.INSTANCE.registerTaskAddition(new AdditionPillaring());
		TaskRegistry.INSTANCE.registerTaskAddition(new AdditionGrief());
	}
	
	public void registerRenderers()
	{
	}
}
