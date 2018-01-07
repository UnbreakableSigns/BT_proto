package jiepastor.brailletouch_proto;

import android.inputmethodservice.InputMethodService;
import android.support.constraint.ConstraintLayout;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class IME extends InputMethodService {
    @Override
    public View onCreateInputView() {
//        SurfaceView inputView = new SurfaceView(this);
        View inputView = new SurfaceView(this);

        return inputView;
    }
}
