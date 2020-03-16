package apps.nerdyginger.pocketpantry;

import apps.nerdyginger.pocketpantry.models.BrowseRecipeItem;

/*
 * Interface for click interactions with the child adapter in the browse recipes page
 * Last edited: 3/11/2020
 */
public interface BrowseRecipeClickListener {
    void onClick(BrowseRecipeItem clickedItem);
    boolean onLongClick(BrowseRecipeItem longClickedItem);
}
