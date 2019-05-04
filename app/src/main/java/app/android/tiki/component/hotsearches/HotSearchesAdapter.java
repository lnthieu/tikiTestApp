package app.android.tiki.component.hotsearches;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.android.tiki.R;
import app.android.tiki.component.base.TwoLinesTextView;
import app.android.tiki.database.HotSearchesFetcher;

/**
 * Created by hieu.lenguyentrung on 5/4/19.
 */

public class HotSearchesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "HotSearchesAdapter";

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_RETRY = 1;
    private static final int VIEW_TYPE_NORMAL = 2;

    private Handler uiHandler;
    private HotSearchesFetcher.FetchingDataObserver hotSearchesDataObserver;

    public HotSearchesAdapter() {
        uiHandler = new Handler(Looper.myLooper());
        hotSearchesDataObserver = new HotSearchesFetcher.FetchingDataObserver() {
            @Override
            public void onStatusChanged(HotSearchesFetcher.FETCHING_STATUS status) {
                super.onStatusChanged(status);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("HotSearchesAdapter", "status changed");
                        notifyDataSetChanged();
                    }
                });
            }
        };

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        HotSearchesFetcher.getSingleInst().registerFetcherDataObserver(hotSearchesDataObserver);
        HotSearchesFetcher.getSingleInst().requestFetchingData();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        HotSearchesFetcher.getSingleInst().unregisterFetcherDataObserver(hotSearchesDataObserver);
    }

    @Override
    public int getItemViewType(int position) {
        switch (HotSearchesFetcher.getSingleInst().getStatus()) {
            case FAILED:
                return VIEW_TYPE_RETRY;
            case SUCCEED:
                return VIEW_TYPE_NORMAL;
            default:
                return VIEW_TYPE_LOADING;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder vh;
        switch (viewType) {
            case VIEW_TYPE_LOADING:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotsearches_loading, parent, false);
                vh = new LoadingHolder(v);
                return vh;
            case VIEW_TYPE_RETRY:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotsearches_retry, parent, false);
                vh = new RetryHolder(v);
                return vh;
            case VIEW_TYPE_NORMAL:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotsearches, parent, false);
                vh = new HotSearchHolder(v);
                return vh;
            default:
                Log.e(TAG, "Can not create view holder for view type " + viewType);
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HotSearchHolder) {
            ((HotSearchHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        switch (HotSearchesFetcher.getSingleInst().getStatus()) {
            case FETCHING:
            case FAILED:
                return 1;
            case SUCCEED:
                return HotSearchesFetcher.getSingleInst().getItemCount();
            default:
                return 0;
        }
    }

    /**
     *
     */
    public static class LoadingHolder extends RecyclerView.ViewHolder {
        public LoadingHolder(View v) {
            super(v);
        }
    }

    /**
     *
     */
    public static class RetryHolder extends RecyclerView.ViewHolder {
        public RetryHolder(View v) {
            super(v);
            v.findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HotSearchesFetcher.getSingleInst().requestFetchingData();
                }
            });
        }
    }

    /**
     *
     */
    public static class HotSearchHolder extends RecyclerView.ViewHolder {
        private View view;
        private TwoLinesTextView twoLineTextView;

        public HotSearchHolder(View v) {
            super(v);
            view = v;
            twoLineTextView = view.findViewById(R.id.key_words);

        }

        public void bind(int position) {
            String keywords = HotSearchesFetcher.getSingleInst().getItem(position);

            twoLineTextView.setTextAutoWrap(keywords);

            view.setVisibility(keywords == null ? View.GONE : View.VISIBLE);
        }


    }

}
