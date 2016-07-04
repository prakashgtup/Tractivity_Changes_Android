package com.trac.android.tractivity.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

/**
 * 
 * Generic class to show pop up window with the corresponding message
 *
 */

public class AlertNotification
{
  public void showAlertPopup(Context context, String title ,String message, final EditText editText)
  {
    AlertDialog.Builder alertPopUpBuilder = new AlertDialog.Builder(context);
    alertPopUpBuilder.setTitle(title);
    alertPopUpBuilder.setMessage(message);
    alertPopUpBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener()
    {
      @Override
	public void onClick(DialogInterface dialoginterface, int position)
      {
        dialoginterface.dismiss();
        if (editText != null) {
          editText.requestFocus();
        }
      }
    });
    alertPopUpBuilder.show();
  }
}

