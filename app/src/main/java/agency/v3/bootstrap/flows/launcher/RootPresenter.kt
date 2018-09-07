package agency.v3.bootstrap.flows.launcher

import agency.v3.bootstrap.core.mvp.Presenter
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.EmptyDisposable
import javax.inject.Inject

internal class RootPresenter @Inject internal constructor():
        Presenter<RootView, RootPresenter.ScreenState>(ScreenState()) {

    override fun onInit() {
        //TODO
    }


    override fun onAttach(view: RootView): Disposable {
        return ioBinding {
            add(
                    //TODO
                    EmptyDisposable.INSTANCE
            )
        }
    }

    class ScreenState
}