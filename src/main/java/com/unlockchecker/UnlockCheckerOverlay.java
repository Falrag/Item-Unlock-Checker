package com.unlockchecker;

import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.awt.*;
import java.util.Set;

//This class is responsible for DRAWING on top of items.
// RuneLite automatically calls this for every visible item.

public class UnlockCheckerOverlay extends WidgetItemOverlay
{
    private static final Logger log = LoggerFactory.getLogger(UnlockCheckerOverlay.class);

    // Used to get information about items (like tradeable status)
    private final ItemManager itemManager;

    // This is our dynamic list of excluded items
    private final Set<Integer> excludedItems;

    Set<Integer> nonGeItems;

    @Inject
    private Client client;

    @Inject
    public UnlockCheckerOverlay(ItemManager itemManager, Set<Integer> excludedItems, Set<Integer> nonGeItems)
    {
        this.itemManager = itemManager;
        this.excludedItems = excludedItems;
        this.nonGeItems = nonGeItems;
        log.error(nonGeItems.toString());
        //These tell RuneLite WHERE this overlay should work
        //We're enabling basically everything

        showOnInventory();
        showOnBank();
        showOnInterfaces();
        showOnEquipment();
        //showOnLoot();
        //showOnGroundItems();
    }


     // This method runs ONCE PER ITEM that appears on screen

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
    {
        // Get item data (like name, tradeable, etc.)
        ItemComposition itemDef = itemManager.getItemComposition(itemId);

        // If the item is NOT tradeable, do nothing
        if (!itemDef.isTradeable())
        {
            if (!nonGeItems.contains(itemId))
                {return;} // Skip this item, it's in your exception list
        }

        // If the item IS in our excluded list, do nothing
        if (excludedItems.contains(itemId))
        {
            return;
        }

         //If we reach here:
         //- The item IS tradeable
         //- The item is NOT excluded
         //So we draw our symbol

        // Get the on-screen position of this item
        Rectangle bounds = itemWidget.getCanvasBounds();

        // Set drawing color
        graphics.setColor(Color.RED);


        // Draw a small circle in the top-right corner of the item

        graphics.drawLine(
                bounds.x,
                bounds.y,
                bounds.x+24,
                bounds.y+24
        );
    }
}
