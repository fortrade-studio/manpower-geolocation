
Errors :
⛏ java.lang.IllegalStateException: Fragment HomeFragment{7dc8398} (7c3cd933-4aab-4d02-a658-f7b4fbf38753)} not attached to a context.	at androidx.fragment.app.Fragment.requireContext(Fragment.java:805
        at com.fortradestudio.mapowergeolocationtracker.ui.HomeFragment.showDialog(HomeFragment.kt:105)
            at com.fortradestudio.mapowergeolocationtracker.ui.HomeFragment.access$showDialog(HomeFragment.kt:36)	
            at com.fortradestudio.mapowergeolocationtracker.ui.HomeFragment$onViewCreated$1.onLost(HomeFragment.kt:75)	
            at android.net.ConnectivityManager$CallbackHandler.handleMessage(ConnectivityManager.java:2922)
            at android.os.Handler.dispatchMessage(Handler.java:106)	at android.os.Looper.loop(Looper.java:173)	
    at android.os.HandlerThread.run(HandlerThread.java:65)

⛏  java.lang.NullPointerException: Parameter specified as non-null is null: method kotlin.jvm.internal.Intrinsics.checkNotNullParameter, parameter it	
    at com.fortradestudio.mapowergeolocationtracker.viewmodel.clockFragment.ClockFragmentViewModel$filterClockInOrClockOut$1$1.invoke(Unknown Source:2)	
    at com.fortradestudio.mapowergeolocationtracker.viewmodel.clockFragment.ClockFragmentViewModel$filterClockInOrClockOut$1$1.invoke(ClockFragmentViewModel.kt:34)	
    at com.fortradestudio.mapowergeolocationtracker.utils.CacheUtils$getUserData$1.invokeSuspend(CacheUtils.kt:21)	
    at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)	
    at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)	
    at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:571)	
    at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:738)	
    at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:678)	
at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:665)