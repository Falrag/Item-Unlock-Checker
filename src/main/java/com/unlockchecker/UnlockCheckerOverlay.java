package com.unlockchecker;

import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.api.MenuEntry;

import javax.inject.Inject;
import java.awt.*;
import java.util.Set;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.game.ItemManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/*
 * This class is responsible for DRAWING on top of items.
 * RuneLite automatically calls this for every visible item.
 */
public class UnlockCheckerOverlay extends WidgetItemOverlay
{
    // Used to get information about items (like tradeable status)
    private final ItemManager itemManager;

    // This is our dynamic list of excluded items
    private final Set<Integer> excludedItems;

    @Inject
    private Client client;

    @Inject
    public UnlockCheckerOverlay(ItemManager itemManager, Set<Integer> excludedItems)
    {
        this.itemManager = itemManager;
        this.excludedItems = excludedItems;

        /*
         * These tell RuneLite WHERE this overlay should work
         * We're enabling basically everything
         */
        showOnInventory();
        showOnBank();
        showOnInterfaces();
        showOnEquipment();
      //  showOnLoot();
      //  showOnGroundItems();
    }

    /*
     * This method runs ONCE PER ITEM that appears on screen
     */
    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
    {
        // Get item data (like name, tradeable, etc.)
        ItemComposition itemDef = itemManager.getItemComposition(itemId);

        // If the item is NOT tradeable, do nothing
        if (!itemDef.isTradeable())
        {
            return;
        }

        // If the item IS in our excluded list, do nothing
        if (excludedItems.contains(itemId))
        {
            return;
        }

        /*
         * If we reach here:
         * - The item IS tradeable
         * - The item is NOT excluded
         * So we draw our symbol
         */

        // Get the on-screen position of this item
        Rectangle bounds = itemWidget.getCanvasBounds();

        // Set drawing color
        graphics.setColor(Color.RED);

        /*
         * Draw a small circle in the top-right corner of the item
         */
        graphics.fillOval(
                bounds.x + bounds.width - 8,
                bounds.y + 2,
                6,
                6
        );
    }
}
