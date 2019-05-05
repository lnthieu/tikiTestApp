package app.android.tiki;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import app.android.tiki.component.base.TwoLinesTextView;

/**
 * Created by hieu.lenguyentrung on 5/5/19.
 */
@RunWith(AndroidJUnit4.class)
public class TwoLinesTextViewTest {

    private TwoLinesTextView targetTest;

    @Before
    public void setUp() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        targetTest = new TwoLinesTextView(appContext);

    }

    @Test
    public void setTextAutoWrap_noSpace() throws Exception {
        doTest("abc", "abc");
    }

    @Test
    public void setTextAutoWrap_onceSpaceAtStart() throws Exception {
        doTest(" abc", "\nabc");
    }

    @Test
    public void setTextAutoWrap_onceSpaceAtEnd() throws Exception {
        doTest("abc ", "abc\n");
    }

    @Test
    public void setTextAutoWrap_wrapAtCenter() throws Exception {
        doTest("a b c a b c", "a b c\na b c");
    }

    @Test
    public void setTextAutoWrap_wrap_theFirstLineIsLonger() throws Exception {
        doTest("a b*c_a b c", "a b*c_a\nb c");
    }

    @Test
    public void setTextAutoWrap_wrap_theFirstLineIsShorter() throws Exception {
        doTest("a b c_a*b c", "a b\nc_a*b c");
    }

    private void doTest(String input, String output) throws Exception {
        targetTest.setTextAutoWrap(input);
        assertEquals(output, targetTest.getText());
    }

}