package jp.onetake.binzumejigoku.activity;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.exception.ContentsException;

public class InAppBillingActivity extends BasicActivity {
	private final int MESSAGE_WHAT_IAB_NOT_SUPPORTED	= 10001;
	private final int MESSAGE_WHAT_QUERY_SUCCEEDED		= 10002;
	private final int MESSAGE_WHAT_QUERY_FAILED 		= 10003;

	private IInAppBillingService mService;

	// IABを使って一覧の購入処理を行うためのコネクション
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IInAppBillingService.Stub.asInterface(service);
			queryPurchaseItemsAsync();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
	};

	// IABで購入可能なアイテムの一覧を問い合わせた結果を受け取るHandler
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 端末がIABをサポートしており、SKU問い合わせクエリの結果が支障なく返ってきた
			if (msg.what == MESSAGE_WHAT_QUERY_SUCCEEDED) {
				ArrayList<String> detailList = (ArrayList<String>)msg.obj;

				try {
					if (detailList.size() > 0) {
						for (String detail : detailList) {
							JSONObject json = new JSONObject(detail);
							String sku = json.getString("productId");
							String price = json.getString("price");

							android.util.Log.i("IAB-CHECK", "SKU = " + sku + ", PRICE = " + price);

							executePurchase();

							return;
						}
					} else {
						Toast.makeText(InAppBillingActivity.this, "Detail list is empty.", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException jse) {
					jse.printStackTrace();
					Toast.makeText(InAppBillingActivity.this, "SKU JSON is invalid format.", Toast.LENGTH_SHORT).show();
				}
			// IABがサポートされていないか、クエリが正しく返ってこなかった
			} else {
				Toast.makeText(InAppBillingActivity.this, "Exception occurred.", Toast.LENGTH_SHORT).show();

				ContentsException e = (ContentsException)msg.obj;

				android.util.Log.e("IAB-CHECK", e.getMessage());

				if (e.getCause() != null) {
					android.util.Log.e("IAB-CHECK", e.getCause().getMessage());
				}
			}

			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		intent.setPackage("com.android.vending");
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mService != null) {
			unbindService(mConnection);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == getResources().getInteger(R.integer.request_code_execute_purchase)) {
			int resCode = data.getIntExtra("RESPONSE_CODE", 0);
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			String signature = data.getStringExtra("INAPP_DATA_SIGNATURE");

			if (resultCode == RESULT_OK && resCode == 0) {
				try {
					JSONObject jo = new JSONObject(purchaseData);
					String sku = jo.getString("productId");

					Toast.makeText(this, "Purchase succeeded!!! : SKU = " + sku, Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(this, "Purchase failed...", Toast.LENGTH_SHORT).show();
			}

			finish();
		}
	}

	/**
	 * 端末がInAppBillingをサポートしているか確認し、購入可能なアイテムの一覧を非同期で問い合わせる
	 */
	private void queryPurchaseItemsAsync() {
		(new Thread() {
			@Override
			public void run() {
				try {
					// IABがサポートされているかチェック
					int resCodeSupported = mService.isBillingSupported(3, getPackageName(), "inapp");
					if (resCodeSupported == 0) {
						ArrayList<String> skuList = new ArrayList<>();
						skuList.add(getString(R.string.iab_item_id));
						Bundle query = new Bundle();
						query.putStringArrayList("ITEM_ID_LIST", skuList);

						Bundle details = mService.getSkuDetails(3, getPackageName(), "inapp", query);

						// SKUの一覧を取得
						int resCodeQuery = details.getInt("RESPONSE_CODE");
						if (resCodeQuery == 0) {
							mHandler.obtainMessage(MESSAGE_WHAT_QUERY_SUCCEEDED, details.getStringArrayList("DETAILS_LIST")).sendToTarget();
						} else {
							mHandler.obtainMessage(
									MESSAGE_WHAT_QUERY_FAILED,
									new ContentsException(getString(R.string.exception_iab_query_invalid_response) + Integer.toString(resCodeQuery))).sendToTarget();
						}
					} else {
						mHandler.obtainMessage(
								MESSAGE_WHAT_IAB_NOT_SUPPORTED,
								new ContentsException(getString(R.string.exception_iab_not_supported) + Integer.toString(resCodeSupported))).sendToTarget();
					}
				} catch (RemoteException re) {
					mHandler.obtainMessage(MESSAGE_WHAT_QUERY_FAILED, new ContentsException(re)).sendToTarget();
				}
			}
		}).start();
	}

	private boolean executePurchase() {
		try {
			Bundle intentBundle = mService.getBuyIntent(3, getPackageName(), getString(R.string.iab_item_id), "inapp", null);
			// Bundle intentBundle = mService.getBuyIntent(3, getPackageName(), "android.test.purchased", "inapp", null);

			int resCode = intentBundle.getInt("RESPONSE_CODE");
			if (resCode == 0) {
				PendingIntent intent = intentBundle.getParcelable("BUY_INTENT");

				startIntentSenderForResult(
						intent.getIntentSender(), getResources().getInteger(R.integer.request_code_execute_purchase), new Intent(), 0, 0, 0);

				return true;
			}
		} catch (IntentSender.SendIntentException sie) {
			sie.printStackTrace();
		} catch (RemoteException re) {
			re.printStackTrace();
		}

		return false;
	}
}
