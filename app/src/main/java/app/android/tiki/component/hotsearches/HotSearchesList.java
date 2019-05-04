package app.android.tiki.component.hotsearches;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by hieu.lenguyentrung on 5/4/19.
 */

public class HotSearchesList extends RecyclerView {


    private HotSearchesAdapter mAdapter;

    public HotSearchesList(Context context) {
        super(context);
        init(context, null, 0);

    }

    public HotSearchesList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public HotSearchesList(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);

    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyle) {

        // for layout
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(layoutManager);

        // for adapter
        mAdapter = new HotSearchesAdapter();
        setAdapter(mAdapter);
    }

}
