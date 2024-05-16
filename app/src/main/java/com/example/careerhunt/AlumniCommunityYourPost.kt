import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.AlumniCommunityAdd
import com.example.careerhunt.R
import com.example.careerhunt.data.Alumni
import com.example.careerhunt.dataAdapter.AlumniCommunityYourPost_adapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AlumniCommunityYourPost : Fragment() {

    private lateinit var dbRefAlumni : DatabaseReference
    private lateinit var alumniList : ArrayList<Alumni>
    private lateinit var recyclerView : RecyclerView

    private lateinit var sharedIDPreferences : SharedPreferences
    private lateinit var currentLoginPersonalId : String
    private lateinit var noRecordBox : ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alumni_community_your_post, container, false)
        val imgBtnBack : ImageButton = view.findViewById(R.id.imgBtnBack)
        val btnAddNow : Button = view.findViewById(R.id.btnAddNow)
        dbRefAlumni = FirebaseDatabase.getInstance("https://careerhunt-e6787-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Alumni")

        sharedIDPreferences = requireContext().getSharedPreferences("userid", Context.MODE_PRIVATE)
        currentLoginPersonalId = sharedIDPreferences.getString("userid", "") ?: ""

        recyclerView = view.findViewById(R.id.alumni_your_post_recycle_view)
        alumniList = arrayListOf<Alumni>()
        fetchData()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        noRecordBox = view.findViewById(R.id.noRecordBox)

        imgBtnBack.setOnClickListener(){
            val fragment = com.example.careerhunt.Alumni()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        btnAddNow.setOnClickListener(){
            val fragment = AlumniCommunityAdd()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction?.replace(R.id.frameLayout, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }


    private fun fetchData(){
        dbRefAlumni.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                alumniList.clear()
                if(snapshot.exists()) {
                    for (alumniSnap in snapshot.children) {
                        val alumni = alumniSnap.getValue(Alumni::class.java)

                        //only get user's alumni post
                        if(alumni!!.personal_id == currentLoginPersonalId){
                            alumni?.id = alumniSnap.key.toString()
                            alumniList.add(alumni!!)
                        }
                    }
                    val adapter = AlumniCommunityYourPost_adapter(requireContext())
                    adapter.setData(alumniList)
                    recyclerView.adapter = adapter
                    checkEmptyList(recyclerView.adapter as AlumniCommunityYourPost_adapter)

                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun checkEmptyList(adapter : AlumniCommunityYourPost_adapter) {
        Log.d("item list in adapter = ", adapter.itemCount.toString())
        if (adapter.itemCount == 0) {
            // If RecyclerView is empty, show the button
            noRecordBox.visibility = View.VISIBLE
        } else {
            // If RecyclerView has items, hide the button
            noRecordBox.visibility = View.GONE
        }
    }
}