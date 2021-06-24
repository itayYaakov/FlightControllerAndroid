package com.example.flightgearcontroller.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.flightgearcontroller.R
import kotlin.math.*


class JoystickView : View {
    val colorArray = intArrayOf(Color.RED, Color.GREEN, Color.BLUE)
    val positionArray = floatArrayOf(0F, 0.5F, 0.6F)
    val startColor = Color.RED
    val endColor = Color.BLUE
    private var mContext: Context? = context
    private var mLayout: ViewGroup? = null
    private var params: ViewGroup.LayoutParams? = null
    private var draw: DrawCanvas? = null
    private var stick: Bitmap? = null
    private var mEnabled: Boolean = false

    private var touchState = false

    private var STICK_ALPHA = 200
    private var LAYOUT_ALPHA = 200
    private var OFFSET = 0

    private var stickWidth = 0
    private var stickHeight = 0
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var positionX = 0
    private var positionY = 0
    private var maxX: Float = 0f
    private var maxY: Float = 0f
    private var minDistance: Float = 0f
    private var maxDistance: Float = 0f
    private var distance = 0f
    private var angle = 0f
    private var maxRotationAngle = 11.0f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun createJoystick(layout: ViewGroup?) {
        layout?.let {
            mLayout = layout
            stick = BitmapFactory.decodeResource(mContext!!.resources, R.drawable.joystick_in)
            stick = Bitmap.createScaledBitmap(stick!!, stickWidth, stickHeight, false)
            draw = DrawCanvas(mContext)
            params = mLayout!!.layoutParams
            params!!.width = layoutWidth
            params!!.height = layoutHeight

            maxDistance = sqrt(maxX.toDouble().pow(2.0) + maxY.toDouble().pow(2.0)).toFloat()

            layout.setOnTouchListener { v: View, e: MotionEvent ->
                drawStick(e)
                if (e.action == MotionEvent.ACTION_UP) {
                    v.performClick()
                }
                true
            }
//        }
            mEnabled = true;
        }
    }

    fun getOffset(): Int {
        return OFFSET
    }

    fun setOffset(offset: Int) {
        OFFSET = offset
    }

    fun getJoystickX(): Int {
        if (distance > this.minDistance && touchState) {
            return positionX * (1)
        } else {
            return 0
        }
    }

    fun getJoystickY(): Int {
        if (distance > this.minDistance && touchState) {
            return positionY * (-1)
        } else {
            return 0
        }
    }

    fun setMinimumDistance(minDistance: Float) {
        this.minDistance = minDistance
    }

    fun getDistance(): Float {
        if (distance > this.minDistance && touchState) {
            return distance
        } else {
            return 0F
        }
    }

    fun getAngle(): Float {
        if (distance > this.minDistance && touchState) {
            return angle
        } else {
            return 0F
        }
    }

    fun setStickSize(width: Int, height: Int) {
        stickWidth = width
        stickHeight = height
    }

    fun setLayoutSize(width: Int, height: Int) {
        layoutWidth = width
        layoutHeight = height
        maxX = (width / 2 - OFFSET).toFloat()
        maxY = (height / 2 - OFFSET).toFloat()
    }

    fun getLayoutWidth(): Int {
        return params!!.width
    }

    fun getLayoutHeight(): Int {
        return params!!.height
    }

    fun getPosition(): IntArray? {
        if (distance > this.minDistance && touchState) {
            return intArrayOf(positionX, positionY)
        } else {
            return intArrayOf(0, 0)
        }
    }

