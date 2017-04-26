package ceshi.zxf.com.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import ceshi.zxf.com.cheshi.R;
import ceshi.zxf.com.utils.FilePaths;
import ceshi.zxf.com.utils.MyDateAndTime;

public class AudioRecordView extends LinearLayout {

	public interface addMyTextChange {
		void change(String time);
	}

	private addMyTextChange listener;
	private TextView minView;
	private TextView secondsView;
	private TextView mpauseRecord;
	private TextView moverRecord;
	private LinearLayout luyinlayout;
	public Boolean isFirstClick = false;
	private int screenWidth;
	public Clock clock;
	private File recAudioFile;
	private MediaRecorder mMediaRecorder;
	private String recordPath;
	private String recordName;
	private boolean inThePause = false;
	private boolean isPause;
	private boolean misRecording = false;
	private String url = null; // 生成的录音文件路径
	public boolean isstop = false;
	private Handler handler = null;
	public int isalert = 0; //是否关闭或返回

	/** 记录需要合成的几段amr语音文件 **/
	private ArrayList<String> list = new ArrayList<String>();

	public void addhandler(Handler handler) {
		this.handler = handler;
	}

	public void cancleHandler() {
		this.handler = null;
	}

	public void addListener(addMyTextChange listener) {
		this.listener = listener;
	}

	public AudioRecordView(Context context, String filepath, boolean isFirstClick) {

		super(context);
		recordPath = filepath;
		this.isFirstClick = isFirstClick;

		LayoutInflater.from(context).inflate(R.layout.draw_audio_record, this, true);
		int screenW = 0;
		int screeH = 0;
		WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
		if (wm != null) {
			screenW = wm.getDefaultDisplay().getWidth();
			screeH = wm.getDefaultDisplay().getHeight();
		}
		screenWidth = screeH < screenW ? screeH : screenW;
		initAudioRecord();
	}

	public String getUrl() {
		return url;
	}

