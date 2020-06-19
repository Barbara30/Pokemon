package com.buddy.scrima;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PokemonListFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_pokemon_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Intent i = getActivity().getIntent();
        i.getType();
        ArrayList<Pokemon> list = (ArrayList<Pokemon>) i.getSerializableExtra("list");
        list = (ArrayList<Pokemon>) getArguments().getSerializable("list");
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(getActivity(), list);
        final ArrayList<Pokemon> finalList = list;
        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(getActivity(), DetailActivity.class);
                String[] separated = finalList.get(position).url.split("/");
                i.putExtra("id", separated[separated.length-1]);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(PokemonListFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }
}