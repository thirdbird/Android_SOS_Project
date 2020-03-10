package com.example.sosapplication

import com.google.firebase.firestore.FirebaseFirestore
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class NotificationHandler {

    private var database = FirebaseFirestore.getInstance()

    fun createNotificationRequest(){

        //http://developine.com/how-to-send-firebase-push-notifications-from-app-server-tutorial/
        //https://stackoverflow.com/questions/46177133/http-request-in-kotlin
        //https://stackoverflow.com/questions/38069719/setting-custom-header-using-httpurlconnection
        //https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages
        //https://firebase.google.com/docs/cloud-messaging/http-server-ref

        //måste kolla länk
        //https://wajahatkarim.com/2019/11/add-push-notifications-to-your-android-chat-app-using-kotlin/
/*
        Authorization: key=AAAA4wkL01w:APA91bHu... //Server Key --
        Content-type: application/json*/

        /*{
            "to": "eGS6QMzVlbA:APA91bFtY...", //Token
            "notification": {
            "title": "title",
            "body": "body text",
            "icon": "ic_notification"
            }
        }
        {
          "title": string,
          "body": string,
          "icon": string,
          "tag": string,
          "click_action": string,
          "body_loc_key": string,
          "body_loc_args": [
            string
          ],
          "title_loc_key": string,
          "title_loc_args": [
            string
          ],
          "channel_id": string,
          "ticker": string,
          "sticky": boolean,
          "event_time": string,
          "local_only": boolean,
          "notification_priority": enum (NotificationPriority),
          "default_sound": boolean,
          "default_vibrate_timings": boolean,
          "default_light_settings": boolean,
          "vibrate_timings": [
            string
          ],
          "visibility": enum (Visibility),
          "notification_count": integer,
          "light_settings": {
            object (LightSettings)
          },
          "image": string
        }

        */
        val serverKey = "AAAAHVO-dfs:APA91bHgNCQ9q0upSufJUltCH_Zc1PCPTnuBGLooPMCu7rfoiM2ivW_2KT98-R7ukQsrQOWnnaoXb-otfI19RBd5zoCSyjVbYXLYpZQ1VNsgq7v49pu5ccgJcfyJOMqqoWsm_8nEuB3T"
        val mURL = URL("https://fcm.googleapis.com/fcm/send")
        val reqParam = {
            database.toString()
        }
        with(mURL.openConnection() as HttpURLConnection) {
            // optional default is GET
            setRequestProperty("Content-type", "application/json")
            setRequestProperty("Authorization", serverKey)

            requestMethod = "POST"

            val wr = OutputStreamWriter(getOutputStream())
            //wr.write(reqParam)
            wr.flush()

            println("URL : $url")
            println("Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                println("Response : $response")
            }
        }
    }
}