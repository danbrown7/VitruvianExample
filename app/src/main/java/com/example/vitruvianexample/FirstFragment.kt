package com.example.vitruvianexample

import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.danikula.videocache.HttpProxyCacheServer
import com.danikula.videocache.ProxyCacheException
import java.io.FileNotFoundException
import java.lang.Exception


class FirstFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = "https://content.jwplatform.com/manifests/ZaLw9vYv.m3u8"
        val videoView = view.findViewById<VideoView>(R.id.video)

        try {
            val proxy: HttpProxyCacheServer? = (activity as MainActivity).getProxy()
            val proxyUrl = proxy?.getProxyUrl(url)
            videoView.setVideoPath(proxyUrl)
            videoView.start()
        }
        catch(e : Exception){
            videoView.setVideoPath(url)
            videoView.start()
        }

        videoView.setOnCompletionListener(OnCompletionListener { videoView.start() })
    }
}