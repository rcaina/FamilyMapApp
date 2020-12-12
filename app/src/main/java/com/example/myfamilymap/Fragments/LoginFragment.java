package com.example.myfamilymap.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.myfamilymap.Main.MainActivity;
import com.example.myfamilymap.R;
import com.example.myfamilymap.Resource.HttpClient;
import com.example.myfamilymap.Singleton.DataCache;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import Models.Person;
import Requests.LoginRequest;
import Requests.RegisterRequest;
import Response.EventResponse;
import Response.LoginResponse;
import Response.PersonResponse;
import Response.RegisterResponse;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    EditText serverHost, serverPort, userName, password, firstName, lastName, email;
    Button loginButton, registerButton;

    private boolean gender = false;
    private String userFirstName;
    private String userLastName;
    private String gen;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View newView = inflater.inflate(R.layout.fragment_login, container, false);

        serverHost = (EditText) newView.findViewById(R.id.serverHostField);
        serverPort = (EditText) newView.findViewById(R.id.serverPortField);
        userName = (EditText) newView.findViewById(R.id.userNameField);
        password = (EditText) newView.findViewById(R.id.userPasswordField);
        firstName = (EditText) newView.findViewById(R.id.firstNameField);
        lastName = (EditText) newView.findViewById(R.id.lastNameField);
        email = (EditText) newView.findViewById(R.id.emailNameField);
        loginButton = (Button) newView.findViewById(R.id.loginButton);
        registerButton = (Button) newView.findViewById(R.id.registerButton);
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);

        serverHost.addTextChangedListener(watcher);
        serverPort.addTextChangedListener(watcher);
        userName.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);
        firstName.addTextChangedListener(watcher);
        lastName.addTextChangedListener(watcher);
        email.addTextChangedListener(watcher);

        Button m = (Button) newView.findViewById(R.id.radioGenderMale);
        Button f = (Button) newView.findViewById(R.id.radioGenderFemale);

        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();

                switch (v.getId()) {
                    case -1:
                        gender = false;
                        break;
                    case R.id.radioGenderMale:
                        if (checked) gender = true;
                        gen = "m";
                        break;
                }
                buttonEnabling();
            }
        });

        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();

                switch (v.getId()) {
                    case -1:
                        gender = false;
                        break;
                    case R.id.radioGenderFemale:
                        if (checked) gender = true;
                        gen = "f";
                        break;
                }
                buttonEnabling();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new LoginTask().execute();

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new RegisterTask().execute();
            }
        });

        return newView;
    }

    @Override
    public void onClick(View v) { }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            buttonEnabling();
        }
    };

    private void buttonEnabling() {
        if (serverHost.getText().length() > 0 &&
                serverPort.getText().length() > 0 &&
                userName.getText().length() > 0 &&
                password.getText().length() > 0
        ) {
            loginButton.setEnabled(true);
            registerButton.setEnabled(false);

            if (firstName.getText().length() > 0 &&
                    lastName.getText().length() > 0 &&
                    email.getText().length() > 0 &&
                    gender == true) {

                registerButton.setEnabled(true);
            }
        }
        else {
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);
        }
    }

    private class LoginTask extends AsyncTask<Integer, Integer, LoginResponse> {

        @Override
        protected LoginResponse doInBackground(Integer... Integer) {
            String host = serverHost.getText().toString();
            String port = serverPort.getText().toString();
            String user = userName.getText().toString();
            String pass = password.getText().toString();

            DataCache save = DataCache.getInstance();
            LoginResponse personData = new LoginResponse();
            Gson gson = new Gson();
            String result = "";

            String site = "http://" + host + ":" + port + "/user/login";

            try {
                URL url = new URL(site);

                HttpClient httpClient = new HttpClient();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                LoginRequest userInfo = new LoginRequest(user, pass);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                gson.toJson(userInfo, out);

                result = httpClient.getUrl(connection, out);

                if (result == null) {
                    return null;
                }

                personData = gson.fromJson(result, LoginResponse.class);

            } catch (IOException e) { e.printStackTrace(); }

            save.person = personData;

            return personData; //return the results or what you get from the POST
        }

        protected void onProgressUpdate() {
        }

        @Override
        protected void onPostExecute(LoginResponse personData) {

            if(personData == null){
                Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
            }else {
                new FamilyTask().execute(personData);
            }
        }
    }

    public class FamilyTask extends AsyncTask<LoginResponse, Integer, PersonResponse> {

        @Override
        protected PersonResponse doInBackground(LoginResponse... personData) {

            String host = serverHost.getText().toString();
            String port = serverPort.getText().toString();

            PersonResponse data = new PersonResponse();
            EventResponse eventData = new EventResponse();

            LoginResponse userInfo = personData[0];
            String authToken = userInfo.getAuthToken();
            DataCache save = DataCache.getInstance();

            String result = "";
            String result2 = "";

            Gson gson = new Gson();

            String site = "http://" + host + ":" + port + "/person";

            try {
                URL url = new URL(site);

                HttpClient httpClient = new HttpClient();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", authToken);
                connection.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                gson.toJson(userInfo, out);

                result = httpClient.getUrl(connection, out);

                if (result == null) {
                    return null;
                }

                data = gson.fromJson(result, PersonResponse.class);

            } catch (IOException e) { e.printStackTrace(); }

            ArrayList<Person> person = data.getData();
            Person user = new Person();

            for (Person one : person) {
                if (userInfo.getPersonID1().equals(one.getPersonID())) {
                    user = one;
                }
            }

            userFirstName = user.getFirstName();
            userLastName = user.getLastName();

            save.family = data;

            String site2 = "http://" + host + ":" + port + "/event";

            try {
                URL url = new URL(site2);

                HttpClient httpClient = new HttpClient();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", authToken);
                connection.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                gson.toJson(userInfo, out);

                result2 = httpClient.getUrl(connection, out);
                if (result2 == null) {
                    return null;
                }

                eventData = gson.fromJson(result2, EventResponse.class);

            } catch (IOException e) { e.printStackTrace(); }

            save.events = eventData;

            return data;
        }

        protected void onProgressUpdate() {
        }

        protected void onPostExecute(PersonResponse familyData) {

            boolean dataFound = familyData.getSuccess();
            DataCache save = DataCache.getInstance();
            save.setPeople(familyData);
            save.setEventColors();

            if (dataFound) {
                Toast.makeText(getActivity(), userFirstName + " " + userLastName, Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).mapFragment();
            } else {
                Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RegisterTask extends AsyncTask<Integer, Integer, RegisterResponse> {
        @Override
        protected RegisterResponse doInBackground(Integer... integers) {
            String host = serverHost.getText().toString();
            String port = serverPort.getText().toString();
            String user = userName.getText().toString();
            String pass = password.getText().toString();
            String first = firstName.getText().toString();
            String last = lastName.getText().toString();
            String mail = email.getText().toString();
            String result = "";

            RegisterResponse personData = new RegisterResponse();
            DataCache save = DataCache.getInstance();
            Gson gson = new Gson();

            String site = "http://" + host + ":" + port + "/user/register";

            try {
                URL url = new URL(site);

                HttpClient httpClient = new HttpClient();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                RegisterRequest userInfo = new RegisterRequest(user, pass, mail, first, last, gen);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                gson.toJson(userInfo, out);

                result = httpClient.getUrl(connection, out);
                if (result == null) {
                    return null;
                }

                personData = gson.fromJson(result, RegisterResponse.class);

            } catch (IOException e) { e.printStackTrace(); }

            save.newPerson = personData;

            return personData;
        }

        @Override
        protected void onPostExecute(RegisterResponse personData) {
            if(personData == null){
                Toast.makeText(getActivity(), "Registration Failed", Toast.LENGTH_SHORT).show();
            }else {
                new RegFamilyTask().execute(personData);
            }
        }
    }

    public class RegFamilyTask extends AsyncTask<RegisterResponse, Integer, PersonResponse> {

        @Override
        protected PersonResponse doInBackground(RegisterResponse... personData) {

            String host = serverHost.getText().toString();
            String port = serverPort.getText().toString();

            PersonResponse data = new PersonResponse();
            EventResponse eventData = new EventResponse();

            RegisterResponse userInfo = personData[0];
            DataCache save = DataCache.getInstance();
            String authToken = userInfo.getAuthToken();

            String result = "";
            String result2 = "";

            Gson gson = new Gson();

            String site = "http://" + host + ":" + port + "/person";

            try {
                URL url = new URL(site);

                HttpClient httpClient = new HttpClient();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", authToken);
                connection.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                gson.toJson(userInfo, out);

                result = httpClient.getUrl(connection, out);
                if (result == null) {
                    return null;
                }

                data = gson.fromJson(result, PersonResponse.class);

            } catch (IOException e) { e.printStackTrace(); }

            save.family = data;


            String site2 = "http://" + host + ":" + port + "/event";

            try {
                URL url = new URL(site2);

                HttpClient httpClient = new HttpClient();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", authToken);
                connection.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                gson.toJson(userInfo, out);

                result2 = httpClient.getUrl(connection, out);
                if (result2 == null) {
                    return null;
                }

                eventData = gson.fromJson(result2, EventResponse.class);

            } catch (IOException e) {
                e.printStackTrace();
            }

            save.events = eventData;

            return data;
        }

        protected void onPostExecute(PersonResponse familyData) {

            DataCache save = DataCache.getInstance();
            boolean dataFound = familyData.getSuccess();

            save.setPeople(familyData);
            save.setEventColors();

            if (dataFound) {
                Toast.makeText(getActivity(), firstName.getText().toString() + " " + lastName.getText().toString(), Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).mapFragment();
            } else {
                Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class FamilyEventTask extends AsyncTask<Integer, Integer, EventResponse> {
        @Override
        protected EventResponse doInBackground(Integer... integers) {
            return null;
        }
    }
}