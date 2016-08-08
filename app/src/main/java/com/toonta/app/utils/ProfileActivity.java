package com.toonta.app.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
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
    private EditText residencePlace;
    private EditText cumulatedPoint;
    private EditText labelThree;

    // Other infos that needs to be sent when updating user's info
    private String sexe = "";

    // Indicates that some fields have been modified
    private boolean updatesAvailable = false;

    private EditText[] allEditTexts = new EditText[7];

    private final int RESULT_LOAD_IMAGE = 100;
    private ImageView profilePic;

    // Bithdate
    private int toontaUserBDyear;
    private int toontaUserBDmonth;
    private int toontaUserBDday;
    static final int DATE_DIALOG_ID = 999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupActionBar();

        /*ImageView profileButtonUp = (ImageView) findViewById(R.id.profile_button_up);
        assert profileButtonUp != null;
        profileButtonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ProfileActivity.this);
            }
        });*/

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

        residencePlace = (EditText) findViewById(R.id.residence_place);
        assert residencePlace != null;
        allEditTexts[4] = residencePlace;

        cumulatedPoint = (EditText) findViewById(R.id.toonta_cumilated_points);
        assert cumulatedPoint != null;
        allEditTexts[5] = cumulatedPoint;

        labelThree = (EditText) findViewById(R.id.label_three);
        assert labelThree != null;
        allEditTexts[6] = labelThree;

        addOnEditorActionListener();

        profilePic = (ImageView) findViewById(R.id.toonta_profile_pix);
        assert profilePic != null;
        // Setting profile picture
        if (existProfilePicture()) {
            profilePic.setImageBitmap(BitmapFactory.decodeFile(ToontaSharedPreferences.toontaSharedPreferences.profilePicPath));
        }
        // profilePic.setImageBitmap(BitmapFactory.decodeFile(ToontaSharedPreferences.toontaSharedPreferences.profilePicPath));
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
            }

            @Override
            public void onToontaUserUpdate(String responseStatus) {
                Snackbar.make(findViewById(android.R.id.content), responseStatus, Snackbar.LENGTH_LONG).show();
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
                updatedUser = new ToontaUser(
                        birthDate.getText().toString(),
                        emailAddress.getText().toString(),
                        firstName.getText().toString(),
                        "",
                        lastName.getText().toString(),
                        "",
                        phoneNumber.getText().toString(),
                        String.valueOf(professionalActivity.getSelectedItem()),
                        sexe);
                if (cumulatedPoint.getText().toString() != null) {
                    updatedUser.bank_.balance = Integer.parseInt(cumulatedPoint.getText().toString());
                }
                if (residencePlace.getText().toString() != null) {
                    updatedUser.address.city = residencePlace.getText().toString();
                }

                // Sending changes to server
                toontaUserInterceptor.updateToontaUser(updatedUser);
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
            LayoutInflater mInflater = LayoutInflater.from(this);

            View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
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
        ToontaSharedPreferences.setToontaProfilePicPath(path);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        toontaUserBDyear, toontaUserBDmonth,toontaUserBDday);
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

    public void setEditable(View view) {
        /*int saveChangesLayoutVisibility =  saveChangesLayout.getVisibility();
        if (saveChangesLayoutVisibility == View.INVISIBLE) {
            saveChangesLayout.setVisibility(View.VISIBLE);
        }

        int viewId = view.getId();*/

        /*switch (viewId) {
            case R.id._email_address :
                emailAddress.setEnabled(true);
                emailAddress.requestFocus();
                emailAddress.setCursorVisible(true);
                emailAddress.setText("");
                emailAddress.setHint("");
                break;
            case R.id._phone_number :
                phoneNumber.setEnabled(true);
                phoneNumber.requestFocus();
                phoneNumber.setCursorVisible(true);
                phoneNumber.setText("");
                phoneNumber.setHint("");
                break;
            case R.id._professional_activity :
                professionalActivity.setEnabled(true);
                professionalActivity.requestFocus();
                professionalActivity.setCursorVisible(true);
                professionalActivity.setText("");
                professionalActivity.setHint("");
                break;
            case R.id._residence_place :
                residencePlace.setEnabled(true);
                residencePlace.requestFocus();
                residencePlace.setCursorVisible(true);
                residencePlace.setText("");
                residencePlace.setHint("");
                break;
            case R.id._toonta_cumilated_points :
                cumulatedPoint.setEnabled(true);
                cumulatedPoint.requestFocus();
                cumulatedPoint.setCursorVisible(true);
                cumulatedPoint.setText("");
                cumulatedPoint.setHint("");
                break;
            case R.id._label_three :
                labelThree.setEnabled(true);
                labelThree.requestFocus();
                labelThree.setCursorVisible(true);
                labelThree.setText("");
                labelThree.setHint("");
                break;
        }*/
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
        residencePlace.setText(toontaUser.address.city);
        cumulatedPoint.setText(Integer.toString(toontaUser.bank_.balance));
        labelThree.setText(toontaUser.lastname);

        if (toontaUser.birthdate != null && !toontaUser.birthdate.isEmpty()) {
            String[] splittedDate = toontaUser.birthdate.split("-");
            toontaUserBDyear = Integer.parseInt(splittedDate[0]);
            toontaUserBDmonth = Integer.parseInt(splittedDate[1]);
            toontaUserBDday = Integer.parseInt(splittedDate[2]);
        }

        sexe = toontaUser.sexe;
    }

    private void addOnEditorActionListener() {
        for (int i = 0; i < allEditTexts.length; i++) {
            final int finalI = i;
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
