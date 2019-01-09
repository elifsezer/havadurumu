package com.example.esezer.havadurumuapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        verileriGetir("İstanbul")
    }

    fun verileriGetir(sehir:String) {
        var url =
            "https://api.openweathermap.org/data/2.5/weather?q="+sehir+",tr&appid=f5a0343d74202c2d178fda5191bb250e&lang=tr&units=metric"
        var havaDurumuObje = JsonObjectRequest(Request.Method.GET, url, null, object : Response.Listener<JSONObject>,
            Response.ErrorListener {
            override fun onResponse(response: JSONObject?) {
                var main = response?.getJSONObject("main")
                var sicaklik = main?.getInt("temp")
                tvSicaklik.text = sicaklik.toString()


                var sehirAdi = response?.getString("name")


                var weather = response?.getJSONArray("weather")
                var aciklama = weather?.getJSONObject(0)?.getString("description")
                tvAciklama.text = aciklama

                var icon = weather?.getJSONObject(0)?.getString("icon")
                if (icon?.last() == 'd') {
                    rootLayout.background = getDrawable(R.drawable.bg)

                } else {
                    rootLayout.background = getDrawable(R.drawable.gece)
                    tvAciklama.setTextColor(resources.getColor(R.color.colorAccent))
                    tvSicaklik.setTextColor(resources.getColor(R.color.colorAccent))
                    tvTarih.setTextColor(resources.getColor(R.color.colorAccent))
                    tvDerece.setTextColor(resources.getColor(R.color.colorAccent))
                }
                var resimDosyaAdi = resources.getIdentifier("icon_" + icon?.sonKarakteriSil(), "drawable", packageName)
                imgHavaDurumu.setImageResource(resimDosyaAdi)
                tvTarih.text = tarihYazdir()

            }

            override fun onErrorResponse(error: VolleyError?) {
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {

            }
        })


        MySingleton.getInstance(this)?.addToRequestQueue(havaDurumuObje)

    }


    fun tarihYazdir(): String {
        var takvim = Calendar.getInstance().time
        var formatlayici = SimpleDateFormat("EEEE, MMM yyyy", Locale("tr"))
        var tarih = formatlayici.format(takvim)
        return tarih
    }

}

private fun String.sonKarakteriSil(): String {

    return this.substring(0, this.length - 1)
}
