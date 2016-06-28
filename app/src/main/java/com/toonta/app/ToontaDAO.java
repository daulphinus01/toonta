package com.toonta.app;

import android.app.Application;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.toonta.app.forms.ToontaUser;
import com.toonta.app.model.SurveyResponse;
import com.toonta.app.utils.ToontaConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guillaume on 22/05/2016.
 */
public class ToontaDAO extends Application {

    public enum NetworkAnswer {
        NO_NETWORK, NO_SERVER, BAD_REQUEST, FAILED_LOGIN, OK_LOGIN, ACCOUNT_ALREADY_EXISTS, AUTH_FAILURE, FAILED_UPDATING, FORBIDDEN, OK_UPDATING
    }
    private static String API = "http://92.222.90.138:8080/toonta-api/";
    private static String USER = "user/";
    private static String SURVEY = "survey/";
    private static String TAG = "DAO";

    private static RequestQueue requestQueue;

    public static void init(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface SimpleNetworkCallInterface {
        void onSuccess();
        void onFailure(NetworkAnswer error);
    }

    /**
     * LOGIN
     */

    public static void login(String phoneNumber, String password, final SimpleNetworkCallInterface simpleNetworkCallInterface) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                API+USER+"login?phone="+phoneNumber+"&password="+password,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG + " LogIn", response.toString());
                        LoginAnswer loginAnswer = parseLogin(response);
                        if (loginAnswer.loggedIn) {
                            ToontaSharedPreferences.validateLoggedIn(loginAnswer.token, loginAnswer.userId);
                            simpleNetworkCallInterface.onSuccess();
                        } else {
                            simpleNetworkCallInterface.onFailure(NetworkAnswer.FAILED_LOGIN);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG + " LogIn", error.toString());
                        if (error instanceof NoConnectionError) {
                            simpleNetworkCallInterface.onFailure(NetworkAnswer.NO_NETWORK);
                        } else {
                            simpleNetworkCallInterface.onFailure(NetworkAnswer.NO_SERVER);
                        }
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private static LoginAnswer parseLogin(JSONObject jsonObject) {
        LoginAnswer loginAnswer = new LoginAnswer();
        try {
            if (jsonObject != null) {
                String status = jsonObject.getString("status");
                if (status != null && status.equals("OK")) {
                    String token = jsonObject.getString("userToken");
                    String userId = jsonObject.getString("userId");
                    if (token != null && userId != null) {
                        loginAnswer.loggedIn = true;
                        loginAnswer.userId = userId;
                        loginAnswer.token = token;
                        return loginAnswer;
                    }
                }
            }
        } catch (JSONException e) {
            return loginAnswer;
        }
        return loginAnswer;
    }

    private static class LoginAnswer {
        boolean loggedIn;
        String token;
        String userId;

        public LoginAnswer() {
            loggedIn = false;
        }
    }

    /**
     * SIGNUP
     */

