package com.unlockchecker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads a list of non-GE tradeable item IDs from a text file
 * and stores them in a HashSet for fast lookups.
 */
public class NonGeItemLoader
{
    private static final String FILE_NAME = "/nonGE_LockedAtStart.txt";

    public static Set<Integer> load()
    {
        Set<Integer> itemIds = new HashSet<>();

        try
        {
            // Loads file from resources folder inside the plugin JAR
            InputStream inputStream = NonGeItemLoader.class.getResourceAsStream(FILE_NAME);

            if (inputStream == null)
            {
                System.out.println("Could not find nonGE_LockedAtStart.txt in resources!");
                return itemIds;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null)
            {
                // Split by commas
                String[] parts = line.split(",");

                for (String part : parts)
                {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty())
                    {
                        itemIds.add(Integer.parseInt(trimmed));
                    }
                }
            }

            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return itemIds;
    }
}
