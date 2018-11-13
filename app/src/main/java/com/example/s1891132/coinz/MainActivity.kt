package com.example.s1891132.coinz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
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
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.longToast
import org.json.JSONObject
import java.util.*

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
        setSupportActionBar(menu_toolbar)

        Mapbox.getInstance(applicationContext,getString(R.string.access_token))
        mapView=findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        //Is it appropriate to be here??? i mean you quit the app and you have to download it again i guess
        //to login in
        // Choose authentication providers
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build())
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN)

        //can set theme and logo for log in ui in https://github.com/firebase/snippets-android/blob/027f11bd3c3ff625a20ee4a832a2768e0b0204e6/auth/app/src/main/java/com/google/firebase/quickstart/auth/kotlin/FirebaseUIActivity.kt#L40-L57

        val settings=getSharedPreferences(coinzFile,Context.MODE_PRIVATE)
        var downloadCoin=settings.getString(currentDate(),"Unable to load coinz. Check your network connection.")
        if(downloadCoin=="Unable to load coinz. Check your network connection.")
        {
            downloadMap.execute(currentUrl())//unable to load coinz:cannot
        }

        //downloadMap.execute(currentUrl())

        //if have user instance now, stop this intent, add a pending situation
        //startActivity(Intent(this@MainActivity,LogInActivity::class.java))

        mapView.getMapAsync{mapboxMap ->
            map=mapboxMap
            enableLocation()
            //if have files for today, do not need to store
            if(downloadCoin=="Unable to load coinz. Check your network connection."){
                storeCoinz(DownloadCompleteRunner.result)
            }
            downloadCoin=settings.getString(currentDate(),"")
            parseGeoJson(downloadCoin)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                //get current User here

                // ...
            }
            /*else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }*/
        }
    }

    override fun onCreateOptionsMenu(menu: Menu):Boolean{
        menuInflater.inflate(R.menu.menu_toolbar,menu)
        return true
    }

    //still under construction
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    longToast("sign out successfully")
                }
        // [END auth_fui_signout]
        return true
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


    fun storeCoinz(data:String?){
        val editor=getSharedPreferences(coinzFile, Context.MODE_PRIVATE).edit()
        editor.putString(currentDate(),data)
        editor.apply()
    }


    fun parseGeoJson(data:String?){//may not be null
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
                    //Log.i("marker",long.toString())
                    val curtype=fc.properties()!!.getAsJsonPrimitive("currency").toString()
                    val id=fc.properties()!!.getAsJsonPrimitive("id").toString()//or int
                    val curvalue=fc.properties()!!.getAsJsonPrimitive("value").toString()//or int
                    val markersymbol=fc.properties()!!.getAsJsonPrimitive("marker-symbol").toString()//or int?
                    val markercolor=fc.properties()!!.getAsJsonPrimitive("marker-color").toString()
                    /*val icon=IconFactory.getInstance(this)
                    val iconDrawable=ContextCompat.getDrawable(this,R.drawable.puper)*/

                    map.addMarker(MarkerOptions().title(curtype).snippet(curvalue).position(latlng))
                }
            }







            /*val source=GeoJsonSource("coinzsource",featureCollection)
            map.addSource(source)
            val markerLayer=SymbolLayer("mylayerid","mysourceid")
            map.addLayer(markerLayer)*/


            /*Bitmap icon = BitmapFactory.decodeResource(
      BasicSymbolLayerActivity.this.getResources(), R.drawable.blue_marker_view);

    // Add the marker image to map
    mapboxMap.addImage("my-marker-image", icon);

    SymbolLayer markers = new SymbolLayer("marker-layer", "marker-source")
      .withProperties(PropertyFactory.iconImage("my-marker-image"));
    mapboxMap.addLayer(markers);

    // Add the selected marker source and layer
    FeatureCollection emptySource = FeatureCollection.fromFeatures(new Feature[]{});
    Source selectedMarkerSource = new GeoJsonSource("selected-marker", emptySource);
    mapboxMap.addSource(selectedMarkerSource);

    SymbolLayer selectedMarker = new SymbolLayer("selected-marker-layer", "selected-marker")
      .withProperties(PropertyFactory.iconImage("my-marker-image"));
    mapboxMap.addLayer(selectedMarker);

mapboxMap.addOnMapClickListener(this);*/

        }


    }






    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            locationEngine?.requestLocationUpdates()
            locationLayerPlugin?.onStart()
            Log.i("maptest","here3")


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

    companion object {
        private const val RC_SIGN_IN = 123
    }
}




