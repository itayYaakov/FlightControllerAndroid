package com.example.flightgearcontroller.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.flightgearcontroller.R
import kotlin.math.*

// Joystick view
class JoystickView : View {
    // startegy design pattern
    public var sendUpdateEvent: IChange? = null

    private var mContext: Context? = context
    private var mLayout: ViewGroup? = null
    private var params: ViewGroup.LayoutParams? = null
    private var draw: DrawCanvas? = null
    private var stick: Bitmap? = null

    private var touchState = false

    //  default value
    private var OFFSET = 0
    private var xAxisName : String = ""
    private var yAxisName : String = ""
    private var stickWidth = 0
    private var stickHeight = 0
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var positionX = 0
    private var positionY = 0
    private var maxX: Float = 0f
    private var maxY: Float = 0f
    private var maxDistance: Float = 0f
    private var yAxisValueNorm: Float = 0f
    private var xAxisValueNorm: Float = 0f
    private var distance = 0f
    private var angle = 0f
    private var threshold = 0f
    private var maxRotationAngle = 11.0f

    // default constructors for View
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    // make joystick active
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

            // listen to touch events on layout
            layout.setOnTouchListener { v: View, e: MotionEvent ->
                drawStick(e)
                if (e.action == MotionEvent.ACTION_UP) {
                    v.performClick()
                }
                // (startegy design pattern) notify values change
                sendUpdateEvent?.onChange(xAxisName, xAxisValueNorm)
                sendUpdateEvent?.onChange(yAxisName, yAxisValueNorm)
                true
            }
        }
    }

    // Setters 
   fun setXAxisName (name : String) {
        this.xAxisName = name
    }

   fun setYAxisName (name : String) {
        this.yAxisName = name
    }

    fun setOffset(offset: Int) {
        OFFSET = offset
    }

    fun setThreshold (threshold : Float) {
        this.threshold = threshold
    }

    fun setStickSize(width: Int, height: Int) {
        this.stickWidth = width
        this.stickHeight = height
    }

    fun setLayoutSize(width: Int, height: Int) {
        this.layoutWidth = width
        this.layoutHeight = height
        this.maxX = (width / 2 - this.OFFSET).toFloat()
        this.maxY = (height / 2 - this.OFFSET).toFloat()
    }

    // draw inner circle
    private fun draw() {
        try {
            mLayout!!.removeView(draw)
        } catch (e: Exception) {
        }
        mLayout!!.addView(draw)
    }

    private fun drawStick(event: MotionEvent) {
        val halfWidth: Float = (params!!.width / 2).toFloat()
        val halfHeight: Float = (params!!.height / 2).toFloat()
        positionX = (event.x - halfWidth).toInt()
        positionY = (event.y - halfHeight).toInt()
        distance = sqrt(positionX.toDouble().pow(2.0) + positionY.toDouble().pow(2.0)).toFloat()
        distance = min(distance, maxDistance)

        // set values to send to listeners
        xAxisValueNorm = map(positionX.toFloat(), -halfWidth, halfWidth, -1f, 1f)
        yAxisValueNorm = -map(positionY.toFloat(), -halfHeight, halfHeight, -1f, 1f)

        // don't send values smaller than threshold
        if (abs(yAxisValueNorm) < threshold) yAxisValueNorm = 0.0f
        if (abs(xAxisValueNorm) < threshold) xAxisValueNorm = 0.0f

        // set joystick draw position according to touch event
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
                        x += halfWidth
                        y += halfHeight
                    }
                    draw!!.position(x.toDouble(), y.toDouble())
                    draw()

                    // handle 3d joystick rotations
                    val angleIntensity: Float = ((cos(Math.toRadians(angle.toDouble() * 2)) + 1.0f) / 2.0f).toFloat()
                    val distanceIntensity: Float = distance / maxX
                    when (quadrant) {
                        1 -> {
                            mLayout!!.rotationX = map(angle, 0f, 90f, 0.0f, maxRotationAngle) * distanceIntensity
                            mLayout!!.rotationY = map(distance, 0.0f, maxX, 0.0f, maxRotationAngle) * angleIntensity
                        }
                        2 -> {
                            mLayout!!.rotationX = map(angle, 90f, 180f, maxRotationAngle, 0.0f) * distanceIntensity
                            mLayout!!.rotationY = -map(distance, 0.0f, maxX, 0.0f, maxRotationAngle) * angleIntensity
                        }
                        3 -> {
                            mLayout!!.rotationX = -map(angle, 180f, 270f, 0.0f, maxRotationAngle) * distanceIntensity
                            mLayout!!.rotationY =
                                -map(distance, 0.0f, maxX, 0.0f, maxRotationAngle) * angleIntensity
                        }
                        4 -> {
                            mLayout!!.rotationX = -map(angle, 270f, 360f, maxRotationAngle, 0.0f) * distanceIntensity
                            mLayout!!.rotationY = map(distance, 0.0f, maxX, 0.0f, maxRotationAngle) * angleIntensity
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

    // map values
    fun map(x: Float, in_min: Float, in_max: Float, out_min: Float, out_max: Float): Float {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min
    }

    // Inner class for canvas
    inner class DrawCanvas constructor(mContext: Context?) :
        View(mContext) {
        var myX: Float = 0.0f
        var myY: Float = 0.0f
        private var paint: Paint = Paint()

        public override fun onDraw(canvas: Canvas) {
            canvas.drawBitmap(stick!!, myX, myY, paint)
        }

        fun position(pos_x: Double, pos_y: Double) {
            myX = (pos_x - stickWidth / 2).toFloat()
            myY = (pos_y - stickHeight / 2).toFloat()
        }
    }
}
