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
import android.view.Menu
import android.view.MenuItem
import com.example.s1891132.coinz.Authentication.LogInActivity
import com.example.s1891132.coinz.ClassAndItem.Coin
import com.example.s1891132.coinz.Fragment.SearchFriendFragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
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
import java.util.*

class MainActivity : AppCompatActivity() , PermissionsListener, LocationEngineListener, OnMapReadyCallback,MapboxMap.OnMarkerClickListener {
    //TODO:DELETE TOOLBAR
    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var originLocation: Location
    private lateinit var db:FirebaseFirestore
    private lateinit var downloadCoin:String
    private lateinit var settings:SharedPreferences
    private lateinit var user:FirebaseUser
    private var walkingDistance:Double=0.0

    private var coinList:MutableList<Coin> = ArrayList()
    private var locationEngine: LocationEngine?=null
    private var locationLayerPlugin: LocationLayerPlugin?=null
    private var downloadMap: DownloadFileTask=DownloadFileTask(DownloadCompleteRunner)//is it possible to be null???
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
                {
                    replaceFragment(MyAccountFragment())//TODO:ID LAYOUT
                }
                R.id.people -> {
                    replaceFragment(PeopleFragment())
                }
                R.id.my_property ->
                {
                    replaceFragment(MyPropertyFragment())
                    //replaceFragment(SearchFriendFragment())
                }
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

        db=FirebaseFirestore.getInstance()
        Mapbox.getInstance(applicationContext,getString(R.string.access_token))
        mapView=findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        settings=getSharedPreferences(coinzFile,Context.MODE_PRIVATE)
        downloadCoin=settings.getString(currentDate(),"Unable to load coinz. Check your network connection.")
        if(downloadCoin=="Unable to load coinz. Check your network connection.")
        {
            downloadMap.execute(currentUrl())//unable to load coinz:cannot
        }
        FirestoreUtil.newDayUpdateOrNot()
        mapView.getMapAsync(this)

    }

    override fun onMapReady(mapboxMap: MapboxMap?) {
        map=mapboxMap!!
        //val view=this.findViewById<View>(android.R.id.content)
        //view.longSnackbar("Please wait for some seconds to get your location","OK")
        {}
        val dialog=indeterminateProgressDialog(message = "Please wait a bit", title="Getting your location")
        {

        }

        //longToast("Please wait for a second to get your location")
        enableLocation()
        //if have files for today, do not need to store
        if(downloadCoin=="Unable to load coinz. Check your network connection."){
            storeCoinz(DownloadCompleteRunner.result)
        }
        downloadCoin=settings.getString(currentDate(),"")
        parseGeoJson(downloadCoin)
        user = FirebaseAuth.getInstance().currentUser!!
    }


    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.fragment_frame,fragment)
            addToBackStack(null)// press the Back on the phone and return to the main screen
            commit()
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
           if(::originLocation.isInitialized)
           {
               walkingDistance+=originLocation.distanceTo(location).toDouble()
           }
           originLocation=location
           FirestoreUtil.updateWalkingDistance(walkingDistance)
           Log.i("walking",walkingDistance.toString())
           setCameraPosition(location)
           /*val intent=Intent("LocationChanged")
           intent.setType("text/plain")
           sendBroadcast(intent);*/
       }

        map.setOnMarkerClickListener(this)
    }

@SuppressWarnings("MissingPermission")
    override fun onConnected() {
     locationEngine?.requestLocationUpdates()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if(marker.isInfoWindowShown)
        {
            marker.hideInfoWindow()
        }
        else marker.showInfoWindow(map,mapView)
        if(marker.position.distanceTo(LatLng(originLocation.latitude,originLocation.longitude))<25)
        {
            coinList.forEach {
                if(it.latlng==marker.position)
                {
                    if(it.type.equals("PENY",true))
                        FirestoreUtil.updateWalletBalance(FirestoreUtil.currentUserDocRef,it.value,0.0,0.0,0.0,1)
                    else if(it.type.equals("DOLR",true))
                        FirestoreUtil.updateWalletBalance(FirestoreUtil.currentUserDocRef,0.0,it.value,0.0,0.0,1)
                    else if(it.type.equals("SHIL",true))
                        FirestoreUtil.updateWalletBalance(FirestoreUtil.currentUserDocRef,0.0,0.0,it.value,0.0,1)
                    else if(it.type.equals("QUID",true))
                        FirestoreUtil.updateWalletBalance(FirestoreUtil.currentUserDocRef,0.0,0.0,0.0,it.value,1)
                    else
                        Log.d("coinz","Invalid type of coinz")
                    FirestoreUtil.addCoinInList(FirestoreUtil.coinListRef,it)
                    FirestoreUtil.addCoinInList(FirestoreUtil.markerRef,it)
                }
            }
            longToast("success")
            //TODO:COINLIST STORE in firebase
            marker.remove()
            /*val items= HashMap<String,Any>()
            items.put("SHIL",777)
            //unresolved bug here
            Log.i("this",marker.snippet)

            db.collection("user wallet").document(user.uid).set(items)
                    .addOnSuccessListener {
                        longToast("success added")
                    }
                    .addOnFailureListener{
                        longToast("Failure added")
                    }*/
        }
        else longToast("fail")

        return true
    }


    fun storeCoinz(data:String?){
        val editor=getSharedPreferences(coinzFile, Context.MODE_PRIVATE).edit()
        editor.putString(currentDate(),data)
        editor.apply()
    }


    fun parseGeoJson(data:String?){//may not be null

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
                    //Log.i("marker",long.toString())
                    val curtype=fc.properties()!!.getAsJsonPrimitive("currency").toString().replace("\"", "")
                    val id=fc.properties()!!.getAsJsonPrimitive("id").toString().replace("\"", "")//or int
                    val curvalue=fc.properties()!!.getAsJsonPrimitive("value").toString().replace("\"", "");//or int
                    val markersymbol=fc.properties()!!.getAsJsonPrimitive("marker-symbol").toString()//or int?
                    val markercolor=fc.properties()!!.getAsJsonPrimitive("marker-color").toString()
                    /*val icon=IconFactory.getInstance(this)
                    val iconDrawable=ContextCompat.getDrawable(this,R.drawable.puper)*/
                    val coin= Coin(id, curtype, curvalue.toDouble(), latlng)
                    FirestoreUtil.markerRef.document(id).get().addOnSuccessListener { documentSnapshot ->
                            if(!documentSnapshot.exists())
                            {
                                coinList.add(coin)
                                //map.addMarker(MarkerOptions().title(curtype).snippet(curvalue).position(latlng))
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

            /*val source=GeoJsonSource("coinzsource",featureCollection)
            map.addSource(source)
            val markerLayer=SymbolLayer("mylayerid","mysourceid")
            map.addLayer(markerLayer)*/


            /*Bitmap icon = BitmapFactory.decodeResource(
      BasicSymbolLayerActivity.this.getResources(), R.drawable.blue_marker_view);

    // Add the marker image to map.toDouble()
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
        }
        /*if (locationEngine != null) {

            try {
                locationEngine?.requestLocationUpdates()
            } catch (ignored: SecurityException) {
            }

            locationEngine?.addLocationEngineListener(this)
        }*/
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






