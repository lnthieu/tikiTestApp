package app.android.tiki.database;

import android.database.Observable;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hieu.lenguyentrung on 5/4/19.
 */

public class HotSearchesFetcher {
    private static final String TAG = "HotSearchesFetcher";
    private static final String HOT_SEARCHES_DATA_URL = "https://raw.githubusercontent.com/tikivn/android-home-test/v2/keywords.json";
    private static HotSearchesFetcher singleInst = new HotSearchesFetcher();


    public synchronized static HotSearchesFetcher getSingleInst() {
        if (singleInst == null)
            singleInst = new HotSearchesFetcher();
        return singleInst;
    }


    private STATUS mStatus = STATUS.UNKNOWN;
    private final HotSearchesDataObservable mObservable = new HotSearchesDataObservable();
    private OkHttpClient mClient = new OkHttpClient();
    private ArrayList<String> mDataSet = new ArrayList<>();

    private HotSearchesFetcher() {
    }

    public void registerAdapterDataObserver(@NonNull HotSearchesDataObserver observer) {
        mObservable.registerObserver(observer);
    }

    public void unregisterAdapterDataObserver(@NonNull HotSearchesDataObserver observer) {
        mObservable.unregisterObserver(observer);
    }

    public STATUS getStatus() {
        return mStatus;
    }

    private void setStatus(STATUS status) {
        mStatus = status;
        mObservable.notifyFetchStatusChanged(mStatus);
    }

    public int getItemCount() {
        return mDataSet.size();
    }

    public String getItem(int position) {
        return position >= 0 && position <= mDataSet.size() - 1 ? mDataSet.get(position) : null;
    }

    public enum STATUS {
        UNKNOWN,
        FETCHING_DATA,
        FETCHING_DATA_FAILED,
        FETCHING_DATA_SUCCEED
    }

    private static class HotSearchesDataObservable extends Observable<HotSearchesDataObserver> {
        public void notifyFetchStatusChanged(STATUS status) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onFetchStatusChanged(status);
            }
        }
    }

    public abstract static class HotSearchesDataObserver {

        public void onFetchStatusChanged(STATUS status) {
            // do nothing
        }

    }


    /**
     * TODO add document
     */
    public void requestFetchingData() {

        switch (mStatus) {
            case FETCHING_DATA:
                return;
            case UNKNOWN:
            case FETCHING_DATA_FAILED:
                doFetchingData();
                return;
            case FETCHING_DATA_SUCCEED:
                mObservable.notifyFetchStatusChanged(STATUS.FETCHING_DATA_SUCCEED);
                return;
            default:
                throw new RuntimeException("To be implemented > " + HotSearchesFetcher.getSingleInst().getStatus());
        }


    }

    private void doFetchingData() {
        setStatus(STATUS.FETCHING_DATA);
        Request request = new Request.Builder()
                .url(HOT_SEARCHES_DATA_URL)
                .get()
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.w(TAG, e);
                setStatus(STATUS.FETCHING_DATA_FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                try {
                    String raw = response.body().string();
                    JSONArray jsonArray = new JSONArray(raw);
                    ArrayList<String> temp = new ArrayList<>();
                    for (int i = 0, len = jsonArray.length(); i < len; i++) {
                        temp.add(jsonArray.getString(i));
                    }
                    mDataSet = temp;
                    setStatus(STATUS.FETCHING_DATA_SUCCEED);
                } catch (Exception e) {
                    onFailure(call, new IOException(new Exception(response.toString())));
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }


            }
        });
    }
}
