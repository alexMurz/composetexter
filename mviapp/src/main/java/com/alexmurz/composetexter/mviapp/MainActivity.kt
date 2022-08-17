package com.alexmurz.composetexter.mviapp

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.alexmurz.composetexter.mviapp.components.messagelist.MessageListFragment
import com.alexmurz.composetexter.mviapp.components.topiclist.TopicListFragment
import com.alexmurz.composetexter.mviapp.nav_host.AppNav
import com.alexmurz.composetexter.mviapp.nav_host.Destination
import com.alexmurz.messages.model.MessageChainParent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), KoinComponent {
    private lateinit var rootFrame: FrameLayout

    private val nav: AppNav by inject()

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rootFrame = findViewById(R.id.root_frame)
    }

    override fun onResume() {
        super.onResume()
        compositeDisposable.add(
            nav.observeDestination()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::openDestination)
        )
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    private fun openDestination(destination: Destination) {
        Log.i(TAG, "openDestination: navigate to $destination")
        when (destination) {
            Destination.TopicList -> showTopicListFragment()
            is Destination.MessageList -> showMessageList(destination.messageParent)
        }
    }

    private fun showTopicListFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.root_frame, TopicListFragment.create(), TopicListFragment.FRAGMENT_TAG)
            .commit()
    }

    private fun showMessageList(parent: MessageChainParent) {
        val enter = com.google.android.material.R.anim.abc_grow_fade_in_from_bottom
        val exit = com.google.android.material.R.anim.abc_shrink_fade_out_from_bottom

        supportFragmentManager
            .beginTransaction()
            .addToBackStack(MessageListFragment.nameFor(parent))
            .setCustomAnimations(enter, exit, enter, exit)
            .add(R.id.root_frame, MessageListFragment(parent))
            .commit()
    }

}