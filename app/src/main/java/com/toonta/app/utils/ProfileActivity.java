package com.toonta.app.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.toonta.app.R;
import com.toonta.app.ToontaSharedPreferences;
import com.toonta.app.forms.ToontaUser;
import com.toonta.app.model.ToontaBank;

import java.util.Calendar;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final int GALLERY_INTENT_CALLED = 20;
    private static final int GALLERY_KITKAT_INTENT_CALLED = 21;
    private ProgressDialog progressDialog;
    private ToontaUserInterceptor toontaUserInterceptor;

    private LinearLayout saveChangesLayout;
    private Button saveChangesButton;
    private ToontaUser updatedUser;

    private EditText firstName;
    private EditText lastName;
    private TextView birthDate;
    private EditText emailAddress;
    private EditText phoneNumber;
    private Spinner professionalActivity;
    private AutoCompleteTextView countryResidencePlace;
    private EditText cumulatedPoint;
    private AutoCompleteTextView cityResidencePlace;

    private TextInputLayout residencePlaceArea;
    private ToontaAddressInterceptor toontaAddressInterceptor;

    // Other infos that needs to be sent when updating user's info
    private String sexe = "";

    // Indicates that some fields have been modified
    private boolean updatesAvailable = false;

    private EditText[] allEditTexts = new EditText[6];

    private final int RESULT_LOAD_IMAGE = 100;
    private ImageView profilePic;
    RoundImage roundedImage;

    // Bithdate
    private int toontaUserBDyear;
    private int toontaUserBDmonth;
    private int toontaUserBDday;
    static final int DATE_DIALOG_ID = 999;
    private static final int PICK_FROM_GALLERY = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupActionBar();

        // Settings
        ImageView toontaMenuButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_menu_settings);
        toontaMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActionMode(Utils.initActionModeCallBack(ProfileActivity.this));
                v.setSelected(true);
            }
        });

        // Share
        ImageView toontaShareButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.toonta_share);
        toontaShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startShareActionIntent(ProfileActivity.this);
            }
        });

        residencePlaceArea = (TextInputLayout) findViewById(R.id.city_of_residence_area);

        // Shown when user decides to modify personal info
        saveChangesLayout = (LinearLayout) findViewById(R.id.toonta_layout_save_changes);
        assert saveChangesLayout != null;
        // saveChangesLayout.setVisibility(View.INVISIBLE);

        saveChangesButton = (Button) findViewById(R.id.toonta_save_changes_button);
        assert saveChangesButton != null;
        saveChangesButton.setTransformationMethod(null);

        firstName = (EditText) findViewById(R.id.first_name);
        assert firstName != null;
        allEditTexts[0] = firstName;

        lastName = (EditText) findViewById(R.id.last_name);
        assert lastName != null;
        allEditTexts[1] = lastName;

        birthDate = (TextView) findViewById(R.id.birth_date);
        assert birthDate != null;
        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        emailAddress = (EditText) findViewById(R.id.email_address);
        assert emailAddress != null;
        allEditTexts[2] = emailAddress;

        phoneNumber = (EditText) findViewById(R.id.phone_number);
        assert phoneNumber != null;
        allEditTexts[3] = phoneNumber;

        professionalActivity = (Spinner) findViewById(R.id.professional_activity);
        assert professionalActivity != null;

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.toonta_profession_type, R.layout.spinner_item);
        professionalActivity.setAdapter(adapter);

        countryResidencePlace = (AutoCompleteTextView) findViewById(R.id.residence_place);
        assert countryResidencePlace != null;
        // Get countries array
        final String[] countries = getResources().getStringArray(R.array.toonta_countries_list);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        countryResidencePlace.setThreshold(1);
        countryResidencePlace.setAdapter(countryAdapter);
        countryResidencePlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (countryResidencePlace.getText() != null){
                    toontaAddressInterceptor.updateToontaCitiesAdaptor(countryResidencePlace.getText().toString());
                }
            }
        });
        allEditTexts[4] = countryResidencePlace;

        cumulatedPoint = (EditText) findViewById(R.id.toonta_cumilated_points);
        assert cumulatedPoint != null;

        cityResidencePlace = (AutoCompleteTextView) findViewById(R.id.label_three);
        assert cityResidencePlace != null;
        cityResidencePlace.setThreshold(1);
        allEditTexts[5] = cityResidencePlace;

        addOnEditorActionListener();

        /*************************************************************************************
         *                              PROFILE PICTURE
         *************************************************************************************/

        profilePic = (ImageView) findViewById(R.id.toonta_profile_pix);
        assert profilePic != null;
        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.img_profile);
        roundedImage = new RoundImage(bm);
        profilePic.setImageDrawable(roundedImage);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // call android default gallery
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // ******** code for crop image
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 0);
                intent.putExtra("aspectY", 0);
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 150);

                try {

                    intent.putExtra("return-data", true);
                    startActivityForResult(Intent.createChooser(intent,
                            "Complete action using"), PICK_FROM_GALLERY);
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }
            }
        });



        // Setting profile picture
        /*if (existProfilePicture()) {
            profilePic.setImageBitmap(BitmapFactory.decodeFile(ToontaSharedPreferences.toontaSharedPreferences.profilePicPath));
        }
        // profilePic.setImageBitmap(BitmapFactory.decodeFile(ToontaSharedPreferences.toontaSharedPreferences.profilePicPath));*/
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });


        toontaUserInterceptor = new ToontaUserInterceptor(getBaseContext(), new ToontaUserInterceptor.ToontaUserViewUpdater() {
            @Override
            public void onToontaUserGet(ToontaUser toontaUser) {
                Log.v("ProfileActivity ->", toontaUser.toString());
                populateTextViews(toontaUser);
                if (toontaUser.address.country != null)
                    toontaAddressInterceptor.updateToontaCitiesAdaptor(toontaUser.address.country);
            }

            @Override
            public void onToontaUserUpdate(String responseStatus) {
                Snackbar.make(findViewById(android.R.id.content), responseStatus, Snackbar.LENGTH_LONG).show();
                firstName.requestFocus();
                populateTextViews(updatedUser);
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });

        toontaUserInterceptor.fetchToontaUser(ToontaSharedPreferences.toontaSharedPreferences.userId);

        // Setting saveChanges button click listener
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedUser = new ToontaUser();
                if (birthDate.getText() != null)
                    updatedUser.birthdate = birthDate.getText().toString();
                if (emailAddress.getText() != null)
                    updatedUser.email = emailAddress.getText().toString();
                if (firstName.getText() != null)
                    updatedUser.firstname = firstName.getText().toString();
                if (lastName.getText() != null)
                    lastName.getText().toString();
                if (phoneNumber.getText() != null)
                    phoneNumber.getText().toString();
                if (cityResidencePlace.getText() != null)
                    updatedUser.address.city = cityResidencePlace.getText().toString();
                if (countryResidencePlace.getText() != null)
                    updatedUser.address.country = countryResidencePlace.getText().toString();
                updatedUser.profession = String.valueOf(professionalActivity.getSelectedItem());
                updatedUser.sexe = sexe;

                Log.e("***ProfileActivity***", "SaveChangeButton clicked [ " + updatedUser.toString() + " ]");

                // Sending changes to server
                toontaUserInterceptor.updateToontaUser(updatedUser);
            }
        });

        toontaAddressInterceptor = new ToontaAddressInterceptor(ProfileActivity.this, new ToontaAddressInterceptor.ToontaAddressViewUpdater() {
            @Override
            public void onToontaAddressGet(List<String> cities) {
                residencePlaceArea.setVisibility(View.VISIBLE);
                if (cities.size() < 1) {
                    cityResidencePlace.setText("");
                    cityResidencePlace.setAdapter(null);
                    Snackbar.make(findViewById(android.R.id.content), "No cities available for the selected country", Snackbar.LENGTH_LONG).show();
                } else {
                    ArrayAdapter<String> citiesArrayAdapter = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_list_item_1, cities);
                    cityResidencePlace.setAdapter(citiesArrayAdapter);
                }
            }

            @Override
            public void onFailure(String error) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar mActionBar = getSupportActionBar();
            assert mActionBar != null;
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayShowCustomEnabled(true);

            View mCustomView = getLayoutInflater().inflate(R.layout.custom_actionbar_with_up_button, null);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);
            mActionBar.setCustomView(mCustomView, layoutParams);
            Toolbar parent = (Toolbar) mCustomView.getParent();
            parent.setContentInsetsAbsolute(0, 0);
        }
    }

    public void goUp(View view) {
        NavUtils.navigateUpFromSameTask(ProfileActivity.this);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RESULT_LOAD_IMAGE) return;
        if (data == null) return;
        Uri selectedImage = data.getData();
        if (requestCode == GALLERY_KITKAT_INTENT_CALLED) {
            getContentResolver().takePersistableUriPermission(selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        String path = getPathFromURI(selectedImage);
        Snackbar.make(findViewById(android.R.id.content), path, Snackbar.LENGTH_LONG).show();
        profilePic.setImageBitmap(BitmapFactory.decodeFile(path));
        //ToontaSharedPreferences.setToontaProfilePicPath(path);


        roundedImage = new RoundImage(BitmapFactory.decodeFile(path));
        profilePic.setImageDrawable(roundedImage);

        /*if (requestCode == PICK_FROM_GALLERY) {
            Bundle extras2 = data.getExtras();
            if (extras2 != null) {
                Bitmap photo = extras2.getParcelable("data");
                profilePic.setImageBitmap(photo);

            }
        }*/
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                // TODO Use Theme_Material_Dialog_Alert instead of AlertDialog.THEME_HOLO_DARK
                return new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    /* Choose an image from Gallery */
    public void openImageChooser() {
        if (Build.VERSION.SDK_INT < 19){
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_INTENT_CALLED);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
        }
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(proj[0]);
            res = cursor.getString(columnIndex);
        }
        cursor.close();
        return res;
    }

    private void showAlertDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

        builder.setMessage(R.string.toonta_field_modif_msg)
                .setTitle(R.string.toonta_field_modif);

        builder.setPositiveButton(R.string.toonat_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                updatesAvailable = true;
            }
        });
        builder.setNegativeButton(R.string.toonta_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Not necessary
                updatesAvailable = false;
            }
        });


        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void populateTextViews(ToontaUser toontaUser) {
        firstName.setText(toontaUser.firstname);
        lastName.setText(toontaUser.lastname);
        birthDate.setText(toontaUser.birthdate);
        emailAddress.setText(toontaUser.email);
        phoneNumber.setText(toontaUser.phoneNumber);
        professionalActivity.setSelection(getPosFromToontaProfessionByString(toontaUser.profession));
        cumulatedPoint.setText(Integer.toString(toontaUser.bank_.balance));
        if (toontaUser.address != null) {
            if (toontaUser.address.city != null && !toontaUser.address.city.isEmpty()) {
                residencePlaceArea.setVisibility(View.VISIBLE);
                cityResidencePlace.setText(toontaUser.address.city);
            }
            if (toontaUser.address.country != null && !toontaUser.address.country.isEmpty())
                countryResidencePlace.setText(toontaUser.address.country);
        }

        if (toontaUser.birthdate != null && !toontaUser.birthdate.isEmpty()) {
            String[] splittedDate = toontaUser.birthdate.split("-");
            toontaUserBDyear = Integer.parseInt(splittedDate[0]);
            toontaUserBDmonth = Integer.parseInt(splittedDate[1]);
            toontaUserBDday = Integer.parseInt(splittedDate[2]);
        }

        sexe = toontaUser.sexe;
    }

    private void addOnEditorActionListener() {
        for (int i = 0; i < allEditTexts.length - 1; i++) {
            final int finalI = i;
            if (allEditTexts[finalI + 1] != null) {
                allEditTexts[i].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT && finalI < allEditTexts.length - 1) {
                            allEditTexts[finalI + 1].setEnabled(true);
                            allEditTexts[finalI + 1].requestFocus();
                            allEditTexts[finalI + 1].setCursorVisible(true);
                            allEditTexts[finalI + 1].setText("");
                            allEditTexts[finalI + 1].setHint("");
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
    }

    private boolean existProfilePicture() {
        return ToontaSharedPreferences.toontaSharedPreferences.profilePicPath != null
                && !ToontaSharedPreferences.toontaSharedPreferences.profilePicPath.isEmpty();
    }

    private int getPosFromToontaProfessionByString(String profession) {
        String[] professions = getResources().getStringArray(R.array.toonta_profession_type);
        for (int i = 0; i < professions.length; i++) {
            if (professions[i].equals(profession)) {
                return i;
            }
        }
        return 0;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            toontaUserBDyear = selectedYear;
            toontaUserBDmonth = selectedMonth + 1;
            toontaUserBDday = selectedDay;

            // set selected date into textview
            birthDate.setText(new StringBuilder()
                    .append(toontaUserBDyear).append("-")
                    .append(toontaUserBDmonth).append("-")
                    .append(toontaUserBDday));
        }
    };
}
