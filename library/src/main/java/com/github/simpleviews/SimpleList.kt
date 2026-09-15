package com.github.simpleviews

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.simpleviews.internal.dp
import com.github.simpleviews.internal.secondaryTextColor

/**
 * A list with no adapter, no ViewHolder and no LayoutManager:
 *
 * ```kotlin
 * // One line for a working list (rows are TextViews showing toString()):
 * list.items = listOf("Mercury", "Venus", "Earth")
 *
 * // Custom rows, still no adapter boilerplate:
 * list.submit(people) { row, person, _ ->
 *     row.findViewById<TextView>(R.id.name).text = person.name
 * }
 * list.onItemClick = { person, _ -> open(person) }
 * ```
 *
 * ```xml
 * <com.github.simpleviews.SimpleList
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     app:svItemLayout="@layout/row_person"
 *     app:svEmptyText="No people found"
 *     app:svDivider="true"
 *     app:svPullToRefresh="true"
 *     app:svColumns="2" />
 * ```
 *
 * It is a SwipeRefreshLayout under the hood, so pull-to-refresh is just
 * `app:svPullToRefresh="true"` + `list.onRefresh { ... }` (the spinner stops
 * itself when your block returns). If [items] becomes empty and svEmptyText
 * is set, a built-in empty message appears automatically.
 */
class SimpleList @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : SwipeRefreshLayout(context, attrs) {

    private val recycler = RecyclerView(context)
    private val emptyView = TextView(context).apply {
        gravity = Gravity.CENTER
        textSize = 15f
        setTextColor(context.secondaryTextColor())
        val pad = context.dp(32)
        setPadding(pad, pad, pad, pad)
        visibility = GONE
    }
    private val adapter = SimpleAdapter()

    /** Replaces the data and refreshes. */
    var items: List<Any> = emptyList()
        set(value) {
            field = value
            refresh()
        }

    /** Layout inflated for each row. Defaults to a single-TextView row. */
    var itemLayoutRes: Int = android.R.layout.simple_list_item_1
        set(value) {
            field = value
            adapter.notifyDataSetChanged()
        }

    /** Message shown when [items] is empty. */
    var emptyText: String? = null
        set(value) {
            field = value
            emptyView.text = value
            refresh()
        }

    /** Binds each row: (row view, item, position). Null = default toString() binding. */
    var onBind: ((View, Any, Int) -> Unit)? = null

    var onItemClick: ((item: Any, position: Int) -> Unit)? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SimpleList)
        itemLayoutRes = a.getResourceId(
            R.styleable.SimpleList_svItemLayout, android.R.layout.simple_list_item_1,
        )
        emptyText = a.getString(R.styleable.SimpleList_svEmptyText)
        val divider = a.getBoolean(R.styleable.SimpleList_svDivider, false)
        isEnabled = a.getBoolean(R.styleable.SimpleList_svPullToRefresh, false)
        val orientation = a.getInt(R.styleable.SimpleList_svOrientation, ORIENTATION_VERTICAL)
        val columns = a.getInt(R.styleable.SimpleList_svColumns, 1)
        val itemSpacing = a.getDimensionPixelSize(R.styleable.SimpleList_svItemSpacing, 0)
        a.recycle()

        recycler.layoutManager = when {
            columns > 1 -> GridLayoutManager(context, columns)
            orientation == ORIENTATION_HORIZONTAL ->
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            else -> LinearLayoutManager(context)
        }
        if (divider && columns <= 1) {
            recycler.addItemDecoration(
                DividerItemDecoration(
                    context,
                    if (orientation == ORIENTATION_HORIZONTAL) {
                        DividerItemDecoration.HORIZONTAL
                    } else {
                        DividerItemDecoration.VERTICAL
                    },
                ),
            )
        }
        if (itemSpacing > 0) {
            recycler.addItemDecoration(SpacingDecoration(itemSpacing, columns))
        }
        recycler.adapter = adapter

        addView(recycler)
        addView(emptyView)
    }

    /**
     * Replaces the data and the bind function in one call, keeping types:
     * `list.submit(people) { row, person, _ -> ... }`
     */
    fun <T : Any> submit(list: List<T>, bind: ((row: View, item: T, position: Int) -> Unit)? = null) {
        onBind = bind?.let { b ->
            { view: View, item: Any, position: Int -> b(view, item as T, position) }
        }
        items = list
    }

    /** Registers a pull-to-refresh callback; the spinner stops when the block returns. */
    fun onRefresh(block: () -> Unit) {
        setOnRefreshListener {
            block()
            isRefreshing = false
        }
    }

    /** Escape hatch to the underlying RecyclerView. */
    val recyclerView: RecyclerView get() = recycler

    private fun refresh() {
        adapter.notifyDataSetChanged()
        emptyView.visibility = if (items.isEmpty() && !emptyText.isNullOrEmpty()) VISIBLE else GONE
    }

    private inner class SimpleAdapter : RecyclerView.Adapter<Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(LayoutInflater.from(parent.context).inflate(itemLayoutRes, parent, false))

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val item = items[position]
            if (onBind == null && holder.row is TextView) {
                (holder.row as TextView).text = item.toString()
            }
            onBind?.invoke(holder.row, item, position)
            holder.row.setOnClickListener { onItemClick?.invoke(item, position) }
        }
    }

    private inner class Holder(val row: View) : RecyclerView.ViewHolder(row)

    private class SpacingDecoration(
        private val spacing: Int,
        private val columns: Int,
    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            val half = spacing / 2
            if (columns > 1) {
                outRect.set(half, half, half, half)
            } else {
                outRect.set(0, half, 0, half)
            }
        }
    }

    companion object {
        const val ORIENTATION_VERTICAL = 0
        const val ORIENTATION_HORIZONTAL = 1
    }
}
