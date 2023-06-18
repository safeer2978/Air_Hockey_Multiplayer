package com.hajmola

import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.badlogic.gdx.Game
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this.applicationContext)

        setContentView(R.layout.activity_main)
        val ball = findViewById<BallView>(R.id.ball)
        val gameView = findViewById<GameView>(R.id.game)
        val ref = Firebase.database.getReference("position")

        val receiver: (Float, Float)-> Unit = { logicalX, logicalY ->
            ball.apply {
                x = logicalX.toPhysicalX(context)
                y = logicalY.toPhysicalY(context)
            }
        }

        ball.transmitter = { physicalX, physicalY ->
//            ref.child("x").setValue(physicalX.toLogicalX(this@MainActivity))
//            ref.child("y").setValue(physicalY.toLogicalY(this@MainActivity))
            gameView.apply {
                this.somehtin.movePlayer(physicalX.toLogicalX(this@MainActivity), physicalY.toLogicalY(this@MainActivity))
            }
        }

        val breceiver = object :  ReceiverBroadcast(receiver) {
            override fun onReceive(context: Context?, intent: Intent?) {
                val x = intent?.getFloatExtra("x",0f) ?: 0f
                val y = intent?.getFloatExtra("y",0f) ?: 0f
                receiver.invoke(x,y)
            }
        }


        /*ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val x = snapshot.child("x").getValue<Float>()
                val y = snapshot.child("y").getValue<Float>()
                if (x != null && y != null) {
                        receiver.invoke(x,y)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
*/
        registerReceiver(breceiver , IntentFilter().apply {
            addAction("com.safeer.send")
        });
    }
}

abstract class ReceiverBroadcast(val receiver :(Float, Float)-> Unit) : BroadcastReceiver()

//need to convert pixel co ordinates to logical floats
fun Float.toLogicalX(context: Context) =
    this / context.resources.displayMetrics.widthPixels

fun Float.toLogicalY(context: Context) =
    (2*this) / context.resources.displayMetrics.heightPixels

fun Float.toPhysicalY(context: Context) = this * context.resources.displayMetrics.heightPixels

fun Float.toPhysicalX(context: Context) = this * context.resources.displayMetrics.widthPixels