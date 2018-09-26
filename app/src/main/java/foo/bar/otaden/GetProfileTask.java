/**
 * 
 */
package foo.bar.otaden;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * 非同期で相手の推しメンを取得する
 * executeの引数1個目がURL、2個めが推しメンを取得したい相手のDN
 */
public class GetProfileTask extends AsyncTask<Object, Void, String> {

	// ログ出力用タグ名
	private final String TAG = "GetProfileTask";

	// 呼び出し元のアクティビティ取得用（MainActivity）
	private CallActivity activity;

    // コンストラクタ
	public GetProfileTask(CallActivity activity) {
        super();
        this.activity = activity;
	}

	@Override
	protected String doInBackground(Object... params) {
		Log.i(TAG, "doInBackground stard");

		String selectedMember = "";
		
		// POSTリクエスト用のインスタンス生成
		HttpPost httpPost = new HttpPost((String) params[0]);

		// POST用のパラメータ
		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("dn", (String)params[1]));
		Log.d(TAG, "---- dn = " + (String)params[1]);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(paramList,HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "UrlEncodedFormEntity error (UnsupportedEncodingException)", e);
		}

		// httpのタイムアウトパラメータの設定
		HttpParams httpParams = new BasicHttpParams();
		// コネクション確率のタイムアウト
		httpParams.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, AppConstants.CONNECTION_TIMEOUT);
		// データ待ちのタイムアウト
		httpParams.setIntParameter(AllClientPNames.SO_TIMEOUT, AppConstants.SO_TIMEOUT);

		// クライアントオブジェクト生成
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient(httpParams);

        // HTTP通信しレスポンス情報取得
		try {
			HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
	        // HTTPレスポンスが正常な場合
	        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	            // HTTPレスポンスのコンテンツを取得
	            HttpEntity entity = httpResponse.getEntity();
	            JSONObject json = new JSONObject(EntityUtils.toString(entity));
	            selectedMember = json.getString("comment");
	        }
		} catch (Exception e) {
			Log.e(TAG, "http error", e);
		} finally {
			defaultHttpClient.getConnectionManager().shutdown();
		}

		// 通常はisUseが0であることはありえないが、何かのタイミングで0だった場合に次のonPostExectuteでIndexOutOfBoundsになるので強制的に1に変えておく
		if (selectedMember == null) {
			selectedMember = "";
		}

		Log.i(TAG, "doInBackground end, dn=<" + params[1] + ">, result=<" + selectedMember + ">");
		return selectedMember;
	}

	protected void onPostExecute(String result) {
		Log.i(TAG, "onPostExecute start");
		
		// 推しメンをセット
		activity.memberText.setText(result);

    }

}
