package com.github.simpleviews.sample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.simpleviews.BottomActionBar
import com.github.simpleviews.BannerView
import com.github.simpleviews.EmptyStateView
import com.github.simpleviews.ListItemView
import com.github.simpleviews.PasswordEditText
import com.github.simpleviews.ProfileHeader
import com.github.simpleviews.SectionHeader
import com.github.simpleviews.SimpleButton
import com.github.simpleviews.SimpleList
import com.github.simpleviews.ValidatingEditText
import com.github.simpleviews.hideKeyboard
import com.github.simpleviews.toast

class ImageDemo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_image)
    }
}

class ButtonsDemo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_buttons)
        val loadingButton = findViewById<SimpleButton>(R.id.loadingButton)
        loadingButton.setOnClickListener {
            loadingButton.loading = true
            loadingButton.postDelayed({ loadingButton.loading = false }, 2000)
        }
    }
}

class FormsDemo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_forms)
        val email = findViewById<ValidatingEditText>(R.id.emailField)
        val phone = findViewById<ValidatingEditText>(R.id.phoneField)
        val password = findViewById<PasswordEditText>(R.id.passwordField)
        findViewById<SimpleButton>(R.id.submitButton).setOnClickListener {
            val passwordOk = (password.text?.length ?: 0) >= 4
            if (!passwordOk) password.error = "At least 4 characters"
            val valid = email.validateOrShowError() and
                phone.validateOrShowError() and passwordOk
            if (valid) {
                hideKeyboard()
                toast("Welcome!")
            }
        }
    }
}

class StacksDemo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_stacks)
    }
}

class ListDemo : AppCompatActivity() {

    private val names = listOf(
        "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_list)

        val linear = findViewById<SimpleList>(R.id.linearList)
        val grid = findViewById<SimpleList>(R.id.gridList)

        fun refill() {
            linear.submit(names) { row, name, _ ->
                row.findViewById<TextView>(R.id.row_name).text = name
                row.findViewById<TextView>(R.id.row_role).text = "planet"
            }
            grid.submit(names) { row, name, _ ->
                row.findViewById<TextView>(R.id.row_name).text = name
                row.findViewById<TextView>(R.id.row_role).text = "planet"
            }
        }
        refill()

        linear.onRefresh {
            linear.submit(names.shuffled()) { row, name, _ ->
                row.findViewById<TextView>(R.id.row_name).text = name
                row.findViewById<TextView>(R.id.row_role).text = "planet"
            }
            toast("Refreshed")
        }
        grid.onItemClick = { item, _ -> toast("Tapped $item") }

        findViewById<SimpleButton>(R.id.clearButton).setOnClickListener {
            if (linear.items.isEmpty()) refill() else {
                linear.items = emptyList()
                grid.items = emptyList()
            }
        }
    }
}

class RowsDemo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_rows)

        findViewById<SectionHeader>(R.id.peopleHeader).onActionClick = { toast("See all people") }
        findViewById<ProfileHeader>(R.id.profile).onActionClick = { toast("Edit profile") }

        findViewById<ListItemView>(R.id.rowNotifications).setOnClickListener { toast("Notifications") }
        findViewById<ListItemView>(R.id.rowStorage).setOnClickListener { toast("Storage") }
        findViewById<ListItemView>(R.id.rowFriend).setOnClickListener { toast("Friend profile") }

        findViewById<BannerView>(R.id.paymentBanner).onDismissed = { toast("Banner dismissed") }

        findViewById<EmptyStateView>(R.id.emptyOrders).onButtonClickListener = { toast("Browsing…") }
    }
}

class BottomBarDemo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_bottombar)
        findViewById<BottomActionBar>(R.id.bottomBar).onButtonClick = { toast("Continued!") }
    }
}

class MiscDemo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_misc)
    }
}
