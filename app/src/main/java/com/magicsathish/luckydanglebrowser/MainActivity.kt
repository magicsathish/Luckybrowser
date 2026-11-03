package com.magicsathish.luckydanglebrowser

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView
import kotlin.math.sqrt

class MainActivity : Activity() {
    private lateinit var runtime: GeckoRuntime
    private lateinit var session: GeckoSession
    private lateinit var geckoView: GeckoView
    private lateinit var address: EditText
    private lateinit var dangle: DangleOverlayView
    private lateinit var browserFrame: FrameLayout
    private var dangleVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runtime = GeckoRuntime.create(this)
        session = GeckoSession()
        session.open(runtime)
        buildInterface()
        session.loadUri("https://www.google.com")
    }

    private fun buildInterface() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(11, 18, 32))
        }
        val toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 8, 8, 8)
            setBackgroundColor(Color.rgb(15, 23, 42))
        }
        val back = toolButton("‹") { if (::session.isInitialized) session.goBack() }
        val forward = toolButton("›") { session.goForward() }
        val reload = toolButton("↻") { session.reload() }
        address = EditText(this).apply {
            setSingleLine(true)
            hint = "Search or enter website"
            textSize = 14f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            setPadding(12, 0, 12, 0)
            setBackgroundColor(Color.rgb(35, 48, 68))
            imeOptions = EditorInfo.IME_ACTION_GO
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) { navigate(address.text.toString()); true } else false
            }
        }
        val go = toolButton("Go") { navigate(address.text.toString()) }
        toolbar.addView(back, buttonParams())
        toolbar.addView(forward, buttonParams())
        toolbar.addView(address, LinearLayout.LayoutParams(0, dp(44), 1f).apply { setMargins(4, 0, 4, 0) })
        toolbar.addView(go, buttonParams())
        toolbar.addView(reload, buttonParams())
        root.addView(toolbar)

        browserFrame = FrameLayout(this)
        geckoView = GeckoView(this)
        geckoView.setSession(session)
        browserFrame.addView(geckoView, FrameLayout.LayoutParams(-1, -1))
        dangle = DangleOverlayView(this)
        browserFrame.addView(dangle, FrameLayout.LayoutParams(-1, -1))
        root.addView(browserFrame, LinearLayout.LayoutParams(-1, 0, 1f))

        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(6, 6, 6, 6)
            setBackgroundColor(Color.rgb(15, 23, 42))
        }
        controls.addView(toolButton("⌂ Home") { session.loadUri("https://www.google.com") }, LinearLayout.LayoutParams(0, dp(48), 1f))
        controls.addView(toolButton("✨ Dangle") { toggleDangle() }, LinearLayout.LayoutParams(0, dp(48), 1f))
        controls.addView(toolButton("🎨 Studio") { session.loadUri("resource://android/assets/luckydangle/index.html") }, LinearLayout.LayoutParams(0, dp(48), 1f))
        controls.addView(toolButton("⚙") { showSettings() }, LinearLayout.LayoutParams(0, dp(48), 0.65f))
        root.addView(controls)
        setContentView(root)
    }

    private fun navigate(input: String) {
        val value = input.trim()
        if (value.isEmpty()) return
        val target = when {
            value.startsWith("https://") || value.startsWith("http://") -> value
            value.contains(".") && !value.contains(" ") -> "https://$value"
            else -> "https://www.google.com/search?q=" + java.net.URLEncoder.encode(value, "UTF-8")
        }
        session.loadUri(target)
        address.clearFocus()
    }

    private fun toggleDangle() {
        dangleVisible = !dangleVisible
        dangle.visibility = if (dangleVisible) View.VISIBLE else View.GONE
    }

    private fun showSettings() {
        val options = arrayOf("Show / hide Lucky Dangle", "Traditional 🍋", "Drishti 👹", "Nazar 🧿", "Bell 🔔", "Open original Lucky Dangle Studio")
        AlertDialog.Builder(this)
            .setTitle("Lucky Dangle Browser")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> toggleDangle()
                    1 -> dangle.setCharm("🍋")
                    2 -> dangle.setCharm("👹")
                    3 -> dangle.setCharm("🧿")
                    4 -> dangle.setCharm("🔔")
                    5 -> session.loadUri("resource://android/assets/luckydangle/index.html")
                }
            }
            .setNeutralButton("Overlay opacity") { _, _ -> showOpacityDialog() }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showOpacityDialog() {
        val seek = SeekBar(this).apply { max = 100; progress = (dangle.overlayAlpha * 100).toInt() }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(28, 12, 28, 4); addView(TextView(this@MainActivity).apply { text = "Lucky Dangle transparency" }); addView(seek) }
        AlertDialog.Builder(this).setTitle("Display")
            .setView(box).setPositiveButton("Apply") { _, _ -> dangle.overlayAlpha = seek.progress / 100f; dangle.invalidate() }
            .setNegativeButton("Cancel", null).show()
    }

    private fun toolButton(label: String, action: () -> Unit): Button = Button(this).apply {
        text = label
        textSize = 12f
        isAllCaps = false
        setTextColor(Color.WHITE)
        setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(30, 41, 59)))
        setOnClickListener { action() }
        minWidth = 0
        minimumWidth = 0
        setPadding(6, 0, 6, 0)
    }

    private fun buttonParams() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(44)).apply { setMargins(2, 0, 2, 0) }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    override fun onBackPressed() {
        session.goBack()
    }

    override fun onDestroy() {
        if (::session.isInitialized) session.close()
        super.onDestroy()
    }
}

