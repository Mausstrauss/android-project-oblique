package com.example.obliquethrowsim

import android.graphics.Canvas
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

import com.example.obliquethrowsim.R

private fun Double.toRadians(): Double {
    return this * Math.PI / 180.0
}


class MainActivity : AppCompatActivity() {

    private lateinit var animationView: SurfaceView
    private lateinit var graphView: SurfaceView
    private lateinit var calculateButton: Button
    private lateinit var startAnimationButton: Button
    private lateinit var initialSpeedInput: EditText
    private lateinit var angleInput: EditText
    private lateinit var resultsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing views
        animationView = findViewById(R.id.animationView)
        graphView = findViewById(R.id.graphView)
        calculateButton = findViewById(R.id.calculateButton)
        startAnimationButton = findViewById(R.id.startAnimationButton)
        initialSpeedInput = findViewById(R.id.initialSpeedInput)
        angleInput = findViewById(R.id.angleInput)
        resultsTextView = findViewById(R.id.resultsTextView)

        // Setting up button click listeners
        calculateButton.setOnClickListener {
            calculateProjectileMotion()
        }

        startAnimationButton.setOnClickListener {
            startAnimation()
        }
    }

    private fun calculateProjectileMotion() {
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

    private fun drawGraph(speed: Double, angleRadians: Double, totalTime: Double) {
        val holder: SurfaceHolder = graphView.holder
        val thread = Thread {
            var canvas: Canvas?
            val paint = Paint()
            paint.color = Color.BLACK
            paint.strokeWidth = 2f

            val g = 9.81

            val points = mutableListOf<Pair<Float, Float>>()

            for (t in 0..(totalTime * 100).toInt()) {
                val time = t / 100.0
                val x = (speed * cos(angleRadians) * time).toFloat()
                val y = (speed * sin(angleRadians) * time - 0.5 * g * time * time).toFloat()
                points.add(Pair(x, y))
            }

            while (true) {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    canvas.drawColor(Color.WHITE) // Clear the canvas
                    for (i in 0 until points.size - 1) {
                        val start = points[i]
                        val end = points[i + 1]
                        canvas.drawLine(start.first, graphView.height - start.second, end.first, graphView.height - end.second, paint)
                    }
                    holder.unlockCanvasAndPost(canvas)
                    break
                }
            }
        }
        thread.start()
    }

    private fun startAnimation() {
        val holder: SurfaceHolder = animationView.holder
        val thread = Thread {
            var canvas: Canvas?
            val paint = Paint()
            paint.color = Color.BLACK
            paint.strokeWidth = 5f

            while (true) {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    // Draw your animation here
                    canvas.drawLine(0f, 0f, animationView.width.toFloat(), animationView.height.toFloat(), paint)
                    holder.unlockCanvasAndPost(canvas)
                }
                try {
                    Thread.sleep(16) // Adjust for frame rate
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        thread.start()
    }
}