    var intensity: Float = 0.0f
    private fun drawStick(event: MotionEvent) {
        positionX = (event.x - params!!.width / 2).toInt()
        positionY = (event.y - params!!.height / 2).toInt()
        distance = sqrt(positionX.toDouble().pow(2.0) + positionY.toDouble().pow(2.0)).toFloat()
        distance = min(distance, maxDistance)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (distance <= maxX) {
                    draw!!.position(event.x.toDouble(), event.y.toDouble())
                    draw()
                    touchState = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchState) {
                    var x = 0f
                    var y = 0f
                    val quadrant = getAxisQuadrant(positionX.toFloat(), positionY.toFloat())
                    angle = calculateAngle(positionX.toFloat(), (positionY * (-1)).toFloat(), quadrant).toFloat()
                    if (distance <= maxX) {
                        x = event.x
                        y = event.y
                    } else if (distance > maxX) {
                        val posAngle = calculateAngle(positionX.toFloat(), positionY.toFloat(), quadrant)
                        x = (cos(Math.toRadians(posAngle)) * maxX).toFloat()
                        y = (sin(Math.toRadians(posAngle)) * maxY).toFloat()
                        x += (params!!.width / 2).toFloat()
                        y += (params!!.height / 2).toFloat()
                    }
                    draw!!.position(x.toDouble(), y.toDouble())
                    draw()

                    intensity = ((cos(Math.toRadians(angle.toDouble() * 2)) + 1.0f) / 2.0f).toFloat()
                    when (quadrant) {
                        1 -> {
                            mLayout!!.rotationX = map(angle, 0f, 90f, 0.0f, maxRotationAngle)
                            mLayout!!.rotationY = map(distance, 0.0f, maxX, 0.0f, maxRotationAngle) * intensity
                        }
                        2 -> {
                            mLayout!!.rotationX = map(angle, 90f, 180f, maxRotationAngle, 0.0f)
                            mLayout!!.rotationY = -map(distance, 0.0f, maxX, 0.0f, maxRotationAngle) * intensity
                        }
                        3 -> {
                            mLayout!!.rotationX = -map(angle, 180f, 270f, 0.0f, maxRotationAngle)
                            mLayout!!.rotationY =
                                -map(distance, 0.0f, maxX, 0.0f, maxRotationAngle) * intensity
                        }
                        4 -> {
                            mLayout!!.rotationX = -map(angle, 270f, 360f, maxRotationAngle, 0.0f)
                            mLayout!!.rotationY = map(distance, 0.0f, maxX, 0.0f, maxRotationAngle) * intensity
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                mLayout!!.removeView(draw)
                mLayout!!.rotationX = 0f
                mLayout!!.rotationY = 0f
                touchState = false
            }
        }
    }

    private fun getAxisQuadrant(x: Float, y: Float): Int {
        if (x >= 0 && y >= 0) {
            return 4
        } else if (x < 0 && y >= 0) {
            return 3
        } else if (x < 0 && y < 0) {
            return 2
        } else if (x >= 0 && y < 0) {
            return 1
        }
        return 0
    }

    private fun calculateAngle(x: Float, y: Float, quadrant: Int): Double {
        when (quadrant) {
            1 -> return Math.toDegrees(atan(y / x.toDouble()))
            2 -> return Math.toDegrees(atan(y / x.toDouble())) + 180
            3 -> return Math.toDegrees(atan(y / x.toDouble())) + 180
            4 -> return Math.toDegrees(atan(y / x.toDouble())) + 360
        }
        return 0.0
    }

    private fun draw() {
        try {
            mLayout!!.removeView(draw)
        } catch (e: Exception) {
        }
        mLayout!!.addView(draw)
    }

    fun map(x: Float, in_min: Float, in_max: Float, out_min: Float, out_max: Float): Float {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min
    }

    inner class DrawCanvas constructor(mContext: Context?) :
        View(mContext) {
        var myX: Float = 0.0f
        var myY: Float = 0.0f
        private var paint: Paint = Paint()
        private var paint2: Paint = Paint()

        public override fun onDraw(canvas: Canvas) {
            canvas.drawBitmap(stick!!, myX, myY, paint)
            paint2.color = Color.GREEN
            paint2.textSize = 40f
            canvas.drawText("DIS=%f".format(distance), myX.toFloat(), myY.toFloat() + 400, paint2)
            canvas.drawText("ANG=%f".format(angle), myX.toFloat(), myY.toFloat() + 460, paint2)
            canvas.drawText("rY %f".format(mLayout!!.rotationY), myX.toFloat(), myY.toFloat() - 20, paint2)
            canvas.drawText("rX %f".format(mLayout!!.rotationX), myX.toFloat(), myY.toFloat() - 60, paint2)
            canvas.drawText("in %f".format(intensity), myX.toFloat(), myY.toFloat() - 100, paint2)
        }

        fun position(pos_x: Double, pos_y: Double) {
            myX = (pos_x - stickWidth / 2).toFloat()
            myY = (pos_y - stickHeight / 2).toFloat()
        }
    }
}
