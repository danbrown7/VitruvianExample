package com.example.vitruvianexample

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File
import java.util.concurrent.Executor


class FirstFragment : Fragment() {
    var databaseProvider : ExoDatabaseProvider? = null
    val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    @SuppressLint("WrongThread")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseProvider = ExoDatabaseProvider(requireContext())

        val url = "https://content.jwplatform.com/manifests/ZaLw9vYv.m3u8"
        val playerView = view.findViewById<StyledPlayerView>(R.id.player_view)

        var fileDir = context?.getExternalFilesDir(null);
        if (fileDir == null) {
            fileDir = context?.filesDir;
        }

        val downloadContentDirectory = File(fileDir, DOWNLOAD_CONTENT_DIRECTORY)
        var downloadCache = SimpleCache(
            downloadContentDirectory,
            NoOpCacheEvictor(),
            databaseProvider!!
        )

        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSourceFactory()

        val downloadExecutor = Executor { obj: Runnable -> obj.run() }
        val downloadManager = DownloadManager(
            requireContext(),
            databaseProvider!!,
            downloadCache,
            dataSourceFactory,
            downloadExecutor
        )

        var downloads = downloadManager.downloadIndex.getDownloads().count

        /*val downloadHelper = DownloadHelper.forMediaItem(
            requireContext(),
            MediaItem.fromUri(url),
            DefaultRenderersFactory(requireContext()),
            dataSourceFactory
        )

        downloadHelper.prepare(myCallback)*/

        if(downloads == 0){
            val downloadRequest: DownloadRequest = DownloadRequest.Builder(url, Uri.parse(url)).build()

            downloadManager.addDownload(downloadRequest)
        }

        val cacheDataSourceFactory: DataSource.Factory = CacheDataSource.Factory()
            .setCache(downloadCache)
            .setUpstreamDataSourceFactory(dataSourceFactory)
            .setCacheWriteDataSinkFactory(null) // Disable writing.

        val hlsMediaSource: HlsMediaSource = HlsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(
            MediaItem.fromUri(
                url
            )
        )

        var player = SimpleExoPlayer.Builder(requireContext()).build()

        playerView.player = player

        player.setMediaSource(hlsMediaSource)

        player.playWhenReady = true

        player.repeatMode = Player.REPEAT_MODE_ALL

        player.prepare()
    }
}