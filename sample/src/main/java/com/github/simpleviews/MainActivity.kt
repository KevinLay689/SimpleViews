package com.github.simpleviews.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.simpleviews.SimpleList

/**
 * The menu is itself a SimpleList — the dogfood demo: a working, tappable
 * list in one property assignment plus one click handler.
 */
class MainActivity : AppCompatActivity() {

    private data class Demo(val title: String, val activity: Class<out AppCompatActivity>) {
        override fun toString() = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val menu = findViewById<SimpleList>(R.id.menu)
        menu.items = listOf(
            Demo("UrlImageView · AvatarView · ImagePage", ImageDemo::class.java),
            Demo("SimpleButton — rounded, ripple, loading", ButtonsDemo::class.java),
            Demo("Forms — validation + password toggle", FormsDemo::class.java),
            Demo("VStack · HStack · SimpleCard · TitlePage", StacksDemo::class.java),
            Demo("SimpleList — linear, grid, empty, refresh", ListDemo::class.java),
            Demo("Rows — ListItem, Profile, Banner, Empty", RowsDemo::class.java),
            Demo("BottomActionBar", BottomBarDemo::class.java),
            Demo("SafeLayout · HtmlTextView · SimpleWebView", MiscDemo::class.java),
        )
        menu.onItemClick = { item, _ ->
            startActivity(Intent(this, (item as Demo).activity))
        }
    }
}
