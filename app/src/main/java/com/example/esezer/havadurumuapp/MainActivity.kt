package com.example.esezer.havadurumuapp

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import im.delight.android.location.SimpleLocation
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var tvSehir: TextView? = null
    var location: SimpleLocation? = null
    var latitude: String? = null
    var longitude: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var spinnerAdapter =
            ArrayAdapter.createFromResource(this, R.array.sehirler, R.layout.spinner_tek_satir)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSehirler.adapter = spinnerAdapter
        spnSehirler.onItemSelectedListener = this
        spnSehirler.setSelection(1)
        verileriGetir("Ankara")


    }

    private fun oankiSehriGetir(lat: String?, longt: String?) {
        var url =
            "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + longt + "&appid=f5a0343d74202c2d178fda5191bb250e&lang=tr&units=metric"
        var sehirAdi: String? = null
        var havaDurumuObje2 = JsonObjectRequest(Request.Method.GET, url, null, object : Response.Listener<JSONObject>,
            Response.ErrorListener {
            override fun onResponse(response: JSONObject?) {
                var main = response?.getJSONObject("main")
                var sicaklik = main?.getInt("temp")
                tvSicaklik.text = sicaklik.toString()


                sehirAdi = response?.getString("name")
                tvSehir!!.text = sehirAdi.toString()


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
                    tvSehir!!.setTextColor(resources.getColor(R.color.colorAccent))
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

        MySingleton.getInstance(this)?.addToRequestQueue(havaDurumuObje2)

    }


    //adaptor boş odlguunda tetiklenir.
    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        tvSehir = view as TextView
        if (position == 0) {
           location= SimpleLocation(this)

            if (!location!!.hasLocationEnabled())
            {
                spnSehirler.setSelection(1)
                Toast.makeText(this,"GPSnizi açmalısınız",Toast.LENGTH_LONG).show()
                SimpleLocation.openSettings(this)
            }else
            {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),60)
                }else
                {
                    location = SimpleLocation(this)
                    latitude = String.format("%.6f", location?.latitude)
                    longitude = String.format("%.6f", location?.longitude)
                    Log.e("LAT", "" + latitude)
                    Log.e("LONG", "" + longitude)

                    oankiSehriGetir(latitude, longitude)
                }
            }
        }
        else
        {
            var secilenSehir = parent?.getItemAtPosition(position).toString()
            tvSehir = view as TextView
            verileriGetir(secilenSehir)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if(requestCode == 60){

            if(grantResults.size > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                location = SimpleLocation(this)
                latitude = String.format("%.6f", location?.latitude)
                longitude = String.format("%.6f", location?.longitude)
                Log.e("LAT", "" + latitude)
                Log.e("LONG", "" + longitude)

                oankiSehriGetir(latitude, longitude)

            }else {
                spnSehirler.setSelection(1)
                Toast.makeText(this, "İzin vereydin de konumunu bulaydık :P", Toast.LENGTH_LONG).show()

            }


        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun verileriGetir(sehir: String) {
        var url =
            "https://api.openweathermap.org/data/2.5/weather?q=" + sehir + ",tr&appid=f5a0343d74202c2d178fda5191bb250e&lang=tr&units=metric"
        var havaDurumuObje = JsonObjectRequest(Request.Method.GET, url, null, object : Response.Listener<JSONObject>,
            Response.ErrorListener {
            override fun onResponse(response: JSONObject?) {
                var main = response?.getJSONObject("main")
                var sicaklik = main?.getInt("temp")
                tvSicaklik.text = sicaklik.toString()


                var sehirAdi = response?.getString("name")
                tvSehir!!.text = sehirAdi.toString()


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
                    tvSehir!!.setTextColor(resources.getColor(R.color.colorAccent))
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
