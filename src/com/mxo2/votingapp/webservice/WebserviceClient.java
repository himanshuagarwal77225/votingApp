package com.mxo2.votingapp.webservice;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.mxo2.votingapp.db.DatabaseSchema;
import com.mxo2.votingapp.pojo.AboutUsModel;
import com.mxo2.votingapp.pojo.Constituency_Result_Model;
import com.mxo2.votingapp.pojo.Constituency_pojo;
import com.mxo2.votingapp.pojo.Election_pojo;
import com.mxo2.votingapp.pojo.OpinionPollingAnswerModel;
import com.mxo2.votingapp.pojo.Party_pojo;
import com.mxo2.votingapp.utils.AppConstants;
import com.mxo2.votingapp.utils.AppUtils;
import com.mxo2.votingapp.utils.Log;

public class WebserviceClient {

	private AppUtils m_AppUtils = null;
	private String ws_url = AppConstants.app_url + "api/ssapi.php?";
	private String key = "949b60d446e37792fd1a8b38e1b7fda4";
	private String param_key = "key";
	private String param_function = "fct";
	String msg, results;

	public WebserviceClient(AppUtils appUtils) {
		m_AppUtils = appUtils;
	}

	/**
	 * verifies if the input login credentials are valid or not
	 **/
	public JSONObject checkLoginCredentials(String login_id,
			String login_password) {
		JSONObject GetJsonObject = null;
		try {
			if (!TextUtils.isEmpty(login_id)
					&& !TextUtils.isEmpty(login_password)) {
				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
				postParameters.add(new BasicNameValuePair(param_key, key));
				postParameters.add(new BasicNameValuePair("email", login_id));
				postParameters.add(new BasicNameValuePair("pass",
						login_password));
				postParameters.add(new BasicNameValuePair(param_function,
						"ulogin"));
				String result = sendDataToServer(postParameters);
				if (!TextUtils.isEmpty(result)) {
					if (result.equalsIgnoreCase("Invalid")
							|| (result.equalsIgnoreCase("Error"))) {
						return null;
					} else {
						GetJsonObject = new JSONObject(result);
						if (GetJsonObject != null) {
							if (!GetJsonObject.has("msg")) {
								return null;
							}
							JSONArray GetJsonArray = GetJsonObject
									.optJSONArray("msg");
							if (GetJsonArray == null) {
								return null;
							} else if (GetJsonArray.length() == 0) {
								return null;
							}
							GetJsonObject = GetJsonArray.getJSONObject(0);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return null;
		}
		return GetJsonObject;
	}

	/**
	 * registers a new user
	 **/
	public String doRegistration(ArrayList<NameValuePair> postParameters) {
		String reg_result = "";
		JSONObject GetJsonObject = null;
		try {
			if (postParameters == null) {
				return null;
			}
			if (postParameters.size() == 0) {
				return null;
			} else {
				postParameters.add(new BasicNameValuePair(param_key, key));
				postParameters.add(new BasicNameValuePair(param_function,
						"ureg"));
				String result = sendDataToServer(postParameters);
				if (!TextUtils.isEmpty(result)) {
					if (result.equalsIgnoreCase("Invalid")
							|| (result.equalsIgnoreCase("Error"))) {
						return null;
					} else {
						GetJsonObject = new JSONObject(result);
						if (GetJsonObject != null) {
							if (!GetJsonObject.has("msg")) {
								return null;
							}
							reg_result = GetJsonObject.optString("msg");
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return null;
		}
		return reg_result;
	}

	/**
	 * fetches list of elections
	 **/
	public ArrayList<Election_pojo> fetchElectionList() {

		ArrayList<Election_pojo> electionList = null;
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(param_key, key));
			postParameters.add(new BasicNameValuePair(param_function,
					"election"));
			String response = sendDataToServer(postParameters);
			if (TextUtils.isEmpty(response)) {
				return null;
			} else {
				JSONObject GetJsonObject = new JSONObject(response);
				if (GetJsonObject != null) {
					if (GetJsonObject.has("result")) {
						if (!GetJsonObject.optString("result")
								.equalsIgnoreCase("success")) {
							return null;
						}
					}
					if (!GetJsonObject.has("msg")) {
						return null;
					}
					JSONArray GetJsonArray = GetJsonObject.optJSONArray("msg");
					if (GetJsonArray == null) {
						return null;
					} else if (GetJsonArray.length() == 0) {
						return null;
					} else {
						electionList = new ArrayList<Election_pojo>();
						for (int index = 0; index < GetJsonArray.length(); index++) {
							GetJsonObject = GetJsonArray.getJSONObject(index);
							Election_pojo electionObj = null;
							if (GetJsonObject != null) {
								electionObj = new Election_pojo(
										GetJsonObject
												.optString(DatabaseSchema.ELECTION_MASTER.ID),
										GetJsonObject
												.optString(DatabaseSchema.ELECTION_MASTER.NAME),
										GetJsonObject
												.optString(DatabaseSchema.ELECTION_MASTER.TYPE),
										GetJsonObject
												.optString(DatabaseSchema.ELECTION_MASTER.STATE_ID),
										GetJsonObject
												.optString(DatabaseSchema.ELECTION_MASTER.DESCRIPTION),
										GetJsonObject
												.optString(DatabaseSchema.ELECTION_MASTER.ELECTION_DATE),
										GetJsonObject
												.optString(DatabaseSchema.ELECTION_MASTER.CREATE_DATE),
										GetJsonObject
												.optString(DatabaseSchema.ELECTION_MASTER.STATUS));
								if (electionObj != null) {
									electionList.add(electionObj);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return null;
		}
		return electionList;
	}

	/**
	 * fetches list of districts for a particular state
	 **/
	public JSONArray fetchElectionDistrictList(int electionId) {

		JSONArray districtJsonArray = null;
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(param_key, key));
			postParameters.add(new BasicNameValuePair(param_function,
					"election"));
			postParameters.add(new BasicNameValuePair("id", String
					.valueOf(electionId)));
			String response = sendDataToServer(postParameters);
			if (TextUtils.isEmpty(response)) {
				return null;
			} else {
				JSONObject GetJsonObject = new JSONObject(response);
				if (GetJsonObject != null) {
					if (GetJsonObject.has("result")) {
						if (!GetJsonObject.optString("result")
								.equalsIgnoreCase("success")) {
							return null;
						}
					}
					if (!GetJsonObject.has("msg")) {
						return null;
					}
					districtJsonArray = GetJsonObject.optJSONArray("msg");
					if (districtJsonArray == null) {
						return null;
					} else if (districtJsonArray.length() == 0) {
						return null;
					}
				}
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return null;
		}
		return districtJsonArray;
	}

	/**
	 * fetches list of Constituency Result
	 **/
	public List<Constituency_Result_Model> fetchConstituencyResult(
			int electionId, int constId) {

		String str;
		JSONArray resultJsonArray = null;
		List<Constituency_Result_Model> resultList = new ArrayList<Constituency_Result_Model>();
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(param_key, key));
			postParameters
					.add(new BasicNameValuePair(param_function, "vresult"));
			postParameters.add(new BasicNameValuePair("id", String
					.valueOf(electionId)));
			postParameters.add(new BasicNameValuePair("cid", String
					.valueOf(constId)));
			String response = sendDataToServer(postParameters);
			if (TextUtils.isEmpty(response)) {
				return null;
			} else {
				JSONObject GetJsonObject = new JSONObject(response);
				if (GetJsonObject != null) {
					if (GetJsonObject.has("result")) {
						if (!GetJsonObject.optString("result")
								.equalsIgnoreCase("success")) {
							return null;
						}
					}
					if (!GetJsonObject.has("msg")) {
						return null;
					}
					str = GetJsonObject.optString("msg");
					GetJsonObject = new JSONObject(str);
					/*
					 * if (!GetJsonObject.has("election")) { return null; } str
					 * = GetJsonObject.optString("election"); GetJsonObject =
					 * new JSONObject(str);
					 */
					if (!GetJsonObject.has("data")) {
						return null;
					}
					resultJsonArray = GetJsonObject.getJSONArray("data");
					/*
					 * str = GetJsonObject.optString("data"); GetJsonObject =
					 * new JSONObject(str); if (!GetJsonObject.has("1")) {
					 * return null; }
					 * 
					 * resultJsonArray = GetJsonObject.getJSONArray("1");
					 */
					if (resultJsonArray == null) {
						return null;
					} else if (resultJsonArray.length() == 0) {
						return null;
					}
					for (int i = 0; i < resultJsonArray.length(); i++) {
						JSONObject json_data = resultJsonArray.getJSONObject(i);
						Constituency_Result_Model data = new Constituency_Result_Model();
						data.setPartyName(json_data.getString("pname"));
						data.setPartyAbbrivation(json_data.getString("pabbr"));
						data.setPartyIcon(json_data.getString("picon"));
						data.setTotalCount(json_data.getString("total"));
						resultList.add(data);
					}

				}
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return null;
		}
		return resultList;
	}

	/**
	 * fetches list of About Us
	 **/
	public List<AboutUsModel> fetchAboutUs() {

		String str;
		List<AboutUsModel> resultList = new ArrayList<AboutUsModel>();
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(param_key, key));
			postParameters.add(new BasicNameValuePair(param_function, "about"));
			String response = sendDataToServer(postParameters);
			if (TextUtils.isEmpty(response)) {
				return null;
			} else {
				JSONObject GetJsonObject = new JSONObject(response);
				if (GetJsonObject != null) {
					if (GetJsonObject.has("result")) {
						if (!GetJsonObject.optString("result")
								.equalsIgnoreCase("success")) {
							return null;
						}
					}
					if (!GetJsonObject.has("msg")) {
						return null;
					}
					str = GetJsonObject.optString("msg");
					GetJsonObject = new JSONObject(str);
					if (!GetJsonObject.has("title")) {
						return null;
					}
					if (!GetJsonObject.has("desc")) {
						return null;
					}
					AboutUsModel data = new AboutUsModel();
					data.setImageLogo(GetJsonObject.getString("title"));
					data.setContent(GetJsonObject.getString("desc"));
					resultList.add(data);

				}
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return null;
		}
		return resultList;
	}

	/**
	 * fetches list of overall Result
	 **/
	public List<Constituency_Result_Model> fetchConstituencyResult(
			int electionId) {

		String str;
		JSONArray resultJsonArray = null;
		List<Constituency_Result_Model> resultList = new ArrayList<Constituency_Result_Model>();
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(param_key, key));
			postParameters
					.add(new BasicNameValuePair(param_function, "vresult"));
			postParameters.add(new BasicNameValuePair("id", String
					.valueOf(electionId)));
			String response = sendDataToServer(postParameters);
			if (TextUtils.isEmpty(response)) {
				return null;
			} else {
				JSONObject GetJsonObject = new JSONObject(response);
				if (GetJsonObject != null) {
					if (GetJsonObject.has("result")) {
						if (!GetJsonObject.optString("result")
								.equalsIgnoreCase("success")) {
							return null;
						}
					}
					if (!GetJsonObject.has("msg")) {
						return null;
					}
					str = GetJsonObject.optString("msg");
					GetJsonObject = new JSONObject(str);
					/*
					 * if (!GetJsonObject.has("election")) { return null; } str
					 * = GetJsonObject.optString("election"); GetJsonObject =
					 * new JSONObject(str);
					 */
					if (!GetJsonObject.has("data")) {
						return null;
					}
					resultJsonArray = GetJsonObject.getJSONArray("data");
					/*
					 * str = GetJsonObject.optString("data"); GetJsonObject =
					 * new JSONObject(str); if (!GetJsonObject.has("1")) {
					 * return null; }
					 * 
					 * resultJsonArray = GetJsonObject.getJSONArray("1");
					 */
					if (resultJsonArray == null) {
						return null;
					} else if (resultJsonArray.length() == 0) {
						return null;
					}
					for (int i = 0; i < resultJsonArray.length(); i++) {
						JSONObject json_data = resultJsonArray.getJSONObject(i);
						Constituency_Result_Model data = new Constituency_Result_Model();
						data.setPartyName(json_data.getString("pname"));
						data.setPartyAbbrivation(json_data.getString("pabbr"));
						data.setPartyIcon(json_data.getString("picon"));
						data.setTotalCount(json_data.getString("total"));
						resultList.add(data);
					}

				}
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return null;
		}
		return resultList;
	}
	/**
	 * fetches list of overall Result
	 **/
	public List<OpinionPollingAnswerModel> fetchOpinionResult(
			int queIDs) {

		String str;
		String que;
		String queID;
		JSONArray resultJsonArray = null;
		List<OpinionPollingAnswerModel> resultList = new ArrayList<OpinionPollingAnswerModel>();
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(param_key, key));
			postParameters
					.add(new BasicNameValuePair(param_function, "pollres"));
			postParameters.add(new BasicNameValuePair("id", String
					.valueOf(queIDs)));
			String response = sendDataToServer(postParameters);
			if (TextUtils.isEmpty(response)) {
				return null;
			} else {
				JSONObject GetJsonObject = new JSONObject(response);
				if (GetJsonObject != null) {
					if (GetJsonObject.has("result")) {
						if (!GetJsonObject.optString("result")
								.equalsIgnoreCase("success")) {
							return null;
						}
					}
					if (!GetJsonObject.has("msg")) {
						return null;
					}
					
					str = GetJsonObject.optString("msg");
					GetJsonObject = new JSONObject(str);
					/*
					 * if (!GetJsonObject.has("election")) { return null; } str
					 * = GetJsonObject.optString("election"); GetJsonObject =
					 * new JSONObject(str);
					 */
					if (!GetJsonObject.has("data")) {
						return null;
					}
					if (!GetJsonObject.has("ques")) {
						return null;
					}
					que = GetJsonObject.optString("ques");
					if (!GetJsonObject.has("ques_id")) {
						return null;
					}
					queID = GetJsonObject.optString("ques_id");
					resultJsonArray = GetJsonObject.getJSONArray("data");
					/*
					 * str = GetJsonObject.optString("data"); GetJsonObject =
					 * new JSONObject(str); if (!GetJsonObject.has("1")) {
					 * return null; }
					 * 
					 * resultJsonArray = GetJsonObject.getJSONArray("1");
					 */
					if (resultJsonArray == null) {
						return null;
					} else if (resultJsonArray.length() == 0) {
						return null;
					}
					for (int i = 0; i < resultJsonArray.length(); i++) {
						JSONObject json_data = resultJsonArray.getJSONObject(i);
						OpinionPollingAnswerModel data = new OpinionPollingAnswerModel();
						data.setQuestion(que);
						data.setQueID(queID);
						data.setPicon(json_data.getString("picon"));
						data.setTotal(json_data.getString("total"));
						data.setAnsID(json_data.getString("ans_id"));
						resultList.add(data);
					}

				}
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return null;
		}
		return resultList;
	}

	/**
	 * fetches list of constituencies for a particular election
	 **/
	public ArrayList<Constituency_pojo> fetchElectionConstituencyList(
			int electionId) {

		ArrayList<Constituency_pojo> constList = null;
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(param_key, key));
			postParameters.add(new BasicNameValuePair(param_function,
					"election"));
			postParameters.add(new BasicNameValuePair("id", String
					.valueOf(electionId)));
			String response = sendDataToServer(postParameters);
			if (TextUtils.isEmpty(response)) {
				return null;
			} else {
				JSONObject GetJsonObject = new JSONObject(response);
				if (GetJsonObject != null) {
					if (GetJsonObject.has("result")) {
						if (!GetJsonObject.optString("result")
								.equalsIgnoreCase("success")) {
							return null;
						}
					}
					if (!GetJsonObject.has("msg")) {
						return null;
					}
					JSONArray GetJsonArray = GetJsonObject.optJSONArray("msg");
					if (GetJsonArray == null) {
						return null;
					} else if (GetJsonArray.length() == 0) {
						return null;
					} else {
						constList = new ArrayList<Constituency_pojo>();
						for (int index = 0; index < GetJsonArray.length(); index++) {
							GetJsonObject = GetJsonArray.getJSONObject(index);
							Constituency_pojo constObj = null;
							if (GetJsonObject != null) {
								constObj = new Constituency_pojo(
										GetJsonObject
												.optString(DatabaseSchema.CONSTITUENCY_MASTER.ID),
										GetJsonObject
												.optString(DatabaseSchema.CONSTITUENCY_MASTER.NAME),
										GetJsonObject
												.optString(DatabaseSchema.CONSTITUENCY_MASTER.STATE_ID),
										GetJsonObject
												.optString(DatabaseSchema.CONSTITUENCY_MASTER.DISTRICT_ID));
								if (constObj != null) {
									constList.add(constObj);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return null;
		}
		return constList;
	}

	/**
	 * fetches list of candidate parties for a particular election and
	 * constituency
	 **/
	public ArrayList<Party_pojo> fetchPartyList(int electionId, int constId) {

		ArrayList<Party_pojo> partyList = null;
		JSONArray partyJsonArray = null;

		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(param_key, key));
			postParameters.add(new BasicNameValuePair(param_function,
					"election"));
			postParameters.add(new BasicNameValuePair("id", String
					.valueOf(electionId)));
			postParameters.add(new BasicNameValuePair("cid", String
					.valueOf(constId)));
			String response = sendDataToServer(postParameters);
			if (TextUtils.isEmpty(response)) {
				return null;
			} else {
				JSONObject GetJsonObject = new JSONObject(response);
				if (GetJsonObject != null) {
					if (GetJsonObject.has("result")) {
						if (!GetJsonObject.optString("result")
								.equalsIgnoreCase("success")) {
							return null;
						}
					}
					if (!GetJsonObject.has("msg")) {
						return null;
					}
					partyJsonArray = GetJsonObject.optJSONArray("msg");
					if (partyJsonArray == null) {
						return null;
					} else if (partyJsonArray.length() == 0) {
						return null;
					} else {
						partyList = new ArrayList<Party_pojo>();
						for (int index = 0; index < partyJsonArray.length(); index++) {
							GetJsonObject = partyJsonArray.getJSONObject(index);
							Party_pojo partyItem = null;
							if (GetJsonObject != null) {
								partyItem = new Party_pojo(
										GetJsonObject
												.optString(DatabaseSchema.PARTY_MASTER.ID),
										GetJsonObject
												.optString(DatabaseSchema.PARTY_MASTER.NAME),
										GetJsonObject
												.optString(DatabaseSchema.PARTY_MASTER.ABBREVIATION),
										GetJsonObject
												.optString(DatabaseSchema.PARTY_MASTER.SYMBOL));
								if (partyItem != null) {
									partyList.add(partyItem);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return null;
		}
		return partyList;
	}

	/**
	 * cast the vote
	 **/
	public String[] castVote(String electionId, String constId, String partyId,
			String mobile) {
		try {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair(param_key, key));
			postParameters
					.add(new BasicNameValuePair(param_function, "ele_in"));
			postParameters.add(new BasicNameValuePair("eid", electionId));
			postParameters.add(new BasicNameValuePair("pid", constId));
			postParameters.add(new BasicNameValuePair("con", partyId));
			postParameters.add(new BasicNameValuePair("uid", mobile));
			String result = sendDataToServer(postParameters);
			Log.i("Result is", result);
			results = msg = "";

			JSONObject GetJsonObject = new JSONObject(result);
			if (GetJsonObject != null) {
				if (GetJsonObject.has("result")) {
					if (GetJsonObject.has("msg")) {
						msg = GetJsonObject.optString("msg");
						results = GetJsonObject.optString("result");
						String response[] = { results, msg };
						return response;
					}
				}

			}

			/*
			 * if (!TextUtils.isEmpty(result)) {
			 * 
			 * if (result.equalsIgnoreCase("Invalid") ||
			 * (result.equalsIgnoreCase("Error"))) {
			 * 
			 * return false; } else if (result.equalsIgnoreCase("Success")) {
			 * return true; } }
			 */

		} catch (Exception e) {
			Log.e("Exception --> ", e.toString());
			return null;
		}
		return null;
	}

	public String sendDataToServer(ArrayList<NameValuePair> PostParamsValue) {

		String responseBody = "";
		try {
			Log.w("REQUEST ", ws_url + " " + PostParamsValue);
			HttpPost WSHttpPost = null;
			HttpClient WSHttpClient = null;
			HttpResponse WSHttpResponse = null;
			UrlEncodedFormEntity WSUrlEncodedFormEntity = null;
			WSHttpClient = getNewHttpClient();
			WSHttpPost = new HttpPost(ws_url);
			if (PostParamsValue != null) {
				WSUrlEncodedFormEntity = new UrlEncodedFormEntity(
						PostParamsValue);
				WSUrlEncodedFormEntity = new UrlEncodedFormEntity(
						PostParamsValue);
				WSHttpPost.setEntity(WSUrlEncodedFormEntity);
			}
			WSHttpResponse = WSHttpClient.execute(WSHttpPost);
			responseBody = EntityUtils.toString(WSHttpResponse.getEntity());
			Log.w("SERVER RESPONSE", responseBody);

		} catch (Exception e) {
			Log.e("Exception 123 ==> ", e.toString());
			return "";
		}
		if (responseBody == null || responseBody.equalsIgnoreCase("null")) {
			return "";
		} else {
			return responseBody;
		}
	}

	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

}