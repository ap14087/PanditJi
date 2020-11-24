package fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amanpandey.panditji.R;

import poojaSamagri.bhumiPooja;
import poojaSamagri.ganeshPooja;
import poojaSamagri.grihaPooja;
import poojaSamagri.laxmiPooja;
import poojaSamagri.satyanarayanPooja;
import poojaSamagri.shadiPooja;

public class SamagriFragment extends Fragment {

    CardView griha,ganesh,laxmi,satyanarayan,shadi,bhumi;

    public SamagriFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_samagri, container, false);

        griha = view.findViewById(R.id.griha);
        ganesh = view.findViewById(R.id.ganesh);
        laxmi = view.findViewById(R.id.laxmi);
        satyanarayan = view.findViewById(R.id.Satyanarayan);
        shadi = view.findViewById(R.id.shadi);
        bhumi = view.findViewById(R.id.bhumi);

        griha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "You just Clicked griha pooja", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity().getApplicationContext(), grihaPooja.class));
            }
        });

        ganesh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "You just Clicked ganesh pooja", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity().getApplicationContext(), ganeshPooja.class));
            }
        });

        laxmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "You just Clicked laxmi pooja", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity().getApplicationContext(), laxmiPooja.class));
            }
        });

        satyanarayan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "You just Clicked satyanarayan pooja", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity().getApplicationContext(), satyanarayanPooja.class));
            }
        });

        shadi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "You just Clicked shadi pooja", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity().getApplicationContext(), shadiPooja.class));
            }
        });

        bhumi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "You just Clicked bhumi pooja", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity().getApplicationContext(), bhumiPooja.class));
            }
        });

        return view;
    }


}