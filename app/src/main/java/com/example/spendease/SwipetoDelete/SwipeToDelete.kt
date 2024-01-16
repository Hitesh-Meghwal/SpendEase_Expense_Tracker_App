package com.example.spendease.SwipetoDelete

import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.spendease.Adapter.TransactionAdapter
import com.example.spendease.Model.TransactionData
import com.example.spendease.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

@Suppress("DEPRECATION")
class SwipeToDelete(private val firestore: FirebaseFirestore,
                    private val adapter: TransactionAdapter,
                    private val rootView: View,
                    private val transactionlist: MutableList<TransactionData>,
                    private val getdata:()-> Unit) : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition  //taking positions of views
        if(direction == ItemTouchHelper.RIGHT && pos >= 0 && pos < transactionlist.size){
            val recentlyDeletedTransaction = transactionlist.removeAt(pos)
            adapter.notifyItemRemoved(pos)
            //after removing from recyclerview it takes id and delete that item from firebase!
            val transactionId = recentlyDeletedTransaction.id
            if (transactionId!=null){
                deleteTransactions(transactionId)
            }
            val snackbar = Snackbar.make(rootView,"Transaction Deleted!",Snackbar.LENGTH_SHORT)
            snackbar.setAction("Undo"){
                //Restore the deleted data
                handleUndoAction(recentlyDeletedTransaction,pos)
            }
            snackbar.show()
        }
    }
    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val swipeColor = Color.parseColor("#C65D5D")
        RecyclerViewSwipeDecorator.Builder(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
            .addSwipeRightBackgroundColor(swipeColor)
            .addSwipeRightActionIcon(R.drawable.baseline_delete_24)
            .addSwipeRightLabel("Delete")
            .setSwipeRightLabelTextSize(1,18f)
            .setSwipeRightLabelColor(Color.BLACK)
            .create()
            .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
    private fun handleUndoAction(recentlyDeletedTransaction : TransactionData?,pos : Int){
        if(recentlyDeletedTransaction != null) {
            transactionlist.add(pos, recentlyDeletedTransaction)
            adapter.notifyItemInserted(pos)
            val transactionId = recentlyDeletedTransaction.id
            if (transactionId != null) {
                firestore.collection("Transactions")
                    .document(FirebaseAuth.getInstance().uid.toString())
                    .collection("TransactionList")
                    .document(transactionId)
                    .set(recentlyDeletedTransaction)
                    .addOnSuccessListener {
                        getdata()
                    }
                    .addOnFailureListener {
                        Toast.makeText(rootView.context, "Failed to Restored Transaction ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    private fun deleteTransactions(transactionId : String){
        firestore.collection("Transactions")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("TransactionList")
            .document(transactionId)
            .delete()
            .addOnSuccessListener {
                getdata()
            }
            .addOnFailureListener {
                Toast.makeText(rootView.context, "Failed to Delete Transaction ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}