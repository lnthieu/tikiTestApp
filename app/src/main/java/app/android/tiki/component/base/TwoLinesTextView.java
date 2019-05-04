package app.android.tiki.component.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by hieu.lenguyentrung on 5/5/19.
 */

public class TwoLinesTextView extends AppCompatTextView {
    public TwoLinesTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TwoLinesTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TwoLinesTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        int[] set = {
                android.R.attr.text
        };
        TypedArray a = context.obtainStyledAttributes(attrs, set);
        CharSequence text = a.getText(0);
        a.recycle();

        setTextAutoWrap(text.toString());
    }


    public void setTextAutoWrap(String text) {
        setText(wrapText(text));
    }

    private int findPosNearCenter(int left, int center, int right) {

        if (0 <= left
                && left <= center
                && center <= right) {
            int l2c = center - left;
            int c2r = right - center;
            return l2c < c2r ? left : right;
        } else {
            throw new RuntimeException("invalid params");
        }
    }

    private String wrapText(final String text) {
        final Character SPACE = ' ';
        final Character NEW_LINE = '\n';
        String result = text;
        if (text != null && text.indexOf(SPACE) > 0) {
            int wrapPos = -1;
            int centerPos = text.length() / 2;
            int firstSpaceFromCenter = text.substring(0, centerPos).lastIndexOf(SPACE);
            int lastSpaceToCenter = text.indexOf(SPACE, centerPos);

            if (text.charAt(centerPos) == SPACE) {
                wrapPos = centerPos;
            } else if (firstSpaceFromCenter > 0 && lastSpaceToCenter > 0) {
                wrapPos = findPosNearCenter(firstSpaceFromCenter, centerPos, lastSpaceToCenter);
            } else if (firstSpaceFromCenter < 0) {
                wrapPos = lastSpaceToCenter;
            } else if (lastSpaceToCenter < 0) {
                wrapPos = firstSpaceFromCenter;
            }

            if (wrapPos > 0) {
                result = text.substring(0, wrapPos)
                        + NEW_LINE
                        + text.substring(wrapPos + 1);

            }
        }
        return result;
    }
}
