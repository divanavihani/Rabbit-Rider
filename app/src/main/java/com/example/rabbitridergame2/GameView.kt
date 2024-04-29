package com.example.rabbitridergame2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import java.lang.Exception

class GameView(var c: Context, var gameTask: Gametask) : View(c) {
    private var myPaint: Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private val otheranimal = ArrayList<HashMap<String, Any>>()
    var viewWidth = 0
    var viewHeight = 0
    private var rabbitX = 0f
    private var isGameOver = false

    init {
        myPaint = Paint()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        if (!isGameOver) {
            if (time % 700 < 10 + speed) {
                val map = HashMap<String, Any>()
                map["lane"] = (0..2).random()
                map["startTime"] = time
                otheranimal.add(map)
            }

            time += 20 + speed // Increase the time increment for faster yellow car movement

            val animalWidth = viewWidth / 5
            val animalHeight = animalWidth + 10

            myPaint!!.style = Paint.Style.FILL

            // Draw the red car
            val d = resources.getDrawable(R.drawable.rrabbit, null)
            d.setBounds(
                rabbitX.toInt(),
                viewHeight - 2 - animalHeight,
                (rabbitX + animalWidth).toInt(),
                viewHeight - 2
            )
            d.draw(canvas)

            myPaint!!.color = Color.GREEN
            var highScore = 0
            for (i in otheranimal.indices) {
                try {
                    val animalX = otheranimal[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                    var animalY = (time - (otheranimal[i]["startTime"] as Int)) * 2 // Increase the speed of the yellow car
                    val d2 = resources.getDrawable(R.drawable.ytiger, null)
                    d2.setBounds(
                        animalX + 25, animalY - animalHeight, animalX + animalWidth - 25, animalY
                    )
                    d2.draw(canvas)

                    if (otheranimal[i]["lane"] as Int == rabbitX.toInt() / (viewWidth / 3)) {
                        if (animalY > viewHeight - 2 - animalHeight && animalY < viewHeight - 2) {
                            isGameOver = true
                            gameTask.closeGame(score)
                        }
                    }

                    if (animalY > viewHeight + animalHeight) {
                        otheranimal.removeAt(i)
                        score++
                        speed = 1 + Math.abs(score / 8)
                        if (score > highScore) {
                            highScore = score
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            myPaint!!.color = Color.WHITE
            myPaint!!.textSize = 40f
            canvas.drawText("Score : $score", 80f, 80f, myPaint!!)
            canvas.drawText("Speed : $speed", 380f, 80f, myPaint!!)
        } else {
            // Game over condition
            myPaint!!.color = Color.RED
            myPaint!!.textSize = 80f
            canvas.drawText("Game Over", viewWidth / 4f, viewHeight / 2f, myPaint!!)
        }
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null && !isGameOver) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val x1 = event.x
                    if (x1 < viewWidth / 3) {
                        rabbitX = 0f
                    } else if (x1 < viewWidth * 2 / 3) {
                        rabbitX = (viewWidth / 3).toFloat()
                    } else {
                        rabbitX = (viewWidth * 2 / 3).toFloat()
                    }
                    invalidate()
                }
                MotionEvent.ACTION_UP -> {
                }
            }
        } else if (event != null && isGameOver && event.action == MotionEvent.ACTION_DOWN) {
            // Restart the game
            isGameOver = false
            score = 0
            speed = 1
            time = 0
            otheranimal.clear()
            invalidate()
        }
        return true
    }
}