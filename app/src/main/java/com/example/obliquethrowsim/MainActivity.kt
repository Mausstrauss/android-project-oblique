package com.example.obliquethrowsim

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.cos
import kotlin.math.sin
import android.graphics.Canvas
import android.view.View


class MainActivity : AppCompatActivity() {
    // Extension function to convert degrees to radians
    private fun Double.toRadians(): Double {
        return this * Math.PI / 180.0
    }
    // Declare variables for the UI elements
    private lateinit var initialSpeedInput: EditText
    private lateinit var angleInput: EditText
    private lateinit var calculateButton: Button
    private lateinit var startAnimationButton: Button
    private lateinit var resultsTextView: TextView
    private lateinit var animationView: SurfaceView
    private lateinit var graphView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Set the layout defined in activity_main.xml

        // Initialize the UI elements
        initialSpeedInput = findViewById(R.id.initialSpeedInput)
        angleInput = findViewById(R.id.angleInput)
        calculateButton = findViewById(R.id.calculateButton)
        startAnimationButton = findViewById(R.id.startAnimationButton)
        resultsTextView = findViewById(R.id.resultsTextView)
        animationView = findViewById(R.id.animationView)
        graphView = findViewById(R.id.graphView)

        // Set onClickListeners for the buttons
        calculateButton.setOnClickListener { calculateProjectile() }
        startAnimationButton.setOnClickListener { startAnimation() }
    }

    private fun startAnimation() {
        TODO("Not yet implemented")
    }

    // Function to calculate projectile motion and display results
    private fun calculateProjectile() {
        // Get user inputs
        val speed = initialSpeedInput.text.toString().toDoubleOrNull() ?: return
        val angle = angleInput.text.toString().toDoubleOrNull() ?: return

        // Convert angle to radians
        val angleRadians = angle.toRadians()
        val g = 9.81  // Acceleration due to gravity

        // Calculate time of flight, range, and maximum height
        val totalTime = (2 * speed * sin(angleRadians)) / g
        val range = (speed * cos(angleRadians)) * totalTime
        val maxHeight = (speed * speed * sin(angleRadians) * sin(angleRadians)) / (2 * g)

        // Format results and display them
        val results = """
            Time of Flight: ${"%.2f".format(totalTime)} s
            Range: ${"%.2f".format(range)} m
            Maximum Height: ${"%.2f".format(maxHeight)} m
        """.trimIndent()
        resultsTextView.text = results

        // Draw the graph
        drawGraph(speed, angleRadians, totalTime)
    }

    // Function to draw the graph of height vs. time and height vs. distance
    private fun drawGraph(speed: Double, angleRadians: Double, totalTime: Double) {
        val g = 9.81
        val surfaceHolder = graphView.holder
        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                val canvas = holder.lockCanvas()
                val paint = Paint().apply {
                    color = Color.BLACK
                    strokeWidth = 5f
                }

                canvas.drawColor(Color.WHITE)

                val width = canvas.width.toFloat()
                val height = canvas.height.toFloat()
                val scaleX = width / totalTime.toFloat()
                val scaleY = height / (speed * speed / (2 * g)).toFloat()

                var previousX = 0f
                var previousY = height

                for (t in 0..(totalTime * 100).toInt()) {
                    val time = t / 100.0
                    val x = speed * cos(angleRadians) * time
                    val y = height - (speed * sin(angleRadians) * time - 0.5 * g * time * time) * scaleY

                    if (t > 0) {
                        canvas.drawLine(previousX, previousY, (x * scaleX).toFloat(), y.toFloat(), paint)
                    }

                    previousX = (x * scaleX).toFloat()
                    previousY = y.toFloat()
                }

                holder.unlockCanvasAndPost(canvas)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })
    }

    // Function to start the animation of the projectile motion
    fun startAnimation(view: View) {
        val speed = initialSpeedInput.text.toString().toDoubleOrNull() ?: return
        val angle = angleInput.text.toString().toDoubleOrNull() ?: return

        val angleRadians = Math.toRadians(angle)
        val g = 9.81
        val totalTime = (2 * speed * Math.sin(angleRadians)) / g

        val surfaceHolder = animationView.holder
        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                val canvas = holder.lockCanvas()
                try {
                    // Perform initial drawing operations on the canvas
                    canvas.drawColor(Color.WHITE) // Example of drawing a white background
                } finally {
                    holder.unlockCanvasAndPost(canvas)
                }

                // Start a thread to handle the animation
                Thread {
                    // Animation logic goes here
                    // For example, animate the projectile's path
                    val paint = Paint().apply {
                        color = Color.RED
                        strokeWidth = 5f
                    }

                    var time = 0.0
                    while (time <= totalTime) {
                        val x = speed * cos(angleRadians) * time.toFloat()
                        val y = animationView.height - (speed * sin(angleRadians) * time.toFloat() - 0.5 * g * time.toFloat() * time.toFloat())

                        val canvas = holder.lockCanvas()
                        canvas.drawColor(Color.WHITE) // Clear previous frames

                        // Draw the projectile at current position
                        canvas.drawCircle(x.toFloat(), y.toFloat(), 10f, paint)

                        holder.unlockCanvasAndPost(canvas)

                        // Adjust time step for smoother animation (you can tweak this value)
                        Thread.sleep(50) // Adjust sleep time for smoother animation
                        time += 0.05 // Adjust this increment to control animation speed
                    }
                }.start()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                // Handle surface changes if needed
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                // Clean up and release resources
            }
        })
    }



    fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            fun surfaceDestroyed(holder: SurfaceHolder) {}
    fun z(view: View) {}
}
