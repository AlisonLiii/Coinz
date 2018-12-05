package com.example.s1891132.coinz

import android.support.v4.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import com.example.s1891132.coinz.userAuthentication.LogInActivity
import com.example.s1891132.coinz.dataClassAndItem.Coin
import com.firebase.ui.auth.AuthUI
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar
import java.util.*

class MainActivity : AppCompatActivity() , PermissionsListener, LocationEngineListener, OnMapReadyCallback,MapboxMap.OnMarkerClickListener {
    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var originLocation: Location
    private lateinit var downloadCoin:String
    private lateinit var settings:SharedPreferences

    private var walkingDistance:Double=0.0
    private var coinList:MutableList<Coin> = ArrayList()
    private var locationEngine: LocationEngine?=null
    private var locationLayerPlugin: LocationLayerPlugin?=null
    private var downloadMap: DownloadFileTask=DownloadFileTask(DownloadCompleteRunner)
    private var coinzFile="CoinzGeoInfoToday"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.title = "Coinz"

        // Initialize the action bar drawer toggle instance
        val drawerToggle:ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        )
        {
        }

        // Configure the drawer layout to add listener and show icon on toolbar
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Set navigation view navigation item selected listener
        navigation_view.setNavigationItemSelectedListener{
            when (it.itemId){
                R.id.my_account ->
                { replaceFragment(MyAccountFragment()) }
                R.id.people -> { replaceFragment(PeopleFragment()) }
                R.id.my_property ->
                { replaceFragment(MyPropertyFragment()) }
                R.id.signout ->{
                    AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener {
                                longToast("sign out successfully, now return to the log in page")
                                startActivity<LogInActivity>()}
                }
            }
            // Close the drawer
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }


        Mapbox.getInstance(applicationContext,getString(R.string.access_token))
        mapView=findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        //get GeoJson information of the day
        settings=getSharedPreferences(coinzFile,Context.MODE_PRIVATE)
        downloadCoin=settings.getString(currentDate(),"No GeoJson for today")
        //if shared preference file doesn't have today's information, the variable downloadCoin="No GeoJson for today"
        if(downloadCoin=="No GeoJson for today")
        {
            downloadMap.execute(currentUrl())//download today's GeoJson information from the server
        }
        FirestoreUtil.newDayUpdateOrNot()//zero out wallet balance, coinz and walking distance if it is a new day
        mapView.getMapAsync(this)

    }

    override fun onMapReady(mapboxMap: MapboxMap?) {
        map=mapboxMap!!
        //val view=this.findViewById<View>(android.R.id.content)
        contentView?.longSnackbar("Please wait for some seconds to get your location","OK") {}
        enableLocation()
        //if have files for today, do not need to store
        if(downloadCoin=="No GeoJson for today"){
            storeCoinz(DownloadCompleteRunner.result)//store the GeoJson downloaded in shared preference file
        }
        downloadCoin=settings.getString(currentDate(),"")//get GeoJson information of the day from shared preference file
        parseGeoJson(downloadCoin)//parse GeoJson information to add markers
    }


    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.fragment_frame,fragment)
            addToBackStack(null)//this enables user to press the Back button on the phone and return to the main screen
            commit()
        }
    }


    //store information in shared preference file
    private fun storeCoinz(data:String?){
        val editor=getSharedPreferences(coinzFile, Context.MODE_PRIVATE).edit()
        editor.putString(currentDate(),data)
        editor.apply()
    }


    private fun parseGeoJson(data:String?){

        //get icons representing four types of currency
        val iconFactory = IconFactory.getInstance(this)
        val iconQuid=iconFactory.fromResource(R.drawable.ic_quid)
        val iconDolr = iconFactory.fromResource(R.drawable.ic_dolr)
        val iconPeny=iconFactory.fromResource(R.drawable.ic_peny)
        val iconShil = iconFactory.fromResource(R.drawable.ic_shil)

        if(data==null)
        {
            Log.e("","no GeoJson information for the day")
        }
        else{

            val featureCollection=FeatureCollection.fromJson(data)
            val features:List<Feature>?=featureCollection.features()
            if(features==null)
            {
                Log.e("","no GeoJson information for the day")
            }
            else{
                for(fc in features)
                {
                    val point=fc.geometry()as Point
                    val latlng=LatLng(point.latitude(),point.longitude())
                    val curtype=fc.properties()!!.getAsJsonPrimitive("currency").toString().replace("\"", "")
                    //use .replace to remove the quotation mark of the String
                    val id=fc.properties()!!.getAsJsonPrimitive("id").toString().replace("\"", "")//or int
                    val curvalue=fc.properties()!!.getAsJsonPrimitive("value").toString().replace("\"", "")
                    val coin= Coin(id, curtype, curvalue.toDouble(), latlng)

                    FirestoreUtil.markerRef.document(id).get().addOnSuccessListener { documentSnapshot ->
                        //add the markers on the map which have not been successfully collected during the day
                        if(!documentSnapshot.exists())
                        {
                            coinList.add(coin)
                            //the coins on the map which have not been successfully collected during the day
                            if(curtype.equals("DOLR",true))
                                map.addMarker(MarkerOptions().title(curtype).snippet(curvalue).position(latlng).icon(iconDolr))
                            else if(curtype.equals("SHIL",true))
                                map.addMarker(MarkerOptions().title(curtype).snippet(curvalue).position(latlng).icon(iconShil))
                            else if(curtype.equals("PENY",true))
                                map.addMarker(MarkerOptions().title(curtype).snippet(curvalue).position(latlng).icon(iconPeny))
                            else
                                map.addMarker(MarkerOptions().title(curtype).snippet(curvalue).position(latlng).icon(iconQuid))
                        }
                    }

                }
            }

        }

    }



    //Location service
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
           //calculate user's walking distance
           if(::originLocation.isInitialized)
           {
               walkingDistance+=originLocation.distanceTo(location).toDouble()
           }
           originLocation=location
           FirestoreUtil.updateWalkingDistance(walkingDistance)
           setCameraPosition(location)
       }

        map.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if(marker.isInfoWindowShown)
        {
            marker.hideInfoWindow()
        }
        else marker.showInfoWindow(map,mapView)

        //only after the system get user's current location, the user can click on the markers and collect coinz
        if(::originLocation.isInitialized)
        {
            if(marker.position.distanceTo(LatLng(originLocation.latitude,originLocation.longitude))<25)
            {
                coinList.forEach {//iterate the coins which have not been collected for the day
                    if(it.latlng==marker.position)
                    {
                        if(it.type.equals("PENY",true))
                            FirestoreUtil.updateWalletBalance(FirestoreUtil.currentUserDocRef,it.value,0.0,0.0,0.0,1)
                        //operation:1 means adding coins
                        else if(it.type.equals("DOLR",true))
                            FirestoreUtil.updateWalletBalance(FirestoreUtil.currentUserDocRef,0.0,it.value,0.0,0.0,1)
                        else if(it.type.equals("SHIL",true))
                            FirestoreUtil.updateWalletBalance(FirestoreUtil.currentUserDocRef,0.0,0.0,it.value,0.0,1)
                        else if(it.type.equals("QUID",true))
                            FirestoreUtil.updateWalletBalance(FirestoreUtil.currentUserDocRef,0.0,0.0,0.0,it.value,1)
                        else
                            Log.d("coinz","Invalid type of coinz")
                        FirestoreUtil.addCoinInList(FirestoreUtil.coinSelfCollectListRef,it)
                        //record the markers that need to be removed the next time user open the app
                        FirestoreUtil.addCoinInList(FirestoreUtil.markerRef,it)
                    }
                }
                contentView?.longSnackbar("Successfully collect a coin!")
                marker.remove()
            }
            else contentView?.longSnackbar("Please get closer!")
        }
        else longToast("Wait for the system to get your location!")
        return true
    }


    @SuppressWarnings("MissingPermission")
    override fun onConnected() {
     locationEngine?.requestLocationUpdates()
    }



    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            locationEngine?.requestLocationUpdates()
            locationLayerPlugin?.onStart()
        }
        mapView.onStart()


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






