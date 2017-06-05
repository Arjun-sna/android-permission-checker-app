package in.arjsna.permissionchecker.lib;

/**
 * Created by arjun on 9/12/16.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import in.arjsna.permissionchecker.R;
import java.util.HashMap;
import java.util.Map;

public class TypeFaceTextView extends AppCompatTextView {

  private final static Map<String, Typeface> inflatedFonts = new HashMap<>();

  public TypeFaceTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs);
  }

  public TypeFaceTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public TypeFaceTextView(Context context) {
    super(context);
    init(null);
  }

  private void init(AttributeSet attrs) {
    if (attrs != null) {
      TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TypeFaceTextView);
      String fontName = a.getString(R.styleable.TypeFaceTextView_fontName);
      if (fontName != null) {
        Typeface myTypeface = inflatedFonts.get(fontName);
        if (myTypeface == null) {
          myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
          inflatedFonts.put(fontName, myTypeface);
        }
        setTypeface(myTypeface);
      }
      a.recycle();
    }
  }
}