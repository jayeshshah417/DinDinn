package com.jayesh.dindinntest.utils

import android.content.Context
import android.media.*
import android.provider.Settings

import android.net.Uri
import android.speech.tts.TextToSpeech
import android.widget.EditText
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit
import android.media.ToneGenerator

import android.media.AudioManager





object OConstants {
     val MAIN_URL:String = "https://9f061f1f-b838-48df-b0f8-f2f36b1c1d1c.mock.pstmn.io"
    val ORDERS_URL:String = "/incredients"

    fun playTone(){
        val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 10000)
        tg.startTone(ToneGenerator.TONE_SUP_RINGTONE)
        tg.release()    }


    fun speakOut(tts:TextToSpeech,str_speak:String) {
        val text = str_speak
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
        /*if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }*/
    }

    fun getJsonFromAssets(context: Context, fileName: String?): String? {
        val jsonString: String
        jsonString = try {
            val `is`: InputStream = context.assets.open(fileName!!)
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return jsonString
    }

    fun getMins(milliseconds:Long): Long {
       return TimeUnit.MILLISECONDS.toMinutes(milliseconds)
    }
    fun getSecs(milliseconds:Long): Long {
        return (TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(
                        milliseconds
                    )
                ))
    }

}