    public static void signup(String phoneNumber, String password, final SimpleNetworkCallInterface simpleNetworkCallInterface) {
        try {
            JSONObject content = new JSONObject();
            content.put("password", password);
            content.put("phoneNumber", phoneNumber);
            content.put("type", "INDIVIDUAL");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    API+USER+"signup",
                    content,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v(TAG + " Signup", response.toString());
                            LoginAnswer loginAnswer = parseLogin(response);
                            if (loginAnswer.loggedIn) {
                                ToontaSharedPreferences.validateLoggedIn(loginAnswer.token, loginAnswer.userId);
                                simpleNetworkCallInterface.onSuccess();
                            } else {
                                simpleNetworkCallInterface.onFailure(NetworkAnswer.ACCOUNT_ALREADY_EXISTS);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG + " Signup", error.toString());
                            if (error instanceof NoConnectionError) {
                                simpleNetworkCallInterface.onFailure(NetworkAnswer.NO_NETWORK);
                            } else {
                                simpleNetworkCallInterface.onFailure(NetworkAnswer.NO_SERVER);
                            }
                        }
                    });
                requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * SURVEY LIST
     */

    public interface SurveysListNetworkCallInterface {
        void onSuccess(ArrayList<SurveysListAnswer.SurveyElement> surveysListAnswer);
        void onFailure(NetworkAnswer error);
    }

    public interface SurveyNetworkCallInterface {
        void onSuccess(QuestionsList questionsList);
        void onFailure(NetworkAnswer error);
    }

    public interface ToontaUserNetworkCallInterface {
        void onSuccess(ToontaUser toontaUser);
        void onFailure(NetworkAnswer error);
    }

    public interface UpdateToontaUserNetworkCallInterface {
        void onSuccess(ToontaDAO.NetworkAnswer networkAnswer);
        void onFailure(NetworkAnswer error);
    }

    public interface SurveyPostNetworkCallInterface {
        void onSuccess();
        void onFailure(NetworkAnswer error);
    }

    public static void getSurveys(int page, final SurveysListNetworkCallInterface surveysListNetworkCallInterface) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                API+SURVEY+"list",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v(TAG + "Surveys list", response.toString());
                        surveysListNetworkCallInterface.onSuccess(parseSurveyList(response).surveyElements);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG + "Surveys list", error.toString());
                        if (error instanceof NoConnectionError) {
                            surveysListNetworkCallInterface.onFailure(NetworkAnswer.NO_NETWORK);
                        } else if (error instanceof AuthFailureError) {
                            surveysListNetworkCallInterface.onFailure(NetworkAnswer.AUTH_FAILURE);
                        } else {
                            surveysListNetworkCallInterface.onFailure(NetworkAnswer.NO_SERVER);
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("userId", ToontaSharedPreferences.toontaSharedPreferences.userId);
                params.put("userToken", ToontaSharedPreferences.toontaSharedPreferences.requestToken);

                return params;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static void getSurvey(String surveyId, final SurveyNetworkCallInterface surveyNetworkCallInterface) {
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET,
                API+SURVEY+surveyId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG + "Survey ", response.toString());
                        surveyNetworkCallInterface.onSuccess(parseQuestionsList(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG + "Survey ", error.toString());
                        if (error instanceof NoConnectionError) {
                            surveyNetworkCallInterface.onFailure(NetworkAnswer.NO_NETWORK);
                        } else if (error instanceof AuthFailureError) {
                            surveyNetworkCallInterface.onFailure(NetworkAnswer.AUTH_FAILURE);
                        } else {
                            surveyNetworkCallInterface.onFailure(NetworkAnswer.NO_SERVER);
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("userId", ToontaSharedPreferences.toontaSharedPreferences.userId);
                params.put("userToken", ToontaSharedPreferences.toontaSharedPreferences.requestToken);

                return params;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static void getToontaUser(String userId, final ToontaUserNetworkCallInterface toontaUserNetworkCallInterface) {
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET,
                API + USER + "/" + userId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG + "ToontaUser ", response.toString());
                        toontaUserNetworkCallInterface.onSuccess(parseToontaUser(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG + "ToontaUser ", error.toString());
                        if (error instanceof NoConnectionError) {
                            toontaUserNetworkCallInterface.onFailure(NetworkAnswer.NO_NETWORK);
                        } else if (error instanceof AuthFailureError) {
                            toontaUserNetworkCallInterface.onFailure(NetworkAnswer.AUTH_FAILURE);
                        } else {
                            toontaUserNetworkCallInterface.onFailure(NetworkAnswer.NO_SERVER);
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("userId", ToontaSharedPreferences.toontaSharedPreferences.userId);
                params.put("userToken", ToontaSharedPreferences.toontaSharedPreferences.requestToken);

                return params;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static void postSurveyResponse(SurveyResponse surveyResponse, final SurveyPostNetworkCallInterface surveyPostNetworkCallInterface) {
        if (surveyResponse != null) {
            try {
                JSONArray respArray = new JSONArray();
                for (int i = 0; i < surveyResponse.responses.size(); i++) {
                    JSONObject resp = new JSONObject();
                    resp.put("choiceId", surveyResponse.responses.get(i).choiceId);
                    resp.put("questionId", surveyResponse.responses.get(i).questionId);
                    resp.put("textAnswer", surveyResponse.responses.get(i).textAnswer);
                    resp.put("yesNoAnswer", surveyResponse.responses.get(i).yesNoAnswer);
                    respArray.put(i, resp);
                }

                JSONObject content = new JSONObject();
                content.put("respondentId", surveyResponse.respondentId);
                content.put("surveyId", surveyResponse.surveyId);
                content.put("responses", respArray);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        API+USER+"answer",
                        content,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.v(TAG + " answer", response.toString());
                                surveyPostNetworkCallInterface.onSuccess();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG + " Signup", error.toString());
                                if (error instanceof NoConnectionError) {
                                    surveyPostNetworkCallInterface.onFailure(NetworkAnswer.NO_NETWORK);
                                } else {
                                    surveyPostNetworkCallInterface.onFailure(NetworkAnswer.NO_SERVER);
                                }
                            }
                        });
                requestQueue.add(jsonObjectRequest);
            } catch (JSONException e) {
                // TODO Log errors
            }
        }
    }

    public static class SurveysListAnswer {
        public static class SurveyElement {
            public String name;
            public int reward = 0;
            public String surveyId;

            public SurveyElement(String name, int reward, String surveyId) {
                this.name = name;
                this.reward = reward;
                this.surveyId = surveyId;
            }

            public SurveyElement(String name, int reward) {
                this.name = name;
                this.reward = reward;
            }

            public String print() {
                return reward + " toons";
            }
        }

        ArrayList<SurveyElement> surveyElements;

        public SurveysListAnswer() {
            surveyElements = new ArrayList<>();
        }
    }

    public static class QuestionsList implements Parcelable {

        protected QuestionsList(Parcel in) {
            authorId = in.readString();
            createDate = in.readString();
            expery = in.readString();
            surveyId = in.readString();
            receivedUnswerCptr = in.readInt();
            reward = in.readInt();
            questionResponseElements = in.createTypedArrayList(QuestionResponse.CREATOR);
        }

        public static final Creator<QuestionsList> CREATOR = new Creator<QuestionsList>() {
            @Override
            public QuestionsList createFromParcel(Parcel in) {
                return new QuestionsList(in);
            }

            @Override
            public QuestionsList[] newArray(int size) {
                return new QuestionsList[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(authorId);
            dest.writeString(createDate);
            dest.writeString(expery);
            dest.writeString(surveyId);
            dest.writeInt(receivedUnswerCptr);
            dest.writeInt(reward);
            dest.writeTypedList(questionResponseElements);
        }

        public static class Question {}

        public static class QuestionResponse implements Parcelable {
            // option; 'CHECKBOX', 'RADIO'
            public String category;
            // optional
            public ArrayList<ResponseChoiceResponse> choices;
            // optional
            public String id;
            // optional
            public int order;
            // optional
            public String question;
            // optional; ['BASIC', 'YES_NO', 'MULTIPLE_CHOICE']
            public String type;

            public QuestionResponse(String category, ArrayList<ResponseChoiceResponse> choices, String id, int order, String question, String type) {
                this.category = category;
                this.choices = choices;
                this.id = id;
                this.order = order;
                this.question = question;
                this.type = type;
            }

            protected QuestionResponse(Parcel in) {
                category = in.readString();
                id = in.readString();
                order = in.readInt();
                question = in.readString();
                type = in.readString();
                in.readTypedList(choices, ResponseChoiceResponse.CREATOR);
            }

            public QuestionResponse() {}

            public static final Creator<QuestionResponse> CREATOR = new Creator<QuestionResponse>() {
                @Override
                public QuestionResponse createFromParcel(Parcel in) {
                    return new QuestionResponse(in);
                }

                @Override
                public QuestionResponse[] newArray(int size) {
                    return new QuestionResponse[size];
                }
            };

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(category);
                dest.writeString(id);
                dest.writeInt(order);
                dest.writeString(question);
                dest.writeString(type);
                dest.writeTypedList(choices);
            }

            @Override
            public String toString() {
                return "QuestionResponse{" +
                        "category='" + category + '\'' +
                        ", choices=" + choices +
                        ", id='" + id + '\'' +
                        ", order=" + order +
                        ", question='" + question + '\'' +
                        ", type='" + type + '\'' +
                        '}';
            }
        }

        public static class ResponseChoiceResponse implements Parcelable {
            public String id;
            public String value;
            public ResponseChoiceResponse(String id, String value) {
                this.id = id;
                this.value = value;
            }

            public ResponseChoiceResponse() {}

            protected ResponseChoiceResponse(Parcel in) {
                id = in.readString();
                value = in.readString();
            }

            public static final Creator<ResponseChoiceResponse> CREATOR = new Creator<ResponseChoiceResponse>() {
                @Override
                public ResponseChoiceResponse createFromParcel(Parcel in) {
                    return new ResponseChoiceResponse(in);
                }

                @Override
                public ResponseChoiceResponse[] newArray(int size) {
                    return new ResponseChoiceResponse[size];
                }
            };

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(id);
                dest.writeString(value);
            }

            @Override
            public String toString() {
                return "ResponseChoiceResponse{" +
                        "id='" + id + '\'' +
                        ", value='" + value + '\'' +
                        '}';
            }
        }

        public String authorId;
        public String createDate;
        public String expery;
        public String surveyId;
        public int receivedUnswerCptr;
        public int reward;
        public ArrayList<QuestionResponse> questionResponseElements;
        public QuestionsList() {
            questionResponseElements = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "QuestionsList{" +
                    "authorId='" + authorId + '\'' +
                    ", createDate='" + createDate + '\'' +
                    ", expery='" + expery + '\'' +
                    ", surveyId='" + surveyId + '\'' +
                    ", receivedUnswerCptr=" + receivedUnswerCptr +
                    ", reward=" + reward +
                    ", questionResponseElements=" + questionResponseElements +
                    '}';
        }
    }

    private static QuestionsList parseQuestionsList(JSONObject jsonObject) {
        QuestionsList questionsList = new QuestionsList();
        try {
            if (jsonObject != null) {
                questionsList.authorId = jsonObject.getString("authorId");
                questionsList.createDate = jsonObject.getString("createdDate");
                questionsList.expery = jsonObject.getString("expiry");
                questionsList.surveyId = jsonObject.getString("id");
                questionsList.receivedUnswerCptr = jsonObject.getInt("receivedAnswer");
                questionsList.reward = jsonObject.getInt("reward");

                questionsList.questionResponseElements = new ArrayList<>();
                JSONArray qstList = jsonObject.getJSONArray("questions");
                if (qstList != null) {
                    for (int k = 0; k < qstList.length(); k++) {
                        JSONObject jsonObjectQst = qstList.getJSONObject(k);
                        QuestionsList.QuestionResponse qstResponse = new QuestionsList.QuestionResponse();
                        qstResponse.category = jsonObjectQst.getString("category");
                        qstResponse.id = jsonObjectQst.getString("id");
                        qstResponse.order = jsonObjectQst.getInt("order");
                        qstResponse.question = jsonObjectQst.getString("question");
                        qstResponse.type = jsonObjectQst.getString("type");

                        qstResponse.choices = new ArrayList<>();
                        JSONArray respChoiceResp = jsonObjectQst.getJSONArray("choices");
                        if (respChoiceResp != null) {
                            for (int l = 0; l < respChoiceResp.length(); l++) {
                                JSONObject jsonObjectRespChoiceResp = respChoiceResp.getJSONObject(l);
                                QuestionsList.ResponseChoiceResponse responseChoiceResponse = new QuestionsList.ResponseChoiceResponse();
                                responseChoiceResponse.id = jsonObjectRespChoiceResp.getString("id");
                                responseChoiceResponse.value = jsonObjectRespChoiceResp.getString("value");

                                qstResponse.choices.add(responseChoiceResponse);
                            }
                        }

                        questionsList.questionResponseElements.add(qstResponse);
                    }
                }
            }
        } catch (JSONException e) {
            // TODO Do some logging
        }
        return questionsList;
    }

    private static SurveysListAnswer parseSurveyList(JSONArray jsonArray) {
        SurveysListAnswer surveysListAnswer = new SurveysListAnswer();
        try {
            if (jsonArray != null) {
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    //boolean isActive = jsonObject.getBoolean("active ");
                    String name = jsonObject.getString("name");
                    int reward = jsonObject.getInt("reward");
                    String surveyId = jsonObject.getString("id");
                    if (name != null) {
                        surveysListAnswer.surveyElements.add(new SurveysListAnswer.SurveyElement(name, reward, surveyId));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return surveysListAnswer;
    }

    public static ToontaUser parseToontaUser(JSONObject jsonObject) {
        ToontaUser toontaUser = new ToontaUser();
        try {
            if (jsonObject != null) {
                toontaUser.phoneNumber =  jsonObject.getString("phoneNumber");
                toontaUser.birthdate = jsonObject.getString("birthdate");
                toontaUser.email = jsonObject.getString("email");
                toontaUser.firstname = jsonObject.getString("firstname");
                toontaUser.lastname = jsonObject.getString("lastname");
                toontaUser.name = jsonObject.getString("name");
                toontaUser.id = jsonObject.getString("id");
                toontaUser.profession = jsonObject.getString("profession");
                toontaUser.sexe = jsonObject.getString("sexe");

                JSONObject jsonObjectAddr = jsonObject.getJSONObject("address");
                if (jsonObjectAddr != null) {
                    toontaUser.address.city = jsonObjectAddr.getString("city");
                    toontaUser.address.country = jsonObjectAddr.getString("country");
                    toontaUser.address.department = jsonObjectAddr.getString("department");
                    toontaUser.address.region = jsonObjectAddr.getString("region");
                }

                JSONObject jsonObjectBank_ = jsonObject.getJSONObject("bank");
                if (jsonObjectBank_ != null) {
                    toontaUser.bank_.balance = jsonObjectBank_.getInt("balance");
                    toontaUser.bank_.id = jsonObjectBank_.getString("id");
                }

                return toontaUser;

            }
        } catch (JSONException e) {
            // TODO Do some logging
            return toontaUser;
        }
        return toontaUser;
    }


    /**
     * Updates toonta user info
     * @param toontaUser
     * @param updateToontaUserNetworkCallInterface
     */
    public static void updateToontaUser(ToontaUser toontaUser, final UpdateToontaUserNetworkCallInterface updateToontaUserNetworkCallInterface) {
        try {
            JSONObject content = new JSONObject();
            content.put("birthdate", toontaUser.birthdate);
            content.put("city", "Paris");
            content.put("country", "France");
            content.put("email", toontaUser.email);
            content.put("firstname", toontaUser.firstname);
            content.put("lastname", toontaUser.lastname);
            content.put("name", "Jean Dupond");
            content.put("password", "qwerty12");
            content.put("phoneNumber", "rwego123");
            content.put("profession", "ALL");
            content.put("sexe", "M");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,
                    API+USER+"update/" + ToontaSharedPreferences.toontaSharedPreferences.userId,
                    content,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v(TAG + " Update ToontaUser", response.toString());
                            String responseStatus = "";
                            try {
                                responseStatus = response.getString("status");
                            } catch (JSONException e) {
                                // Nothing to do, responseStatus stays empty and is dealt with
                                // in the underneath part.
                            }

                            if (responseStatus.isEmpty()) {
                                updateToontaUserNetworkCallInterface.onFailure(NetworkAnswer.FAILED_UPDATING);
                            } else if (responseStatus.equals(HttpStatus.CREATED.name())){
                                updateToontaUserNetworkCallInterface.onSuccess(NetworkAnswer.OK_UPDATING);
                            } else if (responseStatus.equals(HttpStatus.FORBIDDEN)) {
                                updateToontaUserNetworkCallInterface.onSuccess(NetworkAnswer.FORBIDDEN);
                            } else {
                                updateToontaUserNetworkCallInterface.onFailure(NetworkAnswer.FAILED_UPDATING);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG + " Update ToontaUser", error.toString());
                            if (error instanceof NoConnectionError) {
                                updateToontaUserNetworkCallInterface.onFailure(NetworkAnswer.NO_NETWORK);
                            } else {
                                updateToontaUserNetworkCallInterface.onFailure(NetworkAnswer.NO_SERVER);
                            }
                        }
                    })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("userId", ToontaSharedPreferences.toontaSharedPreferences.userId);
                    params.put("userToken", ToontaSharedPreferences.toontaSharedPreferences.requestToken);

                    return params;
                }
            };

            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}