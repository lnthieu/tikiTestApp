package app.android.tiki;

import android.content.Context;
import android.os.Handler;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import app.android.tiki.component.hotsearches.HotSearchesList;
import app.android.tiki.database.HotSearchesFetcher;

/**
 * Created by hieu.lenguyentrung on 5/5/19.
 */
@RunWith(AndroidJUnit4.class)
public class HotSearchesListTest {

    private Handler handler;
    private static final Object testDone = new Object();

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getTargetContext();
        handler = new Handler(appContext.getMainLooper());
    }

    /**
     * Pass if app does not throw the exception
     *
     * @throws Exception
     */
    @Test
    public void testMultiViews() throws Exception {

        final Context appContext = InstrumentationRegistry.getTargetContext();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < 10) {
                    HotSearchesList v = new HotSearchesList(appContext);
                    i++;
                }
                synchronized (testDone) {
                    testDone.notify();
                }
            }
        });
        synchronized (testDone) {
            testDone.wait();
        }
    }

}
