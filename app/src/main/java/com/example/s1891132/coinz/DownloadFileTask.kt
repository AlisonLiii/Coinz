package com.example.s1891132.coinz


import android.os.AsyncTask
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


interface DownloadCompleteListener{
    fun downloadComplete(result:String)
}

object DownloadCompleteRunner:DownloadCompleteListener{
    var result:String?=null
    override fun downloadComplete(result:String){
        this.result=result
    }
}

class DownloadFileTask(private val caller:DownloadCompleteListener):AsyncTask<String,Void,String>(){
    override fun doInBackground(vararg urls: String): String=try {
       loadFileFromNetwork(urls[0])
    }catch(e: IOException)
    {
        "Unable to load coinz. Check your network connection."
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        caller.downloadComplete(result)
    }
}

private fun loadFileFromNetwork(urlString:String): String{
    val stream: InputStream =downloadUrl(urlString)
    return stream.bufferedReader().use{it.readText()}
}

@Throws(IOException::class)
private fun downloadUrl(urlString: String):InputStream{
    val url= URL(urlString)
    val conn=url.openConnection()as HttpURLConnection
    conn.readTimeout=10000
    conn.connectTimeout=15000
    conn.requestMethod="GET"
    conn.doInput=true
    conn.connect()
    return conn.inputStream
}