private class DangleOverlayView(activity: Activity) : View(activity) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val points = Array(9) { FloatArray(4) }
    private var initialized = false
    private var dragging = false
    private var charm = "🍋"
    var overlayAlpha = 0.92f
    private var scale = 1f
    private var lastX = 0f
    private var lastY = 0f

    init { setLayerType(View.LAYER_TYPE_SOFTWARE, null); isClickable = true }

    fun setCharm(value: String) { charm = value; invalidate() }

    private fun initPoints() {
        val x = width * 0.76f
        for (i in points.indices) {
            points[i][0] = x; points[i][1] = i * 24f + 6f
            points[i][2] = x; points[i][3] = i * 24f + 6f
        }
        initialized = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!initialized && width > 0) initPoints()
        if (!initialized) return
        val save = canvas.save()
        canvas.scale(scale, scale, points.last()[0], points.last()[1])
        paint.color = Color.argb((overlayAlpha * 95).toInt(), 0, 0, 0)
        paint.strokeWidth = 5f; paint.style = Paint.Style.STROKE; paint.strokeCap = Paint.Cap.ROUND
        val path = Path().apply { moveTo(points[0][0] + 5, points[0][1] + 7); for (i in 1 until points.size) lineTo(points[i][0] + 5, points[i][1] + 7) }
        canvas.drawPath(path, paint)
        paint.color = Color.rgb(120, 53, 15); paint.strokeWidth = 3f
        val rope = Path().apply { moveTo(points[0][0], points[0][1]); for (i in 1 until points.size) lineTo(points[i][0], points[i][1]) }
        canvas.drawPath(rope, paint)
        paint.style = Paint.Style.FILL; paint.textSize = 52f; paint.textAlign = Paint.Align.CENTER
        paint.color = Color.argb((overlayAlpha * 255).toInt(), 255, 255, 255)
        canvas.drawText(charm, points.last()[0], points.last()[1] + 52f, paint)
        canvas.restoreToCount(save)
        if (!dragging) stepPhysics()
        postInvalidateOnAnimation()
    }

    private fun stepPhysics() {
        for (i in 1 until points.size) {
            val p = points[i]
            val vx = (p[0] - p[2]) * 0.985f
            val vy = (p[1] - p[3]) * 0.985f
            p[2] = p[0]; p[3] = p[1]
            p[0] += vx; p[1] += vy + 0.35f
        }
        repeat(4) {
            for (i in 0 until points.size - 1) {
                val a = points[i]; val b = points[i + 1]
                val dx = b[0] - a[0]; val dy = b[1] - a[1]
                val dist = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
                val diff = (dist - 24f) / dist
                if (i > 0) { a[0] += dx * diff * 0.5f; a[1] += dy * diff * 0.5f }
                if (!(dragging && i + 1 == points.lastIndex)) { b[0] -= dx * diff * 0.5f; b[1] -= dy * diff * 0.5f }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val tip = points.last()
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val nearCharm = sqrt((event.x - tip[0]) * (event.x - tip[0]) + (event.y - (tip[1] + 25f)) * (event.y - (tip[1] + 25f))) < 90f
                val nearRope = event.x > tip[0] - 55f && event.x < tip[0] + 55f && event.y < tip[1] + 30f
                if (!nearCharm && !nearRope) return false
                dragging = true; lastX = event.x; lastY = event.y; parent?.requestDisallowInterceptTouchEvent(true); return true
            }
            MotionEvent.ACTION_MOVE -> if (dragging) {
                val dx = event.x - lastX; val dy = event.y - lastY
                for (i in 1 until points.size) { points[i][0] += dx; points[i][1] += dy }
                lastX = event.x; lastY = event.y; invalidate(); return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (dragging) { dragging = false; performClick(); return true }
        }
        return dragging
    }

    override fun performClick(): Boolean { super.performClick(); return true }
}
