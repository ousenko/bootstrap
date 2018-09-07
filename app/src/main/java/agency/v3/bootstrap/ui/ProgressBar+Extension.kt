package agency.v3.bootstrap.ui

import android.view.View
import android.widget.ProgressBar


fun ProgressBar.loading(inProgress: Boolean){
    visibility = if(inProgress) View.VISIBLE else View.GONE
}