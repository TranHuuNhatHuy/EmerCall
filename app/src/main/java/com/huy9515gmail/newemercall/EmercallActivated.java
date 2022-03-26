package com.huy9515gmail.newemercall;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmercallActivated extends AppCompatActivity {

    public final String EMERACTIVECASELIST = "EMERACTIVECASELIST";
    public final String DATABASE = "DATABASE";
    public String deviceID;

    final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    final DatabaseReference userStatus = FirebaseDatabase.getInstance().getReference("userStatus");

    @BindView(R.id.toolbar_main)
    Toolbar myToolbar;

    @BindView(R.id.btnAffirmative)
    Button btnAffirmative;

    @BindView(R.id.lvEmerdict)
    ListView lvEmerdict;

    @BindView(R.id.spnGender)
    Spinner spnGender;

    @BindView(R.id.edtAge)
    EditText edtAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emercall_activated_1);
        ButterKnife.bind(this);

        //getting device id
        deviceID = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        //setting up toolbar
        myToolbar.setTitle("EmerCall (đang kích hoạt)");
        myToolbar.setTitleTextColor(Color.WHITE);

        //initiating gender spinner
        List<String> gender = new ArrayList<>();
        gender.add("Nam"); gender.add("Nữ");
        ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(this, R.layout.spinner_text, gender);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spnGender.setAdapter(langAdapter);

        //initiating listview data
        final ArrayList<String> database = new ArrayList<>();

        BufferedReader input = null;
        try {
            String filenameTXT = "EmergencyCase.txt";

            InputStream iS = getResources().getAssets().open(filenameTXT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(iS));
            String sCurrentLine;

            while ((sCurrentLine = reader.readLine()) != null) {
                database.add(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null){
                    input.close();
                }
            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }


        //initiating listview
        final CustomAdapterCheckbox customAdapterCheckBox = new CustomAdapterCheckbox(this, R.layout.row_listview_emercallactivated, database);
        lvEmerdict.setAdapter(customAdapterCheckBox);

        //affirmative button click event
        btnAffirmative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stringAge = edtAge.getText().toString();
                String stringGender = spnGender.getSelectedItem().toString();
                int stt = 0;
                for (int i=0; i <= database.size(); i++ )
                    if (customAdapterCheckBox.mCheckStates.get(i) == true) stt = stt + 1;

                if ((!(stringAge.equals(""))) && (stt > 0)) {

                    //initiating Firebase database
                    DatabaseReference thisUser = users.child(deviceID);
                    DatabaseReference thisPatient = thisUser.child("patient");
                    DatabaseReference thisUserStatus = userStatus.child(deviceID);
                    DatabaseReference thisStatus = thisUser.child("status"); //inner status

                    //sending patient gender and age to Firebase database
                    DatabaseReference thisPatientAge = thisPatient.child("age");
                    thisPatientAge.setValue(stringAge);
                    DatabaseReference thisPatientGender = thisPatient.child("gender");
                    thisPatientGender.setValue(stringGender);
                    DatabaseReference thisPatientSymptoms = thisPatient.child("symptoms");

                    Intent intent = new Intent(EmercallActivated.this, EmeractiveDict.class);
                    ArrayList<String> emeractiveCaseList = new ArrayList<>();
                    for (int i = 0; i <= database.size(); i++) {
                        if (customAdapterCheckBox.mCheckStates.get(i) == true) {
                            //add symptoms to list which will be sent to EmeractiveDict
                            emeractiveCaseList.add(database.get(i).toString());
                            //add symptoms to Firebase database
                            DatabaseReference thisSymptom = thisPatientSymptoms.child(database.get(i).toString());
                            thisSymptom.setValue("");
                            stt = stt + 1;
                        }
                    }

                    //increase call count
                    final DatabaseReference thisUserCall = thisUser.child("callCount").child("call");
                    thisUserCall.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int callCount = new Integer(dataSnapshot.getValue(Integer.class));
                            callCount = callCount + 1;
                            thisUserCall.setValue(callCount);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                    //active user status
                    thisUserStatus.setValue(1);
                    thisStatus.setValue(1); //inner status

                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(EMERACTIVECASELIST, emeractiveCaseList);
                    bundle.putStringArrayList(DATABASE, database);
                    bundle.putString("from", "EmercallActivated");
                    intent.putExtras(bundle);

                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(EmercallActivated.this, "Please enter all victim information: gender, age, symptoms and signs!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //highlight chosen symptoms
        lvEmerdict.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox cBox = view.findViewById(R.id.cbHeader);
                cBox.toggle();
            }
        });

    }

}
