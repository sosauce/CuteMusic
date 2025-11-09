@file:OptIn(DelicateCoroutinesApi::class)

package com.sosauce.cutemusic.data

import android.content.Context
import android.media.audiofx.Equalizer
import com.sosauce.cutemusic.data.datastore.getEqBands
import com.sosauce.cutemusic.data.datastore.saveEqBands
import com.sosauce.cutemusic.utils.EqBand
import com.sosauce.cutemusic.utils.copyMutate
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CuteEqualizer(
    private val context: Context
) {
    private var equalizer: Equalizer? = null
    fun initEqualizer(audioSessionId: Int) {
        equalizer?.release()
        equalizer = Equalizer(0, audioSessionId).apply {
            enabled = true

            GlobalScope.launch(Dispatchers.IO) {
                val eqBands = getEqBands(context).first()

                if (eqBands.isEmpty()) {
                    firstTimeSettingUp(this@apply)
                } else {
                    eqBands.forEach { (frequencies, decibel) ->
                        setBandLevel(frequencies.substringBefore("-").toInt(), decibel)
                    }
                }
            }
        }
    }

    suspend fun setBandLevel(
        frequency: Int,
        level: Float
    ) {
        val levelMb = (level * 100).toInt().toShort()
        val band = equalizer?.getBand(frequency * 1000) ?: return


        equalizer?.setBandLevel(band, levelMb)


        // Need to rewrite some of this logic but too lazy rn
        val eqBands = getEqBands(context).first()
        val index = eqBands.indexOfFirst { it.first.substringBefore("-") == frequency.toString() }
        if (index != -1) {
            val newList = eqBands.copyMutate {
                this[index] = this[index].copy(second = level)
            }

            saveEqBands(context, newList)
        }
    }

    fun clearEqualizer() {
        equalizer?.release()
    }

    suspend fun resetBands() {

        val eqBands = getEqBands(context).first()

        for (i in 0 until equalizer!!.numberOfBands) {
            val band = i.toShort()
            equalizer?.setBandLevel(band, 0)
        }

        saveEqBands(context, eqBands.copyMutate { replaceAll { it.copy(second = 0.0f) } })
    }

    private suspend fun firstTimeSettingUp(equalizer: Equalizer) {

        val tempEqBands = mutableListOf<EqBand>()

        for (i in 0 until equalizer.numberOfBands) {
            val band = i.toShort()
            val frequencyRange = equalizer.getBandFreqRange(band).joinToString("-") { milliHertz ->
                (milliHertz / 1000).toString()
            }
            val decibel = equalizer.getBandLevel(band) / 1000


            tempEqBands.add(
                EqBand(frequencyRange, decibel.toFloat())
            )
        }

        saveEqBands(context, tempEqBands)
    }
}

