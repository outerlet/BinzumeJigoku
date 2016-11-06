package jp.onetake.binzumejigoku.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.exception.ContentsException;

/**
 * 購入処理の前にSKUの一覧を取得したり自身の購入履歴を取得するためのスレッド
 */
public class PurchaseQueryThread extends Thread {
	/**
	 * PurchaseQueryThreadでクエリを発行した結果を受け取る為のリスナ
	 */
	public interface PurchaseQueryListener {
		/**
		 * 端末がIABをサポートしていなかった
		 */
		void onPurchaseNotSupported();

		/**
		 * 以前に購入したことがなく、SKU一覧の取得にも成功した
		 * @param skuList	取得したSKU一覧
		 */
		void onQueryResult(List<String> skuList);

		/**
		 * SKU一覧の取得に失敗した
		 * @param e	SKU一覧の取得において発生した例外
		 */
		void onQueryFailed(ContentsException e);

		/**
		 * 以前に購入したことがあった
		 * @param purchaseToken	購入を取り消すためのトークン
		 */
		void onAlreadyPurchased(String purchaseToken);
	}

	// IABで購入可能なアイテムの一覧を問い合わせた結果を受け取るHandler
	private class IabHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (mListener != null) {
				switch (msg.what) {
					// 端末がIABをサポートしており、SKU問い合わせクエリの結果が支障なく返ってきた
					case MESSAGE_WHAT_QUERY_SUCCEEDED:
						mListener.onQueryResult((List<String>)msg.obj);
						break;
					// 端末がIABをサポートしていない
					case MESSAGE_WHAT_IAB_NOT_SUPPORTED:
						mListener.onPurchaseNotSupported();
						break;
					// IABがサポートされていないか、クエリが正しく返ってこなかった
					case MESSAGE_WHAT_QUERY_FAILED:
						mListener.onQueryFailed((ContentsException)msg.obj);
						break;
					// 既に購入済み
					case MESSAGE_WHAT_ALREADY_PURCHASED:
						mListener.onAlreadyPurchased((String)msg.obj);
						break;
				}
			}
		}
	}

	/*
	 * アイテム問い合わせにおいて、どういう結果が返されたかをHandler側で判別するためにwhatに与えるint値
	 */
	private final int MESSAGE_WHAT_IAB_NOT_SUPPORTED	= 10001;	// 端末がIABをサポートしていない
	private final int MESSAGE_WHAT_QUERY_SUCCEEDED		= 10002;	// 問い合わせが正常に完了した
	private final int MESSAGE_WHAT_QUERY_FAILED 		= 10003;	// 問い合わせにおいて何かしらの問題が発生した
	private final int MESSAGE_WHAT_ALREADY_PURCHASED	= 10004;	// 購入済み

	private Context mContext;
	private IInAppBillingService mService;
	private PurchaseQueryListener mListener;

	private IabHandler mHandler = new IabHandler();

	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 * @param service	IABを実行する為のService
	 */
	public PurchaseQueryThread(Context context, IInAppBillingService service) {
		mContext = context;
		mService = service;
	}

	/**
	 * クエリの結果を受け取るリスナを設定する
	 * @param listener	クエリの結果を受け取るリスナ
	 */
	public void setListener(PurchaseQueryListener listener) {
		mListener = listener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		int messageWhat = MESSAGE_WHAT_IAB_NOT_SUPPORTED;

		try {
			// IABがサポートされているか
			checkIsIabSupport();

			// SKU一覧の問い合わせ
			messageWhat = MESSAGE_WHAT_QUERY_FAILED;
			List<String> skuList = queryPurchasableSkus();

			// 購入履歴の有無を確認
			messageWhat = MESSAGE_WHAT_QUERY_FAILED;
			String token = checkAlreadyPurchased();

			// 購入を取り消すためのトークンが得られた、つまり既に課金したことがあればこちら
			if (!TextUtils.isEmpty(token)) {
				// 課金を取り消したうえで再度課金を実行する場合はforce_iab_consumeをtrueに設定してこちらの分岐に入る
				if (mContext.getResources().getBoolean(R.bool.force_iab_consume)) {
					consumePurchase(token);

					android.util.Log.w("IN-APP-BILLING", "Succeeded consume purchase");

					mHandler.obtainMessage(MESSAGE_WHAT_QUERY_SUCCEEDED, skuList).sendToTarget();
				// 課金を取り消さない(通常の動作)の場合はこちらの分岐に入る
				} else {
					mHandler.obtainMessage(MESSAGE_WHAT_ALREADY_PURCHASED, token).sendToTarget();
				}
			// トークンが得られない、つまり課金をしたことがなければこちら
			} else {
				mHandler.obtainMessage(MESSAGE_WHAT_QUERY_SUCCEEDED, skuList).sendToTarget();
			}
		} catch (ContentsException ce) {
			mHandler.obtainMessage(messageWhat, ce).sendToTarget();
		} catch (RemoteException re) {
			mHandler.obtainMessage(MESSAGE_WHAT_QUERY_FAILED, new ContentsException(re)).sendToTarget();
		}
	}

	/**
	 * In App Purchaseがサポートされているか
	 * @throws RemoteException
	 * @throws ContentsException
	 */
	private void checkIsIabSupport() throws RemoteException, ContentsException {
		// IABがサポートされているか
		int codeSupported = mService.isBillingSupported(3, mContext.getPackageName(), "inapp");

		if (codeSupported != 0) {
			throw new ContentsException(
					mContext.getString(R.string.exception_iab_not_supported) + Integer.toString(codeSupported));
		}
	}

	/**
	 * アプリ内課金アイテムのSKU一覧を取得する
	 * @return	アプリ内課金アイテムのSKU一覧
	 * @throws RemoteException
	 * @throws ContentsException
	 */
	private List<String> queryPurchasableSkus() throws RemoteException, ContentsException {
		ArrayList<String> skuList = new ArrayList<>();
		skuList.add(mContext.getString(R.string.iab_item_id));
		Bundle query = new Bundle();
		query.putStringArrayList("ITEM_ID_LIST", skuList);

		Bundle result = mService.getSkuDetails(3, mContext.getPackageName(), "inapp", query);

		// SKUの一覧を取得
		int resCode = result.getInt("RESPONSE_CODE");

		if (resCode != 0) {
			throw new ContentsException(
					mContext.getString(R.string.exception_iab_query_invalid_response) + Integer.toString(resCode));
		}

		return result.getStringArrayList("DETAILS_LIST");
	}

	/**
	 * 既にアプリ内課金アイテムを購入済みかどうか確認し、購入済みならそれを取り消すためのトークンを返却する
	 * @return	アプリ内課金アイテムが購入済みならそれを取り消すためのトークン。未購入ならnull
	 * @throws RemoteException
	 * @throws ContentsException
	 */
	private String checkAlreadyPurchased() throws RemoteException, ContentsException {
		Bundle result = mService.getPurchases(3, mContext.getPackageName(), "inapp", null);

		int resCode = result.getInt("RESPONSE_CODE");

		if (resCode != 0) {
			throw new ContentsException(
					mContext.getString(R.string.exception_iab_cannot_get_purchases) + Integer.toString(resCode));
		}

		ArrayList<String> itemList = result.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");

		if (itemList != null && itemList.size() > 0) {
			try {
				ArrayList<String> dataList = result.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
				if (dataList != null) {
					for (String data : dataList) {
						JSONObject json = new JSONObject(data);

						if (json.getString("productId").equals(mContext.getString(R.string.iab_item_id))) {
							return json.getString("purchaseToken");
						}
					}
				}
			} catch (JSONException je) {
				throw new ContentsException(mContext.getString(R.string.error_purchase_failed));
			}
		}

		return null;
	}

	/**
	 * 与えられたtokenに対応する購入履歴を取り消す<br />
	 * ここで引数に与えるのはcheckAlreadyPurchasedの戻り値として得られた文字列
	 * @param token	購入履歴を取り消すために必要なトークン
	 * @throws RemoteException
	 * @throws ContentsException
	 */
	private void consumePurchase(String token) throws RemoteException, ContentsException {
		int response = mService.consumePurchase(3, mContext.getPackageName(), token);

		if (response != 0) {
			throw new ContentsException(mContext.getString(R.string.exception_iab_cannot_consume_purchases));
		}
	}
}
