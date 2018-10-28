package com.example.s1891132.coinz

import android.content.Context
import android.content.Intent
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode

class MainActivity : AppCompatActivity() , PermissionsListener, LocationEngineListener{


    private val tag= "MainActivity"
    //private var downloadDate=""//Format:YYYY/MM/DD
    //private val preferencesFile="MarkersInfo"//for storing preferences

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var originLocation: Location
    private var locationEngine: LocationEngine?=null
    private var locationLayerPlugin: LocationLayerPlugin?=null

    private var downloadMap: DownloadFileTask=DownloadFileTask(DownloadCompleteRunner)//is it possible to be null???
    private var coinzFile="CoinzGeoInfoToday"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Mapbox.getInstance(applicationContext,getString(R.string.access_token))
        mapView=findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        //Is it appropriate to be here??? i mean you quit the app and you have to download it again i guess
        //to login in
        val intentToLogin= Intent()
        intentToLogin.setClass(this,LogInActivity::class.java)
        startActivity(intentToLogin)
        downloadMap.execute(CurrentUrl())//here needs a string buildeR to change the date
        mapView.getMapAsync{mapboxMap ->
            map=mapboxMap
            enableLocation()
        }


        //Log.i("download",DownloadCompleteRunner.result)//debug
        //i think the point is they didn't finish the download and it directly goes down to here
        //so to solve it:1.write shared preference in object 2:wait for the background(but how???)
        //getMap(DownloadCompleteRunner.result)
    }



    fun enableLocation(){
        if(PermissionsManager.areLocationPermissionsGranted(this))
        {
            initializeLocationEngine()
            initializeLocationLayer()
        }else {
            permissionsManager= PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)

        }
    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationEngine(){
        locationEngine=LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine?.priority=LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()

        val lastLocation=locationEngine?.lastLocation
        if(lastLocation!=null)
        {
            originLocation=lastLocation
            setCameraPosition(lastLocation)
        }else{
            locationEngine?.addLocationEngineListener(this)
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationLayer(){
        locationLayerPlugin= LocationLayerPlugin(mapView,map,locationEngine)
        locationLayerPlugin?.setLocationLayerEnabled(true)
        locationLayerPlugin?.cameraMode=CameraMode.TRACKING
        locationLayerPlugin?.renderMode=RenderMode.NORMAL

    }

    private fun setCameraPosition(location: Location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude),15.0))
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        //Present a dialog explaining why they need to grant access
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted) {
            enableLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }

    override fun onLocationChanged(location: Location?) {
       location?.let {
           originLocation=location
           setCameraPosition(location)
       }
    }

@SuppressWarnings("MissingPermission")
    override fun onConnected() {
     locationEngine?.requestLocationUpdates()
    }

    /*fun getMap(storeInfo:String?){
        val settings= getSharedPreferences(coinzFile,Context.MODE_PRIVATE)
        val editor= settings.edit()
        editor.putString("CoinzStoreInfo",storeInfo)
        editor.apply()
        Log.i("getMap",storeInfo)



    }*/





    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            locationEngine?.requestLocationUpdates()
            locationLayerPlugin?.onStart()

        }
        mapView.onStart()
        //val settings=getSharedPreferences(preferencesFile, Context.MODE_PRIVATE)//restore preferences
        //downloadDate=settings.getString("lastDownloadDate","")//use ""as default value(this might be the first time the app is run)
        //Log.d(tag,"[onStart] Recalled lastDownloadDate is '$downloadDate'")

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        //Log.d(tag,"[onStop] Storing lastDownloadDate of $downloadDate")
        //All objets are from android.context.Context
        //val settings=getSharedPreferences(preferencesFile,Context.MODE_PRIVATE)
        //We need an Editor object to make preference changes.
        //val editor=settings.edit()
        //editor.putString("lastDownloadDate",downloadDate)
        //apply the edits
        //editor.apply()
        locationEngine?.removeLocationUpdates()
        locationLayerPlugin?.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        locationEngine?.deactivate()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        if(outState!=null){
            mapView.onSaveInstanceState(outState)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
