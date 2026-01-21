package com.unlockchecker;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.awt.*;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;

//This is the MAIN plugin file
//It controls startup, shutdown, and data flow

@PluginDescriptor(
		name = "Item Unlock Checker",
		description = "Draws an icon over all tradeable items that are not excluded"
)
public class UnlockCheckerPlugin extends Plugin
{
	private static final Logger log = LoggerFactory.getLogger(UnlockCheckerPlugin.class);

	@Inject
	private ItemManager itemManager;

	// This manages all overlays in RuneLite
	@Inject
	private OverlayManager overlayManager;

	// Our custom overlay
	@Inject
	private  UnlockCheckerOverlay overlay;

	// Our config file
	@Inject
	private UnlockCheckerConfig config;

	@Inject
	private Client client;

	Set<Integer> nonGeItems;

	 //This stores our parsed item ID list
	 //We use a Set because it's FAST to check

	private final Set<Integer> excludedItems = new HashSet<>();

	 //This tells RuneLite how to load our config

	@Provides
	UnlockCheckerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(UnlockCheckerConfig.class);
	}


	//Runs when plugin is enabled

	@Override
	protected void startUp()
	{
		updateExcludedItems();
		nonGeItems = NonGeItemLoader.load();
		overlay.nonGeItems = nonGeItems;
		overlayManager.add(overlay);
		log.error(nonGeItems.toString());
	}


	 //Runs when plugin is disabled

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		excludedItems.clear();
	}


	//This runs whenever you change the config in RuneLite

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("itemunlockchecker"))
		{
			updateExcludedItems();
		}
	}


	 //This converts your text list into real numbers
	 //Example input:
	 //4151, 11840, 13576

	private void updateExcludedItems()
	{
		excludedItems.clear();

		String text = config.excludedItems();

		if (text.isEmpty())
		{
			return;
		}

		for (String s : text.split(","))
		{
			try
			{
				excludedItems.add(Integer.parseInt(s.trim()));
			}
			catch (NumberFormatException ignored)
			{
				// If user types something invalid, we just skip it
			}
		}
	}


	 //This lets the overlay access the SAME excluded list

	@Provides
	Set<Integer> provideExcludedItems()
	{
		return excludedItems;
	}

	//vanaf hier probeer item options weg te halen

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		MenuEntry[] entries = client.getMenuEntries();

		if (entries == null || entries.length == 0)
		{
			return;
		}

		// Find the last menu entry (this is usually the item you clicked)
		MenuEntry last = entries[entries.length - 1];

		int itemId = last.getItemId();

		if (itemId <= 0)
		{
			return;
		}

		// If item is not locked, do nothing
		if (excludedItems.contains(itemId))
		{
			return;
		}

		ItemComposition itemDef = itemManager.getItemComposition(itemId);

		// If the item is NOT tradeable, do nothing
		if (!itemDef.isTradeable())
		{
			if (!nonGeItems.contains(itemId))
				{return;} // Skip this item, it's in your exception list
		}

		// Rebuild menu: keep only Drop and Examine
		MenuEntry[] filtered = java.util.Arrays.stream(entries)
				.filter(e ->
						e.getOption().equalsIgnoreCase("Drop") ||
								e.getOption().equalsIgnoreCase("Examine") ||
									e.getOption().equalsIgnoreCase("Deposit-All") ||
										e.getOption().equalsIgnoreCase("Withdraw-All")

				)
				.toArray(MenuEntry[]::new);

		client.setMenuEntries(filtered);
	}

	private MenuEntry[] removeEntry(MenuEntry[] entries, MenuEntry toRemove)
	{
		return Arrays.stream(entries)
				.filter(e -> e != toRemove)
				.toArray(MenuEntry[]::new);
	}
}
