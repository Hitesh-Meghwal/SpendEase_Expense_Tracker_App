package com.example.spendease.Widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import com.example.spendease.R

class ExpenseWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if(intent?.action == "com.example.spendease.UPDATE_WIDGET"){
            val appwidgetManger = AppWidgetManager.getInstance(context)
            val appWidgetIds = appwidgetManger.getAppWidgetIds(
                ComponentName(context!!,ExpenseWidget::class.java)
            )
            onUpdate(context,appwidgetManger,appWidgetIds)
        }
    }

}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

    val userDetails = context.getSharedPreferences("UserDetails", AppCompatActivity.MODE_PRIVATE)
    val getMonthlyBudget = userDetails.getString("MonthlyBudget","")
    val getCurrency = userDetails.getString("Currency","")
    val getExpense = userDetails.getString("totalExpense","")
    val date = userDetails.getString("date","")
    
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.expense_widget)
    if (!getMonthlyBudget.isNullOrEmpty() || !getCurrency.isNullOrEmpty() || !getExpense.isNullOrEmpty()){
        views.setTextViewText(R.id.budgettv, "$getCurrency $getMonthlyBudget")
        views.setTextViewText(R.id.expensetv, "$getCurrency $getExpense")
        views.setTextViewText(R.id.datetv, date)
    }
    else {
        views.setTextViewText(R.id.budgettv, "")
        views.setTextViewText(R.id.expensetv,"")
    }
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}