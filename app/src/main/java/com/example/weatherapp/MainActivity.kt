package com.example.weatherapp


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 4ac5ede472a9cae7e6d4ccc107990a74
class MainActivity : AppCompatActivity() {
//    companion object {
//        private const val TAG = "MainActivity"
//    }
    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchWeatherData("ahmedabad")
        searchCity()
    }



    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterFace::class.java)

        val response = retrofit.getWeatherData(cityName,"4ac5ede472a9cae7e6d4ccc107990a74","metric")

        response.enqueue(object : Callback<WeatherApp>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val sealLeavel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windspeed.text = "$windSpeed M/s"
                    binding.sunrise.text = time(sunRise)
                    time(sunSet).also { binding.sunset.text = it }
                    binding.sea.text = "$sealLeavel Hpa"
                    binding.condition.text = condition
                    binding.day.text = dayName()
                    binding.date.text = date()
                    binding.cityname.text = cityName


                    //Log.d("TAG", "onResponse: $temperature")

                    changeWeather(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun changeWeather(conditions:String) {
        when (conditions){
            "Clear Sky" , "Sunny", "Clear", "Smoke" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView2.setAnimation(R.raw.sun)
            }

            "Partly Cloudy" , "Clouds" , "Mist" , "Foggy" , "Haze"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView2.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Rain", "Drizzle" , "Moderate Rain", "Showers" , "Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView2.setAnimation(R.raw.rain)
            }

            "Light Snow" , "Snow", "Moderate Snow", "Heavy Snow" , "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView2.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView2.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView2.playAnimation()
    }



    private fun date(): String {
        val sdf= SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf= SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName():String{
        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}