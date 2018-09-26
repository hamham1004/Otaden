package foo.bar.otaden;

/**
 * アプリ全体用定数の設定
 */
public final class AppConstants {

    private AppConstants() {
    }
    // SIPサーバーのIPアドレス（★★本番★★）
    public static final String SIPSERVER_IP = "ec2-54-248-xxx-xxx.ap-northeast-1.compute.amazonaws.com";
    // SIPサーバーのIPアドレス（テスト用）
//    public static final String SIPSERVER_IP = "ec2-54-248-xxx-xxx.ap-northeast-1.compute.amazonaws.com";

    // お知らせ取得用URL
    public static final String GET_INFORMATION_URL = "http://" + SIPSERVER_IP + "/otaden/info_otaden_1.0.php";

    // Caller（相談したい人）のDN取得用URL
    public static final String GET_CALLER_DN_URL = "http://" + SIPSERVER_IP + "/otaden/getdn_otaden.php";

    // Caller（相談したい人）のDN取得用URL、もも電互換用
    public static final String GET_CALLER_DN_URL_MOMO = "http://" + SIPSERVER_IP + "/otaden/getdn_otaden_momo.php";

    // Caller（相談したい人）に割り当てられたDNの解放用URL
    public static final String RELEASE_CALLER_DN_URL = "http://" + SIPSERVER_IP + "/otaden/releasedn_otaden.php";

    // Caller（相談したい人）に割り当てられたDNの解放用URL、もも電互換用
    public static final String RELEASE_CALLER_DN_URL_MOMO = "http://" + SIPSERVER_IP + "/otaden/releasedn_otaden_momo.php";

    // 相手の一言メッセージ（コメント）を取得するURL
    public static final String GET_PROFILE_URL = "http://" + SIPSERVER_IP + "/otaden/getprofile_otaden.php";

    // 相手の一言メッセージ（コメント）を取得するURL、もも電互換用
    public static final String GET_PROFILE_URL_MOMO = "http://" + SIPSERVER_IP + "/otaden/getprofile_otaden_momo.php";

    // 自分の一言メッセージ（コメント）を5100番などの共通番号に強制的に書き込むURL
    public static final String PUT_PROFILE_URL = "http://" + SIPSERVER_IP + "/otaden/putprofile_otaden.php";

    // 自分の一言メッセージ（コメント）を5100番などの共通番号に強制的に書き込むURL、もも電互換用
    public static final String PUT_PROFILE_URL_MOMO = "http://" + SIPSERVER_IP + "/otaden/putprofile_otaden_momo.php";

    // ツイートボタンを表示するためのURL
    public static final String TWEET_BUTTON_URL = "http://" + SIPSERVER_IP + "/otaden/otaden_tw.htm";

    // 通話が始まったタイミングでログを書き込むURL
    public static final String INSERT_LOG_URL = "http://" + SIPSERVER_IP + "/otaden/insert_log_otaden.php";

    // カテゴリーランキングを取得するURL
    public static final String GET_POINT_URL = "http://" + SIPSERVER_IP + "/otaden/getpoint_otaden.php";

    // カテゴリー一覧を取得するURL
    public static final String GET_CATEGORY_URL = "http://" + SIPSERVER_IP + "/otaden/getcategory_otaden.php";

    // 相談したい人がかける先の電話番号
    public static final int CALLER_TARGET_DN = 5000;

    // HTTP通信のコネクションタイムアウト（ミリ秒）
    public static final int CONNECTION_TIMEOUT = 10000;

    // HTTP通信のデータ待ちタイムアウト（ミリ秒）
    public static final int SO_TIMEOUT = 10000;

    // 電話をかけて、通話が始まるまで待ち続ける秒数（秒）
    public static final int CALL_WAIT_TIMER = 120;

    // 通話が終了し、CallActivityからMainActivityに戻るまでの時間（ミリ秒）
    public static final int AFTER_CALL_TIMER = 3000;

    // 通話を続けられる最大時間（秒）
    public static final int CALL_END_TIMER = 300;

    // 通話残り時間がこの時間になったら1回目のアラームを鳴らす（秒）
	public static final int FIRST_ALARM = 60;

    // 通話残り時間がこの時間になったら2回目のアラームを鳴らす（秒）
	public static final int SECOND_ALARM = 10;

    // 延長ボタンを押した時に延長される時間（秒）
	public static final int CALL_END_EXTEND_TIME = 180;

    // 選択したカテゴリー、一言メッセージを保存するプリファレンスのファイル名
    public static final String PREFERENCE_FILE_NAME = "otaden_preference";

    // もも電互換用、ももクロのカテゴリーナンバー、PHP呼び出し時の分岐判断に使用
    public static final int CATEGORY_MOMO = 9;
}
