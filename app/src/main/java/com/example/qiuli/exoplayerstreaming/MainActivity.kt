package com.example.qiuli.exoplayerstreaming

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashChunkSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , Player.EventListener {
    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {

    }

    override fun onPlayerError(error: ExoPlaybackException?) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if(playbackState==Player.STATE_BUFFERING)
            progressBar.visibility = View.VISIBLE
        else if(playbackState === Player.STATE_READY)
            progressBar.visibility = View.INVISIBLE
    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPositionDiscontinuity() {

    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {

    }

    private lateinit var simpleExoPlayer:SimpleExoPlayer
    private  var playbackPosition = 0L
    private val dashurl = "http://rdmedia.bbc.co.uk/dash/ondemand/bbb/2/client_manifest-separate_init.mpd"
    private  val bandwidthMeter by lazy {
        DefaultBandwidthMeter()

    }

    private  val adaptiveTrackSelectionFactory by lazy{
        AdaptiveTrackSelection.Factory(bandwidthMeter)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override  fun onStart(){
        super.onStart()

        initializeExoplayer()
    }

    override fun onStop() {
        releaseExoplayer()
        super.onStop()
    }

    private  fun initializeExoplayer(){
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this),
                DefaultTrackSelector(adaptiveTrackSelectionFactory),
                DefaultLoadControl()
        )
        prepareExoPlayer()
        simpleExoPlayerView.player = simpleExoPlayer
        simpleExoPlayer.seekTo(playbackPosition)
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.addListener(this)
    }

    private fun releaseExoplayer(){
        playbackPosition = simpleExoPlayer.currentPosition
        simpleExoPlayer.release()
    }

    private  fun buildMediaSource(uri:Uri): MediaSource{
        val dataSourceFactory = DefaultHttpDataSourceFactory("ua",bandwidthMeter)
        val dashChunkSourceFactory = DefaultDashChunkSource.Factory(dataSourceFactory)
        return DashMediaSource(uri,dataSourceFactory,dashChunkSourceFactory,null,null)
    }

    private  fun prepareExoPlayer(){
        val uri = Uri.parse(dashurl)
        val mediaSource = buildMediaSource(uri)
        simpleExoPlayer.prepare(mediaSource)
    }
}
