package cn.cz.tetris.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;

public class ViewBox {
    private Activity mActivity;
    private AlertDialog mAlertDialog;

    public ViewBox(Activity activity) {
        mActivity = activity;
    }

    public void showDialog(String message, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener cancelListener) {
        showDialog(null, message, onClickListener, cancelListener);
    }

    public void showDialog(String title, String message, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener cancelListener) {
        dismissDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        mAlertDialog = builder
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, cancelListener)
                .setPositiveButton(android.R.string.ok, onClickListener)
                .create();
        mAlertDialog.show();
    }

    public void showRadioDialog(String title, final IRadioInput iRadioInput, int itemSelected, final String... selects) {
        dismissDialog();
        final int[] id = new int[1];
        id[0] = itemSelected;
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(title);
        builder.setSingleChoiceItems(selects, itemSelected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                id[0] = which;
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                iRadioInput.onRadioInput(selects[id[0]]);
            }
        });
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    private void dismissDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    public interface IRadioInput {
        void onRadioInput(String name);
    }
}
