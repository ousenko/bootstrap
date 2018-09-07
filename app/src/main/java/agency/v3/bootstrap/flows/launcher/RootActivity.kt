package agency.v3.bootstrap.flows.launcher

import agency.v3.bootstrap.BootstrapApp
import agency.v3.bootstrap.R
import agency.v3.bootstrap.di.component.ActivityComponent
import agency.v3.bootstrap.di.component.ActivityComponentOwner
import agency.v3.bootstrap.di.module.ActivityModule
import agency.v3.bootstrap.flows.symbols.SymbolsController
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import butterknife.ButterKnife
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Root Activity responsible for all low-level navigation facilities
 * */
class RootActivity : AppCompatActivity(), ActivityComponentOwner, RootView /*RootNavigator*/ {

    lateinit var router: Router
    private lateinit var activityComponent: ActivityComponent


    private val disposables = CompositeDisposable()


    @Inject
    internal lateinit var presenter: RootPresenter


    override val component: ActivityComponent get() = activityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        ButterKnife.bind(this)
        val container = findViewById<ViewGroup>(R.id.controller_container)
        router = Conductor.attachRouter(this, container, savedInstanceState)

        activityComponent = BootstrapApp.component.plus(ActivityModule(this, router))
        activityComponent.plus(RootModule()).inject(this)

        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(SymbolsController()))
        }

    }


    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }


    override fun onTrimMemory(level: Int) {
//        Glide.get(this).trimMemory(level) TODO: add Glide as Image loader
    }

    override fun onResume() {
        super.onResume()
        disposables.clear()
        disposables.add(presenter.attach(this))
    }

    override fun onPause() {
        disposables.clear()
        super.onPause()
    }

    override fun onDestroy() {
        disposables.clear()
        presenter.destroy()
        super.onDestroy()
    }

}
