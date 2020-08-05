package com.xiaoniu.cleanking.utils.audio

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.utils.LogUtils
import com.xiaoniu.common.utils.ContextUtils

class SoundPoolPlayer {

    private var soundGoldCoin: Int
    private val audioBuild = AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build()
    private var soundPool: SoundPool = SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioBuild).build()

    init {
        soundGoldCoin = soundPool.load(ContextUtils.getContext(), R.raw.gold_coin, 1)
        LogUtils.e("================soundplayer 加载完成")
    }


    fun playGoldCoin() {
        soundPool.play(soundGoldCoin, 1.0f, 1.0f, 0, 0, 1.0f)
        LogUtils.e("================soundplayer 播放完成")
    }

    fun release() {
        soundPool.release();
    }
}