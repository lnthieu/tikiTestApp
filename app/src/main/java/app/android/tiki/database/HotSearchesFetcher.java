package app.android.tiki.database;

import android.database.Observable;
import android.support.annotation.NonNull;

/**
 * Created by hieu.lenguyentrung on 5/4/19.
 */

public class HotSearchesFetcher {

    private static HotSearchesFetcher singleInst = new HotSearchesFetcher();


    public synchronized static HotSearchesFetcher getSingleInst() {
        if (singleInst == null)
            singleInst = new HotSearchesFetcher();
        return singleInst;
    }


    private STATUS mStatus = STATUS.UNKNOWN;
    private final HotSearchesDataObservable mObservable = new HotSearchesDataObservable();

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
        return 0;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setStatus(STATUS.FETCHING_DATA_FAILED);
            }
        }).start();
    }
}
