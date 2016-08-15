package jp.onetake.binzumejigoku.activity;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;

import com.android.vending.billing.IInAppBillingService;

import java.util.List;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.exception.ContentsException;
import jp.onetake.binzumejigoku.fragment.dialog.AlertDialogFragment;
import jp.onetake.binzumejigoku.util.PurchaseQueryThread;

/**
 * アプリ内アイテム（投げ銭＝寄付）の購入を担当するアクティビティ
 */
public class PurchaseActivity extends BasicActivity
		implements PurchaseQueryThread.PurchaseQueryListener, AlertDialogFragment.OnAlertListener {
	private final String TAG_DIALOG_PURCHASE_NOT_SUPPORTED	= "PurchaseActivity.TAG_DIALOG_PURCHASE_NOT_SUPPORTED";
	private final String TAG_DIALOG_PURCHASE_SUCCEEDED		= "PurchaseActivity.TAG_DIALOG_PURCHASE_SUCCEEDED";
	private final String TAG_DIALOG_PURCHASE_FAILED			= "PurchaseActivity.TAG_DIALOG_PURCHASE_FAILED";
	private final String TAG_DIALOG_ALREADY_DONATED			= "PurchaseActivity.TAG_DIALOG_ALREADY_DONATED";

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		intent.setPackage("com.android.vending");
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mService != null) {
			unbindService(mConnection);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == getResources().getInteger(R.integer.request_code_execute_purchase)) {
			int resCode = data.getIntExtra("RESPONSE_CODE", 0);

			// 課金処理に成功した
			if (resultCode == RESULT_OK && resCode == 0) {
				showDialogFragment(
						AlertDialogFragment.newInstance(R.string.phrase_confirm, R.string.message_thanks_for_purchase, R.string.phrase_ok),
						TAG_DIALOG_PURCHASE_SUCCEEDED);
			// 課金処理がうまくいかなかった
			} else {
				showErrorDialog();
			}
		}
	}

	/**
	 * 端末がInAppBillingをサポートしているか確認し、購入可能なアイテムの一覧を非同期で問い合わせる
	 */
	private void queryPurchaseItemsAsync() {
		PurchaseQueryThread thread = new PurchaseQueryThread(this, mService);
		thread.setListener(this);
		thread.start();
	}

	/**
	 * 課金処理をIABのサービスにリクエストする
	 * @return	trueの場合は課金処理のリクエストが行われている
	 */
	private boolean executePurchase() {
		try {
			Bundle intentBundle = mService.getBuyIntent(3, getPackageName(), getString(R.string.iab_item_id), "inapp", null);

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

	// エラーダイアログの表示
	private void showErrorDialog() {
		showDialogFragment(
				AlertDialogFragment.newInstance(R.string.phrase_error, R.string.error_purchase_failed, R.string.phrase_ok),
				TAG_DIALOG_PURCHASE_FAILED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPurchaseNotSupported() {
		showDialogFragment(
				AlertDialogFragment.newInstance(R.string.phrase_error, R.string.error_purchase_not_supported, R.string.phrase_ok),
				TAG_DIALOG_PURCHASE_NOT_SUPPORTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onQueryResult(List<String> skuList) {
		if (skuList.size() > 0) {
			executePurchase();
		} else {
			showErrorDialog();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onQueryFailed(ContentsException e) {
		showErrorDialog();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onAlreadyPurchased(String purchaseToken) {
		showDialogFragment(
				AlertDialogFragment.newInstance(R.string.phrase_confirm, R.string.message_already_purchased, R.string.phrase_ok),
				TAG_DIALOG_ALREADY_DONATED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConfirmed(DialogFragment dialog) {
		setResult(dialog.getTag().equals(TAG_DIALOG_PURCHASE_SUCCEEDED) ? RESULT_OK : RESULT_CANCELED);
		finish();
	}
}
