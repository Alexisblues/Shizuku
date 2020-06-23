package moe.shizuku.manager.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import hidden.HiddenApiBridge
import moe.shizuku.manager.Helps
import moe.shizuku.manager.R
import moe.shizuku.manager.adb.AdbPairingClient
import moe.shizuku.manager.databinding.HomeStartWirelessAdbBinding
import moe.shizuku.manager.ktx.toHtml
import moe.shizuku.manager.starter.StarterActivity
import rikka.core.util.BuildUtils
import rikka.html.text.HtmlCompat
import rikka.recyclerview.BaseViewHolder
import rikka.recyclerview.BaseViewHolder.Creator
import java.net.InetAddress

class StartWirelessAdbViewHolder(binding: HomeStartWirelessAdbBinding) : BaseViewHolder<Any?>(binding.root) {

    companion object {
        val CREATOR = Creator<Any> { inflater: LayoutInflater, parent: ViewGroup? -> StartWirelessAdbViewHolder(HomeStartWirelessAdbBinding.inflate(inflater, parent, false)) }
    }

    init {
        binding.button1.setOnClickListener { v: View ->
            onAdbClicked(v.context)
        }
        binding.text1.movementMethod = LinkMovementMethod.getInstance()
        binding.text1.text = context.getString(R.string.home_wireless_adb_description, Helps.ADB_ANDROID11.get()).toHtml(HtmlCompat.FROM_HTML_OPTION_TRIM_WHITESPACE)
        if (BuildUtils.atLeast30) {
            binding.button2.setOnClickListener { v: View ->
                onPairClicked(v.context)
            }
        } else {
            binding.button2.isVisible = false
        }
    }

    @SuppressLint("NewApi")
    private fun onAdbClicked(context: Context) {
        var port = HiddenApiBridge.SystemProperties_getInt("service.adb.tcp.port", -1)
        if (port == -1) port = HiddenApiBridge.SystemProperties_getInt("persist.adb.tcp.port", -1)

        when {
            port > 0 -> {
                val host = InetAddress.getLoopbackAddress().hostName
                val intent = Intent(context, StarterActivity::class.java).apply {
                    putExtra(StarterActivity.EXTRA_IS_ROOT, false)
                    putExtra(StarterActivity.EXTRA_HOST, host)
                    putExtra(StarterActivity.EXTRA_PORT, port)
                }
                context.startActivity(intent)
            }
            BuildUtils.atLeast30 -> {
                AdbDialogFragment().show((context as FragmentActivity).supportFragmentManager)
            }
            else -> {
                WadbNotEnabledDialogFragment().show((context as FragmentActivity).supportFragmentManager)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun onPairClicked(context: Context) {
        if (AdbPairingClient.available()) {
            AdbPairDialogFragment().show((context as FragmentActivity).supportFragmentManager)
        } else {
            Toast.makeText(context, "Paring is not available on this device.", Toast.LENGTH_LONG).apply { setGravity(Gravity.CENTER, 0, 0) }.show()
        }
    }
}