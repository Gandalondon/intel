package com.gandalondon.intel

import android.content.Context
import android.graphics.Color as AColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

// Tokens from docs/style-guide.html
private val Ground = Color(0xFF1C1C1C)
private val Ink = Color(0xFFF2EFEA)
private val Orange = Color(0xFFFF6C10)
private val OnOrange = Color(0xFF1C1C1C)
private val Saira = FontFamily(Font(R.font.saira_regular, FontWeight.Normal), Font(R.font.saira_medium, FontWeight.Medium))

private const val WEEK_URL = "https://raw.githubusercontent.com/Gandalondon/intel/main/weeks/current.json"

// tasks: this week's objectives. finale: the season's final boss, shown after the last objective.
// next: date the next intel drops (defaults to next Tuesday). ended: replaces the countdown between seasons.
data class Week(val id: String, val tasks: List<String>, val finale: String?, val next: LocalDate?, val ended: String?)

private fun parse(json: String): Week? = try {
    val o = JSONObject(json)
    val t = o.getJSONArray("tasks")
    Week(
        o.getString("id"), List(t.length()) { t.getString(it) },
        o.optString("finale").ifBlank { null },
        o.optString("next").ifBlank { null }?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
        o.optString("ended").ifBlank { null },
    )
} catch (e: Exception) { null }

private fun countdown(next: LocalDate?): String {
    val today = LocalDate.now()
    val date = next ?: today.with(TemporalAdjusters.next(DayOfWeek.TUESDAY))
    val days = ChronoUnit.DAYS.between(today, date)
    return when {
        // Drop day reached but the new week is not in yet (guides still decoding, time zones)
        days <= 0 -> "Collecting intel. Check back later."
        days == 1L -> "Next intel arriving tomorrow."
        else -> "Next intel arriving in $days days."
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AColor.TRANSPARENT),
        )
        setContent { App(this) }
    }
}

@Composable
private fun App(ctx: Context) {
    val prefs = remember { ctx.getSharedPreferences("intel", Context.MODE_PRIVATE) }
    var week by remember { mutableStateOf(prefs.getString("week", null)?.let(::parse)) }
    var step by remember { mutableIntStateOf(prefs.getInt("step", 0)) }
    var failed by remember { mutableStateOf(false) }

    // Fetch this week's file on every open. A new id means a new week: start again at task 1.
    LaunchedEffect(Unit) {
        val text = withContext(Dispatchers.IO) {
            try {
                val c = URL("$WEEK_URL?t=${System.currentTimeMillis() / 60000}").openConnection() as HttpURLConnection
                c.connectTimeout = 8000; c.readTimeout = 8000; c.useCaches = false
                c.inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) { null }
        }
        val fresh = text?.let(::parse)
        if (fresh == null) { failed = true; return@LaunchedEffect }
        if (fresh.id != week?.id) {
            step = 0
            prefs.edit().putInt("step", 0).apply()
        }
        week = fresh
        prefs.edit().putString("week", text).apply()
    }

    fun go(to: Int) { step = to; prefs.edit().putInt("step", to).apply() }

    val w = week
    val steps = w?.let { it.tasks + listOfNotNull(it.finale) } ?: emptyList()
    val total = steps.size
    BackHandler(enabled = w != null && step > 0) { go(step - 1) }

    val taskStyle = TextStyle(fontFamily = Saira, fontWeight = FontWeight.Normal, fontSize = 48.sp, lineHeight = 55.sp,
        letterSpacing = (-0.01).em, color = Ink, lineBreak = LineBreak.Paragraph)
    val smallStyle = TextStyle(fontFamily = Saira, fontWeight = FontWeight.Medium, fontSize = 24.sp, lineHeight = 32.sp,
        letterSpacing = (-0.01).em)

    Column(
        Modifier.fillMaxSize().background(Ground).safeDrawingPadding()
            .padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 24.dp)
    ) {
        Column(Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite }, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            when {
                w == null && !failed -> {}
                w == null -> Text("No intel yet. Connect to the internet and open Intel again.", style = taskStyle)
                step < total -> {
                    val label = if (step < w.tasks.size) "Objective ${step + 1}" else "Final boss"
                    Text(label, style = smallStyle.copy(color = Orange))
                    Text(steps[step], style = taskStyle)
                }
                else -> Text(w.ended ?: countdown(w.next), style = taskStyle)
            }
        }
        if (w != null && step < total) {
            Spacer(Modifier.padding(top = 16.dp))
            Box(
                Modifier.fillMaxWidth().background(Orange)
                    // No pressed effect: the screen changing is the feedback (style guide)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { go(step + 1) }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(if (step == total - 1) "Finish" else "Continue", style = smallStyle.copy(color = OnOrange), textAlign = TextAlign.Center)
            }
        }
    }
}
