package apps.nerdyginger.pocketpantry;

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

import apps.nerdyginger.pocketpantry.adapters.RecipesListAdapter;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;
import apps.nerdyginger.pocketpantry.view_models.RecipeViewModel;

//Custom callback class to handle swipe-to-delete operations for recipes.
//NOTE: Will only delete from recipe box, custom (and read-only) recipes will remain in respective db tables.
//Last edited: 11/4/19
public class RecipeSwipeDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private RecipesListAdapter mAdapter;
    private RecipeViewModel mModel;
    private Context mContext;
    private Drawable icon;
    private final ColorDrawable background;

    public RecipeSwipeDeleteCallback(RecipesListAdapter adapter, Context context, RecipeViewModel viewModel) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        mContext = context;
        mModel = viewModel;
        icon = ContextCompat.getDrawable(context, R.drawable.ic_delete); //TODO: credit icons!//TODO: credit icons!
        background = new ColorDrawable(Color.RED); // trash icon made by Kiranshastry on flaticon.com
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder holder, int direction) {
        int position = holder.getAdapterPosition();
        final UserRecipeBoxItem deletedItem = mAdapter.deleteItem(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserCustomDatabase db = UserCustomDatabase.getDatabase(mContext);
                db.getUserRecipeBoxDao().delete(deletedItem);
            }
        }).start();
        mModel.deleteRecipe(deletedItem);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder holder, @NonNull RecyclerView.ViewHolder holder2) {
        return true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder holder,
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
