package jp.nk5.zipcodeapi

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.kittinunf.fuel.android.extension.responseJson

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity(), ZipcodeApiListener {

    private var executable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickTextView(view : View)
    {
        if (executable) IntentIntegrator(this@MainActivity).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data : Intent) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                accessAPI(result.getContents())
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun accessAPI(code: String)
    {
        if (!isValidZipCode(code)) return

        lockUI()
        val url = "http://zipcloud.ibsnet.co.jp/api/search"

        url.httpGet(listOf("zipcode" to code)).responseJson { request, response, result ->
            when (result) {
            // ステータスコード 2xx
                is Result.Success -> {
                    parseJSON(result.value.obj())
                }
            // ステータスコード 2xx以外
                is Result.Failure -> {
                    // エラー処理
                }
            }
        }
    }

    private fun parseJSON(result: JSONObject)
    {
        val results = result.get("results") as JSONArray
        val data1 = results[0] as JSONObject
        val returnString = data1.getString("address1") + data1.getString("address2") + data1.getString("address3")
        updateUI(returnString)
        unlockUI()
    }

    private fun isValidZipCode(code: String) : Boolean
    {
        if (code.length != 7) return false
        try {
            Integer.parseInt(code)
        } catch (e : Exception) {
            return false
        }
        return true
    }

    override fun lockUI()
    {
        executable = false
    }

    override fun unlockUI()
    {
        executable = true
    }

    override fun updateUI(returnString: String)
    {
        textView1.text = String.format(Locale.JAPANESE, "それは%sの郵便番号", returnString)
    }


}
