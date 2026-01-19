package com.unlockchecker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("unlockchecker")
public interface UnlockCheckerConfig extends Config
{
	@ConfigItem(
			keyName = "excludedItems",
			name = "Excluded Item IDs",
			description = "Comma-separated list of item IDs that should NOT get an icon",
			position = 1
	)
	default String excludedItems()
	{
		// Default value when plugin is first loaded
		return "";
	}
}
