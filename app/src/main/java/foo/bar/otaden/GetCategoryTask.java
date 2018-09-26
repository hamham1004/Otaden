package foo.bar.otaden;

import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * 非同期でお知らせをWebサーバから取得するためのタスク
 */
public class GetCategoryTask extends AsyncTask<Object, Void, Void> {

	// ログ出力用タグ名
	private final String TAG = "GetCategoryTask";

	// 呼び出し元への戻り値、キーinfoに取得したお知らせが入る、処理が終わるとキーendFlagに1が入る
	private HashMap<String, String> resultMap;

    // コンストラクタ
	public GetCategoryTask(HashMap<String, String> resultMap) {
        super();
		this.resultMap = resultMap;
	}

	@Override
	protected Void doInBackground(Object... params) {
		Log.i(TAG, "doInBackground start");

		// GET通信用オブジェクト生成
		HttpUriRequest request = new HttpGet((String) params[0]);

		// httpのタイムアウトパラメータの設定
		HttpParams httpParams = new BasicHttpParams();
		// コネクション確率のタイムアウト
		httpParams.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, AppConstants.CONNECTION_TIMEOUT);
		// データ待ちのタイムアウト
		httpParams.setIntParameter(AllClientPNames.SO_TIMEOUT, AppConstants.SO_TIMEOUT);

		// クライアントインスタンス作成
		DefaultHttpClient client = new DefaultHttpClient(httpParams);

        // HTTP通信しレスポンス情報取得
		try {
			HttpResponse response = client.execute(request);
	        // HTTPレスポンスが正常な場合
	        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	            // HTTPレスポンスのコンテンツを取得
	            HttpEntity entity = response.getEntity();
	            JSONObject json = new JSONObject(EntityUtils.toString(entity));
	            resultMap.put("category01", json.getString("category01"));
	            resultMap.put("category02", json.getString("category02"));
	            resultMap.put("category03", json.getString("category03"));
	            resultMap.put("category04", json.getString("category04"));
	            resultMap.put("category05", json.getString("category05"));
	            resultMap.put("category06", json.getString("category06"));
	            resultMap.put("category07", json.getString("category07"));
	            resultMap.put("category08", json.getString("category08"));
	            resultMap.put("category09", json.getString("category09"));
	        }
		} catch (Exception e) {
			Log.e(TAG, "http error", e);
		} finally {
			client.getConnectionManager().shutdown();
		}

		resultMap.put("endFlag", "1");
		return null;
	}

}