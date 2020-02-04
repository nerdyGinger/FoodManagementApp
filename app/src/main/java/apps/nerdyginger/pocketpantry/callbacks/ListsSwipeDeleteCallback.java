package apps.nerdyginger.pocketpantry.callbacks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.adapters.ListsAdapter;
import apps.nerdyginger.pocketpantry.models.UserListItem;
import apps.nerdyginger.pocketpantry.view_models.ListItemViewModel;

public class ListsSwipeDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private ListsAdapter mAdapter;
    private ListItemViewModel mModel;
    private Drawable icon;
    private final ColorDrawable background;

    public ListsSwipeDeleteCallback(ListsAdapter adapter, Context context, ListItemViewModel model) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        mModel = model;
        icon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        background = new ColorDrawable(Color.RED);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder holder, int direction) {
        int position = holder.getAdapterPosition();
        final UserListItem deletedItem = mAdapter.deleteItem(position);
        mModel.deleteItem(deletedItem);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder holder, @NonNull RecyclerView.ViewHolder holder2) {
        return true;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder holder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, holder, dX, dY, actionState, isCurrentlyActive);
        View itemView = holder.itemView;
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();

            int magicConstraint = (itemView.getLeft() + ((int) dX) < iconRight + iconMargin) ? (int)dX - icon.getIntrinsicWidth() - ( iconMargin * 2 ) : 0;
            iconLeft += magicConstraint;
            iconRight += magicConstraint;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX),
                    itemView.getBottom());
        }
        else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;

            int magicConstraint = (itemView.getRight() + ((int) dX) > iconLeft - iconMargin ? (int)dX + icon.getIntrinsicWidth() + ( iconMargin * 2) : 0);
            iconLeft += magicConstraint;
            iconRight += magicConstraint;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }

}
