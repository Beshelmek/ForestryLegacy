/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.mail;

import java.util.List;

import forestry.core.proxy.Proxies;
import forestry.core.utils.CommandMC;
import forestry.core.utils.StringUtil;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CommandMail extends CommandMC {

	@Override
	public int compareTo(Object arg0) {
		return this.getCommandName().compareTo(((ICommand) arg0).getCommandName());
	}

	@Override
	public String getCommandName() {
		return "mail";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " help";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] arguments) {
		if (arguments.length <= 0)
			throw new WrongUsageException("Type '" + this.getCommandUsage(sender) + "' for help.");

		if (arguments[0].matches("trades")) {
			commandTrades(sender, arguments);
			return;
		} else if (arguments[0].matches("virtualize")) {
			commandVirtualize(sender, arguments);
			return;
		} else if (arguments[0].matches("help")) {
			sender.sendChatToPlayer("Format: '" + this.getCommandName() + " <command> <arguments>'");
			sender.sendChatToPlayer("Available commands:");
			sender.sendChatToPlayer("- trades : A list of all trade stations.");
			sender.sendChatToPlayer("- virtualize <tradestation-name>: Toggle virtual mode on specified trade station.");
			return;
		}

		throw new WrongUsageException(this.getCommandUsage(sender));
	}

	private void commandTrades(ICommandSender sender, String[] arguments) {
		if(!(sender instanceof EntityPlayer))
			return;
		for(TradeStation trade : PostOffice.getPostOffice(((EntityPlayer)sender).worldObj).getActiveTradeStations(((EntityPlayer)sender).worldObj).values())
			sender.sendChatToPlayer(makeTradeListEntry(trade.getTradeInfo()));
	}	
	
	private String makeTradeListEntry(TradeStationInfo info) {
		String entry = "\u00A7c";
		if (info.state == EnumStationState.OK) {
			entry = "\u00A7a";
		}

		String tradegood = "[ ? ]";
		if(info.tradegood != null) {
			tradegood = info.tradegood.stackSize + "x" + info.tradegood.getDisplayName();
		}
		String demand = "[ ? ]";
		if(info.required.length > 0) {
			demand = "";
			for(ItemStack dmd : info.required) {
				demand = StringUtil.append(", ", demand, dmd.stackSize + "x" + dmd.getDisplayName());
			}
		}
		
		return String.format("%s%-12s | %-20s | %s", entry, info.moniker, tradegood, demand);
	}
	
	private void commandVirtualize(ICommandSender sender, String[] arguments) {
		if ((sender instanceof EntityPlayer && !Proxies.common.isOp((EntityPlayer) sender))
				|| (!sender.canCommandSenderUseCommand(4, getCommandName()))) {
				sender.sendChatToPlayer("\u00a7cYou do not have permission to use this command.");
				return;	
		}

		if(arguments.length <= 1) {
			throw new WrongUsageException("/" + getCommandName() + " virtualize <tradestation-name>");			
		}
		
		World world = getWorld(sender, arguments);
		TradeStation trade = PostOffice.getTradeStation(world, arguments[1]);
		if(trade == null) {
			sender.sendChatToPlayer(String.format("\u00a7cNo tradestation by the name of '%s' was found.", arguments[1]));
			return;
		}
		
		trade.setVirtual(!trade.isVirtual());
		sender.sendChatToPlayer(String.format("\u00A7aSet virtualization for '%s' to %s.", trade.getMoniker(), trade.isVirtual()));
	}
}
