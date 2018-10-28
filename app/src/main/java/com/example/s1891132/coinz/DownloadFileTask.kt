package com.example.s1891132.coinz

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


interface DownloadCompleteListener{
    fun downloadComplete(result:String)
}

object DownloadCompleteRunner:DownloadCompleteListener{
    var result:String?=null//?
    override fun downloadComplete(result:String){
        this.result=result
        //Log.i("download",result)//log
        //here result is what you want? to store it in the local preference file
    }
}

//AsyncTask<the required URL>

//so the logic is downloadComplete-> DownloadFiletask.EXCUTE???->LOADFILEFROMNETWORK->dOWNLOAD URL->ON POSTEXECUTE I GUESS


//so the next part is shared preference

class DownloadFileTask(private val caller:DownloadCompleteListener):AsyncTask<String,Void,String>(){//the first String is the input parameter(should be the URL)
    override fun doInBackground(vararg urls: String): String=try {
            //Log.i("download","DownloadFileTask")//log
       loadFileFromNetwork(urls[0])
    }catch(e: IOException)
    {
        "Unable to load coinz. Check your network connection."
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        caller.downloadComplete(result)//caller is the Downloadcomplete runner
    }
}

//so the logic is downloadURL return the text on server and feedback to loadFileNetwork, and make it a string to go back to DownloadFIleTsk

private fun loadFileFromNetwork(urlString:String): String{
    val stream: InputStream =downloadUrl(urlString)
    //Log.i("download","DownloadFilFromNetwork")//log
    //build result as a string  inputstream->string
    val result = stream.bufferedReader().use{it.readText()}
    return result//this should be a string
}

@Throws(IOException::class)
private fun downloadUrl(urlString: String):InputStream{
    val url= URL(urlString)//here url can be other import
    val conn=url.openConnection()as HttpURLConnection//Also availbale:Https
    conn.readTimeout=10000
    conn.connectTimeout=15000
    conn.requestMethod="GET"
    conn.doInput=true
    conn.connect()
    //Log.i("download","downloadUrl")//log
    return conn.inputStream
}