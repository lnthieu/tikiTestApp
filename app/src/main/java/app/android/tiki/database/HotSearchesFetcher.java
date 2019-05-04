package app.android.tiki.database;

import android.database.Observable;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;

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
    private static HotSearchesFetcher singleInst;

    public synchronized static HotSearchesFetcher getSingleInst() {
        if (singleInst == null)
            singleInst = new HotSearchesFetcher();
        return singleInst;
    }


    private FETCHING_STATUS fetchingStatus = FETCHING_STATUS.UNKNOWN;
    private final FetchingDataObservable fetchingDataObservable = new FetchingDataObservable();
    private OkHttpClient clientFetchData = new OkHttpClient();
    private ArrayList<String> dataSet = new ArrayList<>();

    private HotSearchesFetcher() {
    }

    public void registerFetcherDataObserver(@NonNull FetchingDataObserver observer) {
        fetchingDataObservable.registerObserver(observer);
    }

    public void unregisterFetcherDataObserver(@NonNull FetchingDataObserver observer) {
        fetchingDataObservable.unregisterObserver(observer);
    }

    public FETCHING_STATUS getStatus() {
        return fetchingStatus;
    }

    private void setStatus(FETCHING_STATUS status) {
        fetchingStatus = status;
        fetchingDataObservable.notifyStatusChanged(fetchingStatus);
    }

    public int getItemCount() {
        return dataSet.size();
    }

    public String getItem(int position) {
        return position >= 0 && position <= dataSet.size() - 1 ? dataSet.get(position) : null;
    }


    /**
     *
     */
    public void requestFetchingData() {


        switch (fetchingStatus) {

            case FETCHING:
                Log.d(TAG, "data is being fetched.");
                return;

            case SUCCEED:
                Log.d(TAG, "Data has been fetched.");
                fetchingDataObservable.notifyStatusChanged(FETCHING_STATUS.SUCCEED);
                return;

            case UNKNOWN:
            case FAILED:
            default:
                Log.d(TAG, "Data not yet fetched.");
                doFetchingData();
                return;
        }
    }

    private void doFetchingData() {

        if (fetchingStatus == FETCHING_STATUS.FETCHING) {
            throw new RuntimeException("data is being fetched.");
        }


        setStatus(FETCHING_STATUS.FETCHING);

        Request request = new Request.Builder()
                .url(HOT_SEARCHES_DATA_URL)
                .get()
                .build();

        clientFetchData.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.w(TAG, e);
                        setStatus(FETCHING_STATUS.FAILED);
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
                            dataSet = temp;
                            setStatus(FETCHING_STATUS.SUCCEED);
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


    public enum FETCHING_STATUS {
        UNKNOWN,
        FETCHING,
        FAILED,
        SUCCEED
    }

    private static class FetchingDataObservable extends Observable<FetchingDataObserver> {
        public void notifyStatusChanged(FETCHING_STATUS status) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onStatusChanged(status);
            }
        }
    }

    public abstract static class FetchingDataObserver {

        public void onStatusChanged(FETCHING_STATUS status) {
            // do nothing
        }

    }
}
