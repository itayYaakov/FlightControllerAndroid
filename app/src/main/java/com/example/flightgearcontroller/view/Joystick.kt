package com.example.flightgearcontroller.view


@Suppress("LiftReturnOrAssignment")
class Joystick(context: Context?, layout: ViewGroup?, stick_res_id: Int) : View() {
   private var mContext: Context? = context
   private var mLayout: ViewGroup? = layout
   private var params: ViewGroup.LayoutParams? = null
   private var draw: DrawCanvas? = null
   private var paint: Paint? = null
   private var stick: Bitmap? = null

   private var touchState = false

   private var STICK_ALPHA = 200
   private var LAYOUT_ALPHA = 200
   private var OFFSET = 0

   private var stickWidth = 0
   private var stickHeight = 0
   private var positionX = 0
   private var position_y = 0
   private var minDistance = 0
   private var distance = 0f
   private var angle = 0f

//    constructor(context: Context) : this(context, null)
//    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
   constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

//    init {
//        stick = BitmapFactory.decodeResource(mContext!!.resources, stick_res_id)
//        stickWidth = stick!!.getWidth()
//        stickHeight = stick!!.getHeight()
//        draw = DrawCanvas(mContext)
//        paint = Paint()
//        params = mLayout!!.layoutParams
//    }

   inner class DrawCanvas constructor(mContext: Context?) :
       View(mContext) {
       private var x: Double = 0.0
       private var y: Double = 0.0

       public override fun onDraw(canvas: Canvas) {
           canvas.drawBitmap(stick!!, x.toFloat(), y.toFloat(), paint)
       }

       fun position(pos_x: Double, pos_y: Double) {
           x = pos_x - stickWidth / 2
           y = pos_y - stickHeight / 2
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
           return position_y * (-1)
       } else {
           return 0
       }
   }

   fun setMinimumDistance(minDistance: Int) {
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
       stick = Bitmap.createScaledBitmap(stick!!, width, height, false)
       stickWidth = stick!!.getWidth()
       stickHeight = stick!!.getHeight()
   }

   fun setLayoutSize(width: Int, height: Int) {
       params!!.width = width
       params!!.height = height
   }

   fun getLayoutWidth(): Int {
       return params!!.width
   }

   fun getLayoutHeight(): Int {
       return params!!.height
   }

   fun getPosition(): IntArray? {
       if (distance > this.minDistance && touchState) {
           return intArrayOf(positionX, position_y)
       } else {
           return intArrayOf(0, 0)
       }
   }

   override fun onTouchEvent(event: MotionEvent): Boolean {
       // // if disabled we don't move the
       // if (!mEnabled) {
       //     return true;
       // }


       // to move the button according to the finger coordinate
       // (or limited to one axe according to direction option
       // mPosY = mButtonDirection < 0 ? mCenterY : (int) event.getY(); // direction negative is horizontal axe
       // mPosX = mButtonDirection > 0 ? mCenterX : (int) event.getX(); // direction positive is vertical axe

       // if (event.getAction() == MotionEvent.ACTION_UP) {

       //     // stop listener because the finger left the touch screen
       //     mThread.interrupt();

       //     // re-center the button or not (depending on settings)
       //     if (mAutoReCenterButton) {
       //         resetButtonPosition();

       //         // update now the last strength and angle which should be zero after resetButton
       //         if (mCallback != null)
       //             mCallback.onMove(getAngle(), getStrength());
       //     }

       //     // if mAutoReCenterButton is false we will send the last strength and angle a bit
       //     // later only after processing new position X and Y otherwise it could be above the border limit
       // }

       // if (event.getAction() == MotionEvent.ACTION_DOWN) {
       //     if (mThread != null && mThread.isAlive()) {
       //         mThread.interrupt();
       //     }

       //     mThread = new Thread(this);
       //     mThread.start();

       //     if (mCallback != null)
       //         mCallback.onMove(getAngle(), getStrength());
       // }

       when (event.getAction()) {
           MotionEvent.ACTION_DOWN -> {
           }
           MotionEvent.ACTION_MOVE -> {
           }
           MotionEvent.ACTION_UP -> {
           }
       }

       return true;
   }


   fun drawStick(arg1: MotionEvent) {
       positionX = (arg1.x - params!!.width / 2).toInt()
       position_y = (arg1.y - params!!.height / 2).toInt()
       distance = sqrt(positionX.toDouble().pow(2.0) + position_y.toDouble().pow(2.0)).toFloat()
       angle = calculateAngle(positionX.toFloat(), (position_y * (-1)).toFloat()).toFloat()
       if (arg1.action == MotionEvent.ACTION_DOWN) {
           if (distance <= params!!.width / 2 - OFFSET) {
               draw!!.position(arg1.x.toDouble(), arg1.y.toDouble())
               draw()
               touchState = true
           }
       } else if (arg1.action == MotionEvent.ACTION_MOVE && touchState) {
           if (distance <= params!!.width / 2 - OFFSET) {
               draw!!.position(arg1.x.toDouble(), arg1.y.toDouble())
               draw()
           } else if (distance > params!!.width / 2 - OFFSET) {
               var x = (cos(
                   Math.toRadians(
                       calculateAngle(
                           positionX.toFloat(),
                           position_y.toFloat()
                       )
                   )
               ) * (params!!.width / 2 - OFFSET)).toFloat()
               var y = (sin(
                   Math.toRadians(
                       calculateAngle(
                           positionX.toFloat(),
                           position_y.toFloat()
                       )
                   )
               ) * (params!!.height / 2 - OFFSET)).toFloat()
               x += (params!!.width / 2).toFloat()
               y += (params!!.height / 2).toFloat()
               draw!!.position(x.toDouble(), y.toDouble())
               draw()
           } else {
               mLayout!!.removeView(draw)
           }
       } else if (arg1.action == MotionEvent.ACTION_UP) {
           mLayout!!.removeView(draw)
           touchState = false
       }
   }

   private fun calculateAngle(x: Float, y: Float): Double {
       if (x >= 0 && y >= 0) {
           return Math.toDegrees(atan(y / x.toDouble()))
       } else if (x < 0 && y >= 0) {
           return Math.toDegrees(atan(y / x.toDouble())) + 180
       } else if (x < 0 && y < 0) {
           return Math.toDegrees(atan(y / x.toDouble())) + 180
       } else if (x >= 0 && y < 0) {
           return Math.toDegrees(atan(y / x.toDouble())) + 360
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

}