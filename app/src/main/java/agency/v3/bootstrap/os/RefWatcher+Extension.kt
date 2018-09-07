package agency.v3.bootstrap.os

import android.os.Build
import com.squareup.leakcanary.RefWatcher


//TODO: remove this method once crash will be fixed in leakcanary lib
fun RefWatcher.safeWatch(ref: Any?){
    ref?.let {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            watch(ref)
        }
    }
}
