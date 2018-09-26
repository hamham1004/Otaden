package foo.bar.otaden;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import foo.bar.otaden.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

	// ログ出力用タグ名
	private final String TAG = "MainActivity";

    // 非同期で相談したい人のDNを取得するためのタスクオブジェクト生成
	private MainActivity activity = this;

    // 一言メッセージ表示用
	private TextView shortMessage;

	HashMap<String, String> resultMap;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate start");
        super.onCreate(savedInstanceState);

        // 画面上部のタイトルを無しにする
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);

		// 広告を表示する
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		// 既に保存されているカテゴリーを取得するため、プリファレンスを開く
		final SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_FILE_NAME, MODE_PRIVATE);
		
		// 保存されている一言メッセージをプリファレンスから取得してセット
        shortMessage = (TextView)findViewById(R.id.message);
        shortMessage.setText(preferences.getString("COMMENT", "").toString());

        // 一言メッセージ編集ボタンのリスナー設定
        Button messageEditButton = (Button)findViewById(R.id.message_edit_button);
        messageEditButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
    	        Log.i(TAG, "messageEditButtonOnClickListener start");
    	        showTextDialog(shortMessage.getText().toString());
    		}
        });

		// カテゴリーをサーバーから取得する
        resultMap = new HashMap<String, String>();
        getCategory(resultMap);
        Log.d(TAG, "GetCategoryTask return, category01=<" + resultMap.get("category01") + ">, category02=<" + resultMap.get("category02") + ">, category03=<" + resultMap.get("category03") + ">, category04=<" + resultMap.get("category04") + ">, category05=<" + resultMap.get("category05") + ">, category06=<" + resultMap.get("category06") + ">, category07=<" + resultMap.get("category07") + ">, category08=<" + resultMap.get("category08") + ">, category09=<" + resultMap.get("category09") + ">");
        
        if (resultMap.containsKey("category01") == false) {
			// http通信がエラー
			Handler handler = new Handler();
			handler.post(new Runnable() {
				
				public void run() {
					AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
					dialog.setTitle("エラー");
					dialog.setMessage("サーバーに接続できません。インターネット接続が有効でないか、システムがメンテナンス中の場合があります。");
					dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// OKボタンを押された時の処理は特になし
//							activity.finish();
							finish();
						}
					});
					dialog.show();
				}
			});
		} else {
	        // カテゴリー選択Spinnerにサーバーから取得したカテゴリーをセットする
	        Spinner memberSpinner = (Spinner)findViewById(R.id.member_spinner);
	        setCategory(memberSpinner, resultMap);

	        // プリファレンスに保存されているカテゴリーを取得
	        int savedCategory = preferences.getInt("MEMBER", 0);
	        Log.d(TAG, "preferences.getInt(MEMBER)=" + preferences.getInt("MEMBER", 0));
	        
	        // プリファレンスに保存されているカテゴリーをスピナーにセットする、保存されている値は、画面表示位置＋1になるので、1を引いてセット
	        // 保存されているカテゴリーがもも電(9)の時は、サーバーから取得したカテゴリー数-1に変更してSpinnerにセットする
	        if (savedCategory == AppConstants.CATEGORY_MOMO) {
	        	memberSpinner.setSelection( countOfCategory()-1 );
			} else if ( (preferences.getInt("MEMBER", 0)) <= countOfCategory()) {
	        	memberSpinner.setSelection(preferences.getInt("MEMBER", 0) - 1);
			}

	        // Spinnerのリスナー設定
	        memberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

	        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
	        		// 選択されたカテゴリーを取得
	        		String selectedMmeber = (String) parent.getSelectedItem();

	        		// 一番下を選んだ時はもも電なので、プリファレンスに保存する値は9になるようにpositionの値を8に変える
	        		if ( position == (countOfCategory()-1) ) {
						position = 8;
					}
	        		Log.d(TAG, "selected member = " + selectedMmeber + ",position = " + position);
	        		
	        		// カテゴリーをプリファレンスに保存する
	        		SharedPreferences.Editor editor = preferences.edit();
	        		editor.putInt("MEMBER", position + 1);
	        		editor.commit();
	        	}

				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
	        
	        // 相談したいボタンのリスナー設定
	        Button callerButton = (Button)findViewById(R.id.caller_button);
	        callerButton.setOnClickListener(new OnClickListener() {

	        	public void onClick(View v) {
	    	        Log.i(TAG, "CallerButtonOnClickListener start");
	    	        // 相談したい人用の電話番号を取得し、次画面に進む
	    	    	getDnAndStartActivity();
	    		}

	        });

	        // twitterのツイートボタンを表示する
	        WebView webView = (WebView)findViewById(R.id.webview1);
	        webView.setBackgroundColor(Color.TRANSPARENT);
	        WebSettings settings = webView.getSettings();
	        settings.setJavaScriptEnabled(true);
	        webView.loadUrl(AppConstants.TWEET_BUTTON_URL);
	        webView.setHorizontalScrollBarEnabled(false);
	        webView.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					return(event.getAction() == MotionEvent.ACTION_MOVE);
				}
	        });

		}
    }

	/**
	 * 一言メッセージ入力用のポップアップを開き、入力された値を画面上のTextViewにセットする
	 * @param originalMessage ポップアップが開いた時に表示する元の一言メッセージ
	 */
	protected void showTextDialog(String originalMessage) {
		final EditText editText = new EditText(activity);
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setTitle("一言メッセージ編集");
		dialog.setMessage("30文字以内で入力してください");
		dialog.setCancelable(false);
		editText.setText(originalMessage);
		editText.setSelection(editText.length());
		dialog.setView(editText);
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				StringBuffer newMessage = new StringBuffer(editText.getText().toString());
				Log.d(TAG, "newMessage1 = " + newMessage.toString());

				// 長さは最大30文字、それ以上は切り捨て
				if (newMessage.length() > 30) {
					newMessage.setLength(30);
				}
				Log.d(TAG, "newMessage2 = " + newMessage.toString());

				// 含まれていてはいけない文字列を削除する、ダブルクォーテーション、シングルクォーテーション、改行
				List<String> ngStr = Arrays.asList("\"", "'", "\n");
				newMessage = checkString(newMessage, ngStr);
				Log.d(TAG, "newMessage3 = " + newMessage.toString());

				// 画面のTextViewに入力された値をセット
				shortMessage.setText(newMessage.toString());
				// プリファレンスに一言メッセージを保存する
				saveShortMessage(newMessage.toString());
			}
		});
		dialog.setNegativeButton("Cancel", null);
		dialog.show();
	}

	/**
	 * 含まれていてはいけない文字列を削除する
	 * @param str チェック対象の文字列
	 * @param ngStringList 含まれていてはいけない文字のリスト
	 * @return 含まれていてはいけない文字が削除された後の文字列
	 */
	private StringBuffer checkString(StringBuffer str, List<String> ngStringList) {
		for (String ngString : ngStringList) {
			int i;
			while ((i = str.indexOf(ngString)) > -1) {
				str.deleteCharAt(i);
			}
		}
		return str;
	}
	
	/**
	 * 自分用の電話番号を取得し、次画面のActivityを呼び出す
	 */
	private void getDnAndStartActivity() {
		// プリファレンスから保存されているカテゴリーを取得する
		SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_FILE_NAME, MODE_PRIVATE);
        int savedMember = preferences.getInt("MEMBER", 1);
        
        // GetDnTaskからの戻り値、キーdnに取得した電話番号が入る、処理が終わるとキーendFlagに1が入る
        HashMap<String, String> resultMap = new HashMap<String, String>();

        // 電話番号取得処理を呼び出す、選択しているカテゴリーで番号が決まる、コメントが書き込まれる
        // カテゴリーがももクロの時は、専用のURL、savedMember(pass)をセット
        if (savedMember == AppConstants.CATEGORY_MOMO) {
            new GetDnTask(activity, resultMap).execute(AppConstants.GET_CALLER_DN_URL_MOMO, 9, shortMessage.getText().toString());
        } else {
            new GetDnTask(activity, resultMap).execute(AppConstants.GET_CALLER_DN_URL, savedMember, shortMessage.getText().toString());
        }

        // 非同期呼び出しなので、終了フラグが立つまで待つ
        while (resultMap.containsKey("endFlag") == false) {
			SystemClock.sleep(1000);
		}
        Log.i(TAG, "GetCallerDnTask return, dn=<" + resultMap.get("dn") + ">");

		if (resultMap.containsKey("dn") == false) {
			// http通信がエラー
			AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
			dialog.setTitle("エラー");
			dialog.setMessage("サーバーに接続できません。インターネット接続が有効でないか、システムがメンテナンス中の場合があります。");
			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// OKボタンを押された時の処理は特になし
				}
			});
			dialog.show();
		} else if (resultMap.get("dn").equals("NG")) {
			// 空き番号がない
			AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
			dialog.setTitle("エラー");
			dialog.setMessage("混雑しているため、ただいまご利用になれません。しばらく待ってからご利用ください。");
			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// OKボタンを押された時の処理は特になし
				}
			});
			dialog.show();
		} else {
			// 正常に電話番号が取得できたので、通話するアクティビティを呼び出す
			Intent intent = new Intent(MainActivity.this, CallActivity.class);
			// 自分の電話番号
			intent.putExtra("thisDN", resultMap.get("dn"));
			// 相談したい人
			intent.putExtra("isCaller", true);  // ホントは不要だが受け側のCallActivityで参照している箇所が残っているので残してある
			startActivity(intent);
		}
	}

	/**
	 * 一言メッセージをプリファレンスに保存する
	 */
	private void saveShortMessage(String message) {
		SharedPreferences preferences = getSharedPreferences(AppConstants.PREFERENCE_FILE_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("COMMENT", message);
		editor.commit();
	}

	/**
	 * カテゴリーをサーバーから取得する
	 * @param resultMap サーバーから取得したカテゴリーのMAP
	 */
	private void getCategory(HashMap<String, String> resultMap) {
        // カテゴリーを取得する
        new GetCategoryTask(resultMap).execute(AppConstants.GET_CATEGORY_URL);

        // 非同期呼び出しなので、終了フラグが立つまで待つ
        while (resultMap.containsKey("endFlag") == false) {
			SystemClock.sleep(1000);
		}
	}

	/**
	 * サーバーから取得したカテゴリーをスピナーにセットする
	 * @param memberSpinner スピナー
	 * @param resultMap 取得したカテゴリーの入ったMAP
	 */
	private void setCategory(Spinner memberSpinner, HashMap<String, String> resultMap) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        // カテゴリーが空文字列の場合は読み飛ばす
        if (resultMap.containsKey("category01") == true) {
            if (! resultMap.get("category01").equals("")) {
            	adapter.add(resultMap.get("category01"));
    		}
            if (! resultMap.get("category02").equals("")) {
            	adapter.add(resultMap.get("category02"));
    		}
            if (! resultMap.get("category03").equals("")) {
            	adapter.add(resultMap.get("category03"));
    		}
            if (! resultMap.get("category04").equals("")) {
            	adapter.add(resultMap.get("category04"));
    		}
            if (! resultMap.get("category05").equals("")) {
            	adapter.add(resultMap.get("category05"));
    		}
            if (! resultMap.get("category06").equals("")) {
            	adapter.add(resultMap.get("category06"));
    		}
            if (! resultMap.get("category07").equals("")) {
            	adapter.add(resultMap.get("category07"));
    		}
            if (! resultMap.get("category08").equals("")) {
            	adapter.add(resultMap.get("category08"));
    		}
            if (! resultMap.get("category09").equals("")) {
            	adapter.add(resultMap.get("category09"));
    		}
        }
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberSpinner.setAdapter(adapter);
	}

	/**
	 * サーバーから取得したカテゴリーの中で、空文字列ではないカテゴリーの数を数える
	 * @return 有効なカテゴリー数
	 */
	private int countOfCategory() {
		int count = 0;
		for (int i = 1; i < 10; i++) {
			String keyName = "category0" + String.valueOf(i);
	        if (! resultMap.get(keyName).equals("")) {
	        	count += 1;
			}
		}
		return count;
	}
	
	@Override
    public void onResume() {
        Log.i(TAG, "onResume start");
        super.onResume();
	
        // ログに記録されたカテゴリーを集計したポイントを取得して、表示する
        new GetPointTask(activity, resultMap).execute(AppConstants.GET_POINT_URL);
        
	}
}
