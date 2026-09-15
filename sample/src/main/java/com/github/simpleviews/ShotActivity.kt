package com.github.simpleviews.sample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.simpleviews.SimpleButton
import com.github.simpleviews.SimpleList
import com.github.simpleviews.ValidatingEditText

/**
 * Screenshot harness: renders one component per screen so the README can
 * show each view in isolation. Run with:
 *
 * ```
 * adb shell am start -n com.github.simpleviews.sample/.ShotActivity \
 *     --es shot url_image
 * ```
 */
class ShotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val shot = intent.getStringExtra("shot") ?: "button"
        setContentView(
            when (shot) {
                "url_image" -> R.layout.shot_url_image
                "url_circle" -> R.layout.shot_url_circle
                "avatar" -> R.layout.shot_avatar
                "button" -> R.layout.shot_button
                "button_loading" -> R.layout.shot_button_loading
                "password" -> R.layout.shot_password
                "validation" -> R.layout.shot_validation
                "card" -> R.layout.shot_card
                "webview" -> R.layout.shot_webview
                "html" -> R.layout.shot_html
                "banner" -> R.layout.shot_banner
                "empty_state" -> R.layout.shot_empty_state
                "vstack" -> R.layout.shot_vstack
                "hstack" -> R.layout.shot_hstack
                "scrollvstack" -> R.layout.shot_scrollvstack
                "center" -> R.layout.shot_center
                "image_page" -> R.layout.shot_image_page
                "title_page" -> R.layout.shot_title_page
                "profile" -> R.layout.shot_profile
                "listitem" -> R.layout.shot_listitem
                "keyvalue" -> R.layout.shot_keyvalue
                "bottombar" -> R.layout.shot_bottombar
                "simplelist" -> R.layout.shot_simplelist
                else -> R.layout.shot_button
            },
        )

        when (shot) {
            "button_loading" ->
                findViewById<SimpleButton>(R.id.shotLoadingButton).loading = true
            "validation" -> {
                val email = findViewById<ValidatingEditText>(R.id.shotEmail)
                email.setText("not-an-email")
                email.validateOrShowError()
            }
            "simplelist" ->
                findViewById<SimpleList>(R.id.shotList).submit(PLANETS) { row, name, _ ->
                    row.findViewById<TextView>(R.id.row_name).text = name
                    row.findViewById<TextView>(R.id.row_role).text = "planet"
                }
        }
    }

    private companion object {
        val PLANETS = listOf("Mercury", "Venus", "Earth", "Mars", "Jupiter")
    }
}
