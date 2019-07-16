package apps.nerdyginger.cleanplateclub;

import android.view.View;

/*
 * Last edited: 7/15/2019
 * Interface for RecyclerView click listener, required for interactions with RecyclerView items
 */
public interface RecyclerViewClickListener {
    void onClick (View view, int position);
    boolean onLongClick (View view, int position);
}
