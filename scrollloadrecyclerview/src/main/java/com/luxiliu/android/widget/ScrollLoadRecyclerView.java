package com.luxiliu.android.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


public class ScrollLoadRecyclerView extends RecyclerView {
    private boolean mLoading = false;
    private boolean mLoadingEnabled = true;

    @Nullable
    private OnLoadListener mOnLoadListener;

    public interface OnLoadListener {
        void onLoad();
    }

    public ScrollLoadRecyclerView(Context context) {
        this(context, null);
    }

    public ScrollLoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollLoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnLoadListener(@Nullable OnLoadListener onLoadListener) {
        mOnLoadListener = onLoadListener;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        if (mLoadingEnabled && !mLoading) {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                if (gridLayoutManager.findLastVisibleItemPosition() / gridLayoutManager.getSpanCount()
                        == (gridLayoutManager.getItemCount() - 1) / gridLayoutManager.getSpanCount()) {
                    setLoading(true, true);
                }
            } else {
                throw new IllegalStateException("Use GridLayoutManager");
            }
        }
    }

    public void setLoading(boolean loading) {
        setLoading(loading, false);
    }

    public void setLoadingEnabled(boolean loadingEnabled) {
        if (mLoadingEnabled != loadingEnabled) {
            mLoadingEnabled = loadingEnabled;

            Adapter adapter = (Adapter) getAdapter();
            adapter.progressVisibility = mLoadingEnabled ? VISIBLE : GONE;

            if (adapter.progressVisibility == VISIBLE) {
                getAdapter().notifyItemInserted(adapter.getItemCount());
            } else {
                getAdapter().notifyItemRemoved(adapter.getItemCount());
            }
        }
    }

    private void setLoading(boolean loading, boolean notify) {
        mLoading = loading;

        if (notify && mOnLoadListener != null) {
            mOnLoadListener.onLoad();
        }
    }

    public abstract static class Adapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {
        private static final int VIEW_TYPE_PROGRESS = ProgressViewHolder.class.hashCode();

        private int progressVisibility = VISIBLE;

        @Override
        public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_PROGRESS) {
                ProgressBar progressBar = new ProgressBar(parent.getContext(), null, android.R.attr.progressBarStyleSmall);
                progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                float paddingInDp = 16;
                int paddingInPx = (int) (paddingInDp * parent.getContext().getResources().getDisplayMetrics().density);
                progressBar.setPadding(0, paddingInPx, 0, paddingInPx);

                //noinspection unchecked
                return (VH) new ProgressViewHolder(progressBar);
            } else {
                return onCreateDataViewHolder(parent, viewType);
            }
        }

        @Override
        public final void onBindViewHolder(VH holder, int position) {
            if (!(holder instanceof ProgressViewHolder)) {
                onBindDataViewHolder(holder, position);
            }
        }

        @Override
        public final int getItemViewType(int position) {
            if (position < getDataItemCount()) {
                return super.getItemViewType(position);
            } else {
                return VIEW_TYPE_PROGRESS;
            }
        }

        @Override
        public final int getItemCount() {
            if (getDataItemCount() == 0) {
                return 0;
            } else {
                if (progressVisibility == VISIBLE) {
                    return getDataItemCount() + 1;
                } else {
                    return getDataItemCount();
                }
            }
        }

        public abstract VH onCreateDataViewHolder(ViewGroup parent, int viewType);

        public abstract void onBindDataViewHolder(VH holder, int position);

        public abstract int getDataItemCount();

        public abstract int getDataItemViewType();
    }

    private static class ProgressViewHolder extends ViewHolder {

        ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }
}
