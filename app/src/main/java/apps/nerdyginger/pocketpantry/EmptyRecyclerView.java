package apps.nerdyginger.pocketpantry;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/*
 * A class to make empty RecyclerViews a little bit more user-friendly.
 * Thanks to:
 * https://alexzh.com/2017/02/05/how-to-setemptyview-to-recyclerview/
 *
 * NOTE1: MUST set adapter before calling setEmptyView to avoid issues
 * NOTE2: Added animation to empty view to help with jerky data filling
 *        (otherwise, empty view appears for split second before data)
 */
public class EmptyRecyclerView extends RecyclerView {
    private View emptyView;

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, @NonNull AttributeSet attributes) {
        super(context, attributes);
    }

    public EmptyRecyclerView(Context context, @NonNull AttributeSet attributes, int defStyle) {
        super(context, attributes, defStyle);
    }

    private void initEmptyView() {
        if (emptyView != null) {
            if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_menu_open);
                emptyView.setVisibility(VISIBLE);
                emptyView.startAnimation(anim);
            } else {
                emptyView.clearAnimation();
                emptyView.setVisibility(GONE);
            }
            emptyView.setVisibility(
                    getAdapter() == null || getAdapter().getItemCount() == 0 ? VISIBLE : GONE);
            EmptyRecyclerView.this.setVisibility(
                    getAdapter() == null || getAdapter().getItemCount() == 0 ? GONE : VISIBLE);
        }
    }

    final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            initEmptyView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            initEmptyView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            initEmptyView();
        }
    };

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        super.setAdapter(adapter);

        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }

        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    public void setEmptyView(View view) {
        emptyView = view;
        initEmptyView();
    }

    public View getEmptyView() {
        return emptyView;
    }
}
