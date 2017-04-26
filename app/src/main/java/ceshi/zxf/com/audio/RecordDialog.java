package ceshi.zxf.com.audio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import ceshi.zxf.com.cheshi.R;
import ceshi.zxf.com.utils.DataUtil;
import ceshi.zxf.com.audio.AudioRecordView;

public class RecordDialog {

	private String title;
	private Context context;
	private Handler handler;
	private Item item;
	public AudioRecordView audioView;
	public boolean isshowing = false;
	public OnKeyListener dialoglistener;
	public AlertDialog alert;

	public boolean isIsshowing() {
		return isshowing;
	}

	public void setIsshowing(boolean isshowing) {
		this.isshowing = isshowing;
	}

	public RecordDialog(Context context, String title, Handler handler, AudioRecordView audioView, OnKeyListener dialoglistener) {
		this.context = context;
		this.title = title;
		this.handler = handler;
		this.audioView = audioView;
		this.dialoglistener = dialoglistener;
	}

	public class Item {
		TextView edittextdialog_title;
		TextView edittextdialog_close;
		LinearLayout luyinlayout;
	}

	public void showDialog() {
		alert = new AlertDialog.Builder(context).create();
		alert.setCanceledOnTouchOutside(false);
		alert.setOnKeyListener(dialoglistener);
		item = new Item();
		View view = LayoutInflater.from(context).inflate(R.layout.draw_recorddialog, null);
		item.edittextdialog_title = (TextView) view.findViewById(R.id.edittextdialog_title);

		item.edittextdialog_close = (TextView) view.findViewById(R.id.edittextdialog_close);
		item.luyinlayout = (LinearLayout) view.findViewById(R.id.luyinlayout);
		if (audioView.getParent() != null) {
			((LinearLayout) audioView.getParent()).removeView(audioView);
		}

		Handler recordFinishedHandler = new Handler() {
			public void handleMessage(Message msg) {
				int pos = msg.what;
				if (pos == 1) {
					alert.cancel();
					if (audioView.isIsstop()) {// 录音结束
						Message message = new Message();
						Bundle bd = new Bundle();
						bd.putString("type", "1");
						if (audioView.getUrl() != null) {
							bd.putString("audioname", audioView.getUrl().substring(audioView.getUrl().lastIndexOf("/") + 1));
						}
						message.what=DataUtil.AUDIO_RECORD;//新加
						message.setData(bd);
						handler.sendMessage(message);
					}
				}
			}
		};

		audioView.addhandler(recordFinishedHandler);
		item.luyinlayout.addView(audioView);
		item.edittextdialog_title.setText(title);

		item.edittextdialog_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				audioView.jieshu();
				/*if (audioView.isIsstop()) {// 录音结束

				} else {
					Message msg = new Message();
					Bundle bd = new Bundle();
					bd.putString("type", "2");
					msg.what = DataUtil.AUDIO_RECORD;//新加
					msg.setData(bd);
					handler.sendMessage(msg);
				}
				isshowing = false;
				alert.cancel();*/
			}
		});

		alert.setCanceledOnTouchOutside(false);
		alert.setView(view, 0, 0, 0, 0);
		alert.show();
	}
}