	private void initAudioRecord() {
		minView = (TextView) findViewById(R.id.AudioRecord_MinView);
		secondsView = (TextView) findViewById(R.id.AudioRecord_SecondsView);
		mpauseRecord = (TextView) findViewById(R.id.AudioRecord_PauseRecord);
		// mpauseRecord.setLayoutParams(new LayoutParams(screenWidth / 9,
		// screenWidth / 9));
		luyinlayout = (LinearLayout) findViewById(R.id.luyin);

		mpauseRecord.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isFirstClick) {
					isFirstClick = false;
					mpauseRecord.setBackgroundResource(R.drawable.pause);
					startClock(true);
					startRecorder();
				} else {
					pauseRecord();
				}
			}
		});
		moverRecord = (TextView) findViewById(R.id.AudioRecord_OverRecord);
		// moverRecord.setLayoutParams(new LayoutParams(screenWidth / 9,
		// screenWidth / 9));
		moverRecord.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isstop = true;
				stopRecorder(true);
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		});

		secondsView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String str1 = minView.getText().toString();
				String str2 = secondsView.getText().toString();
				if (listener != null) {
					listener.change(str1 + ":" + str2);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void jieshu(){
		isalert = 1;//自己定义，关闭或返回
		isstop = true;
		stopRecorder(true);
		Message message = new Message();
		message.what = 1;
		handler.sendMessage(message);
	}


	public void startClock(boolean flag) {
		if (flag) {
			minView.setText("00");
			secondsView.setText("00");
		}
		if (null == clock) {
			clock = new Clock();
		}
		clock.count(minView, secondsView);
	}

	private void endClock() {
		clock.count(minView, secondsView);
	}

	public void startRecorder() {
		String recordtime = MyDateAndTime.getStringToday();
		recordName = recordtime + FilePaths.DRAWACTIVITY_LUYIN_SUFFIX;

		File recordpathfile = new File(recordPath);
		if (!recordpathfile.exists()) {
			recordpathfile.mkdirs();
		}
		recAudioFile = new File(recordpathfile, recordName);

		mMediaRecorder = new MediaRecorder();
		if (recAudioFile.exists()) {
			recAudioFile.delete();
		}
		
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mMediaRecorder.setOutputFile(recAudioFile.getAbsolutePath());
		try {
			mMediaRecorder.prepare();

			mMediaRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void pauseRecord() {
		try {

			isPause = true;

			if (inThePause) {
				startRecorder();
				inThePause = false;

				mpauseRecord.setBackgroundResource(R.drawable.pause);
				startClock(false);
			}
			// 正在录音，点击暂停,现在录音状态为暂停
			else {
				// 当前正在录音的文件名，全程
				list.add(recAudioFile.getPath());
				inThePause = true;

				if (recAudioFile != null) {
					if (mMediaRecorder != null) {
						mMediaRecorder.stop();
						mMediaRecorder.release();
						mMediaRecorder = null;
					}
				}
				mpauseRecord.setBackgroundResource(R.drawable.luyinrecord);
				// 计时暂停
				endClock();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String stopRecorder(Boolean save) {
		try {
			if (isstop) {
				if (!isFirstClick) {
					isFirstClick = false;
					if (misRecording) {
						mpauseRecord.setBackgroundResource(R.drawable.luyinrecord);
						if (isPause) {
							// 在暂停状态按下结束键,处理list就可以了
							if (inThePause) {
								url = getInputCollection(list, false);
							}
							// 在正在录音时，处理list里面的和正在录音的语音
							else {
								endClock();
								list.add(recAudioFile.getPath());
								if (recAudioFile != null) {
									try {

										if (mMediaRecorder != null) {
											mMediaRecorder.stop();
											mMediaRecorder.release();
											mMediaRecorder = null;
										}
									} catch (IllegalStateException e) {
										e.printStackTrace();
									}
								}
								url = getInputCollection(list, true);
							}
							// 还原标志位
							isPause = false;
							inThePause = false;

							hide();
							misRecording = false;
							return url;
						} else {
							endClock();
							if (recAudioFile != null) {
								try {
									if (mMediaRecorder != null) {
										mMediaRecorder.stop();
										mMediaRecorder.release();
										mMediaRecorder = null;
									}
								} catch (IllegalStateException e) {
									e.printStackTrace();
								}
							}
							url = recordPath + "/" + recordName;
							misRecording = false;
							hide();
							return url;
						}
					}
				} else {
					misRecording = false;
					hide();
				}
			}
		} catch (Exception e) {
			misRecording = false;
			hide();
		}
		return url;
	}

	/**
	 * @param isAddLastRecord
	 *            是否需要添加list之外的最新录音，一起合并
	 * @return 将合并的流用字符保存
	 */
	public String getInputCollection(List list, boolean isAddLastRecord) {
		// 创建音频文件,合并的文件放这里
		String recordtime = MyDateAndTime.getStringToday();
		String newName = recordtime + FilePaths.DRAWACTIVITY_LUYIN_SUFFIX;
		File file1 = new File(recordPath, newName);
		FileOutputStream fileOutputStream = null;
		if (!file1.exists()) {
			try {
				file1.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fileOutputStream = new FileOutputStream(file1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// list里面为暂停录音 所产生的 几段录音文件的名字，中间几段文件的减去前面的6个字节头文件
		for (int i = 0; i < list.size(); i++) {
//			System.out.println("当前总文件长度为：" + file1.length());
			File file = new File((String) list.get(i));
//			System.out.println("第" + i + "段文件的长度：" + file.length());
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				byte[] myByte = new byte[fileInputStream.available()];
//				System.out.println(fileInputStream.available() + "fileInputStream.available()");
				// 文件长度
				int length = myByte.length;
				// 头文件
				if (i == 0) {
					while (fileInputStream.read(myByte) != -1) {
						fileOutputStream.write(myByte, 0, length);
					}
				}
				// 之后的文件，去掉头文件就可以了
				else {
					while (fileInputStream.read(myByte) != -1) {
						fileOutputStream.write(myByte, 6, length - 6);
					}
				}
				fileOutputStream.flush();
				fileInputStream.close();
//				System.out.println("合成文件长度：" + file1.length());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 结束后关闭流
		try {
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 合成一个文件后，删除之前暂停录音所保存的零碎合成文件
		deleteListRecord(isAddLastRecord);
		list.clear();
		return file1.getPath();
	}

	private void deleteListRecord(boolean isAddLastRecord) {
		for (int i = 0; i < list.size(); i++) {
			File file = new File((String) list.get(i));
			if (file.exists()) {
				file.delete();
			}
		}
		// 正在暂停后，继续录音的这一段音频文件
		if (isAddLastRecord) {
			recAudioFile.delete();
		}
	}

	public void hide() {
		this.luyinlayout.setVisibility(View.INVISIBLE);
	}

	public void showRecord() {
		isFirstClick = true;
		misRecording = true;
	}

	public boolean isIsstop() {
		return isstop;
	}

	public void setIsstop(boolean isstop) {
		this.isstop = isstop;
	}

	public class Clock {

		private static final int STOP = 0;

		public int v;

		public boolean isStarted = false;
		clockThread threadS;
		sechandler sech;
		minhandler minh;

		int sec = 0;
		int min = 0;

		public NumberFormat nf;

		public TextView myMinView;
		public TextView mySecondsView;

		public Clock() {
			nf = NumberFormat.getInstance();

			nf.setMinimumIntegerDigits(2); // The minimum Digits required is 2
			nf.setMaximumIntegerDigits(2); // The maximum Digits required is 2

			sech = new sechandler();
			minh = new minhandler();
		}

		public void count(TextView minView, TextView secondsView) {
			myMinView = minView;
			mySecondsView = secondsView;
			if (!isStarted) {
				sec = Integer.valueOf(mySecondsView.getText().toString());
				min = Integer.valueOf(myMinView.getText().toString());
				isStarted = true;
				if (v == STOP) {
					threadS = new clockThread();
					threadS.start();
				}
			} else {
				isStarted = false;
				if (v == STOP) {
					if (threadS.isAlive())
						threadS.interrupt();
				}
			}
		}

		private class clockThread extends Thread {
			@Override
			public void run() {
				while (true) {
					sec++;
					sech.sendEmptyMessage(MAX_PRIORITY);
					if (sec == 60) {
						min++;
						sec = 0;
						minh.sendEmptyMessage(MAX_PRIORITY);
					}
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		}

		private class sechandler extends Handler {
			@Override
			public void handleMessage(Message msg) {
				mySecondsView.setText("" + nf.format(sec));
			}
		}

		private class minhandler extends Handler {
			@Override
			public void handleMessage(Message msg) {
				myMinView.setText("" + nf.format(min));
			}
		}
	}
}
