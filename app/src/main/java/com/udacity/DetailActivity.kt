package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)


        //receive intent
        val notifIntent = intent
        if (notifIntent != null && intent.hasExtra(getString(R.string.key))) {
            val file = intent.getStringExtra(getString(R.string.key))
            if (file != null) { //show file name if not null
                file_name_text.text = file

                if (intent.getBooleanExtra(getString(R.string.success), false)) {
                    status_text.text = getText(R.string.success)
                    status_text.setTextColor(Color.GREEN)
                } else {
                    status_text.text = getText(R.string.failed)
                    status_text.setTextColor(Color.RED)
                }

            }
        }

    }


    fun moveBack(view: View) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra(getString(R.string.back_key), 19)
        Log.d("MainActivity: ", "onBack intent value is -> ${intent.extras} ")
        startActivity(intent)
    }
}
