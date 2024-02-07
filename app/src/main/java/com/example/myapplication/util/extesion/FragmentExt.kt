package com.example.myapplication.util.extesion

import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

private var listener: ViewTreeObserver.OnGlobalLayoutListener? = null


fun Fragment.showToast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.stopKeyBoardListener() {
    if (listener != null)
        view?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
}

fun Fragment.handleKeyBoardApparition(aboveView: View) {
    stopKeyBoardListener()
    with(view!!) {
        listener = ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            getWindowVisibleDisplayFrame(r)
            val heightDiff: Int = bottom - r.bottom
            val suggestionsBarHeight =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    25f,
                    activity!!.resources.displayMetrics
                ).toInt()
            aboveView.translationY = -(heightDiff + suggestionsBarHeight).toFloat()
        }
        viewTreeObserver?.addOnGlobalLayoutListener(listener)
    }

}

fun Fragment.closeFragment() {
    findNavController().popBackStack()
}