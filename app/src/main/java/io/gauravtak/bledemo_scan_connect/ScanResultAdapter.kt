package io.gauravtak.bledemo_scan_connect

import android.bluetooth.le.ScanResult
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.gauravtak.bledemo_scan_connect.databinding.BleScanResultLayoutBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow


class ScanResultAdapter(private var list: MutableList<ScanResult>, private var isShowAdvertiseDetails: Boolean = false) :
    RecyclerView.Adapter<ScanResultAdapter.ScanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        return ScanViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        val currentItem = list[position]
        holder.bind(currentItem, isShowAdvertiseDetails)
    }

    override fun getItemCount(): Int = list.size

    fun setIsShowAdvertiseDetailsData(isShowAdvertiseDetails: Boolean) {
        this.isShowAdvertiseDetails = isShowAdvertiseDetails
        notifyDataSetChanged()
    }

    fun setData(scans: MutableList<ScanResult>) {
        list = scans
        notifyDataSetChanged()
    }

    class ScanViewHolder(private val binding: BleScanResultLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(scanResult: ScanResult, isShowAdvertiseDetails: Boolean) {
            with(binding) {
                this.result = scanResult
                if (isShowAdvertiseDetails)  {
                    this.advertiseDataString = getAdvertiseDataString(scanResult)
                } else {
                    this.advertiseDataString = null
                }

                val signal = when (calculateDistance(scanResult)) {
                    in 0.0..2.0 -> R.drawable.ic_network_4
                    in 2.0..4.0 -> R.drawable.ic_network_3
                    in 4.0..8.0 -> R.drawable.ic_network_2
                    else -> R.drawable.ic_network_1
                }

                ivSignalStrengthIcon.setImageResource(signal)
            }
        }

        private fun calculateDistance(scanResult: ScanResult): Double =
            10.0.pow((-69 - scanResult.rssi) / (10.0 * 2.0))

        private fun getAdvertiseDataString(scanResult: ScanResult): String {
            val rxTimestampMillis = System.currentTimeMillis() -
                    SystemClock.elapsedRealtime() +
                    scanResult.timestampNanos / 1000000
            val rxDate = Date(rxTimestampMillis)
            val time =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.US).format(rxDate)

            return "RSSI: ${scanResult.rssi}" +
                    "\n Scanned at $time" +
                    "\n Address : ${scanResult.device.address}"
        }

        companion object {
            fun from(parent: ViewGroup): ScanViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    BleScanResultLayoutBinding.inflate(layoutInflater, parent, false)
                return ScanViewHolder(binding)
            }
        }
    }
}
