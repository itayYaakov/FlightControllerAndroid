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
    public var sendUpdateEvent: IChange? = null

    private var mContext: Context? = context
    private var mLayout: ViewGroup? = null
    private var params: ViewGroup.LayoutParams? = null
    private var draw: DrawCanvas? = null
    private var stick: Bitmap? = null
    private var mEnabled: Boolean = false

    private var touchState = false

    private var OFFSET = 0

    private var stickWidth = 0
    private var stickHeight = 0
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var positionX = 0
    private var positionY = 0
    private var maxX: Float = 0f
    private var maxY: Float = 0f
    private var maxDistance: Float = 0f
    private var elevator: Float = 0f
    private var aileron: Float = 0f
    private var distance = 0f
    private var angle = 0f
    private var threshold = 0f
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
                sendUpdateEvent?.onChange("elevator", elevator)
                sendUpdateEvent?.onChange("aileron", aileron)
                true
            }
            mEnabled = true;
        }
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

    var angleIntensity: Float = 0.0f
    var distanceIntensity: Float = 0.0f

    private fun drawStick(event: MotionEvent) {
        val halfWidth: Float = (params!!.width / 2).toFloat()
        val halfHeight: Float = (params!!.height / 2).toFloat()
        positionX = (event.x - halfWidth).toInt()
        positionY = (event.y - halfHeight).toInt()
        distance = sqrt(positionX.toDouble().pow(2.0) + positionY.toDouble().pow(2.0)).toFloat()
        distance = min(distance, maxDistance)

        aileron = map(positionX.toFloat(), -halfWidth, halfWidth, -1f, 1f)
        elevator = -map(positionY.toFloat(), -halfHeight, halfHeight, -1f, 1f)

        if (abs(elevator) < threshold) elevator = 0.0f
        if (abs(aileron) < threshold) aileron = 0.0f

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

                    angleIntensity = ((cos(Math.toRadians(angle.toDouble() * 2)) + 1.0f) / 2.0f).toFloat()
                    distanceIntensity = distance / maxX
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
            canvas.drawText("a in %f".format(angleIntensity), myX.toFloat(), myY.toFloat() - 100, paint2)
            canvas.drawText("d in %f".format(distanceIntensity), myX.toFloat(), myY.toFloat() - 140, paint2)
        }

        fun position(pos_x: Double, pos_y: Double) {
            myX = (pos_x - stickWidth / 2).toFloat()
            myY = (pos_y - stickHeight / 2).toFloat()
        }
    }
}
