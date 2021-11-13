# BidIT
Pamācības Firebase DB!
(ja kaut kas neiet, rakstiet Justīnei)
1. Lai piekļūtu DB aizsūtīt Justīnei savu gmail!!! Links: https://bidit-36751-default-rtdb.firebaseio.com/
2. Jābūt jaunākajai versijai no master
3. Telefonam (vienalga virtuālajam vai fiziskajam) jābūt pieslēgtam internetam (ar virtuālo šeit var rasties ievērojamas problēmas)
4. Lai ierakstītu datus DB:
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
..
private DatabaseReference database; //ieteicams nodefinēt pašā klases sākumā, lai redzams visās metodēs
..
database = FirebaseDatabase.getInstance().getReference(); 
database.child(branch).child(smaller-branch).setValue(value);//value var būt String vērtība, var būt arī objekts(šajā gadījumā klases vērtībām jābūt public - skat.klasi Product)
5. Lai nolasītu datus no DB (piemērs no mūsu DB):
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
..
private DatabaseReference database; //ieteicams nodefinēt pašā klases sākumā, lai redzams visās metodēs
..
database = FirebaseDatabase.getInstance().getReference("products"); //tiek apskatīta DB products sadaļa 
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    //šeit tālāk srādā ar item, piem.:
                    String name = item.child("name").getValue().toString();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //te var error izmest
            }
        };
        database.addValueEventListener(postListener); //katru reizi, kad tiks veiktas izmaiņas DB, tiks izsauktas šīs metodes


