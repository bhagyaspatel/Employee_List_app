package com.example.companyemployee.ui.list

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.companyemployee.R

//Using this class, we can detect and react to events related to:
//swipe items (from right, left or both sides)
//move items (up, down or both)

abstract class EmployeeListSwipeToDelete (context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)
    private val intrinsicHeight = deleteIcon?.intrinsicHeight
    private val intrinsicWidth = deleteIcon?.intrinsicWidth
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#f44336")


    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false  //We don't want to support moving items up/down
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.absoluteAdapterPosition

    }

    //Lets draw our delete view
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        //Draw red delete background
        background.color = backgroundColor
        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        background.draw(c)

        //Calculate position of a deleteIcon
        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight!!)/2
        val deleteIconMargin = (itemHeight - intrinsicHeight!!)/2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth!!
        val delteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight

        //Draw delete Icon
        deleteIcon?.setBounds(deleteIconLeft, deleteIconTop, delteIconRight, deleteIconBottom)
        deleteIcon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

//    Next thing is to connect Callback with our RecyclerView:

}