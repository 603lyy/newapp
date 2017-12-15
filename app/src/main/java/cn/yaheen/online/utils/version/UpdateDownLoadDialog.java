
package cn.yaheen.online.utils.version;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.yaheen.online.R;

/**
 * @author Bin
 * @date 2015-10-20
 * @Des 下载进度框
 */
public class UpdateDownLoadDialog extends Dialog {

    private View btn_update_install;
    private TextView dialog_content_msg;
    private ProgressBar pb_update_downloading;

    private OnCancelClickListener onCancelClickListener;

    private int max;

    public UpdateDownLoadDialog(Context context) {
        super(context);
        setContentView(R.layout.update_downloading_dialog);
        btn_update_install = findViewById(R.id.btn_update_install);
        dialog_content_msg = (TextView) findViewById(R.id.dialog_content_msg);
        pb_update_downloading = (ProgressBar) findViewById(R.id.pb_update_downloading);

        setCancelable(false);
        btn_update_install.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onCancelClickListener != null) {
                    onCancelClickListener.onInstallClick(v);
                }
            }
        });
    }

    public void setProgressMax(int max) {
        this.max = max;
        if (pb_update_downloading != null) {
            pb_update_downloading.setMax(max);
        }
    }

    public void setProgress(int progress) {
        if (pb_update_downloading != null) {
            pb_update_downloading.setProgress(progress);
        }
    }

    public void setDialogContentMsg(String msg) {
        if (dialog_content_msg != null) {
            dialog_content_msg.setText(msg);
        }
    }

    public void setOnCancelClickListener(OnCancelClickListener cl) {
        onCancelClickListener = cl;
    }

    public interface OnCancelClickListener {
        public void onInstallClick(View v);
    }

}
