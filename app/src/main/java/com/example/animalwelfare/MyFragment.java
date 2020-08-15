package com.example.animalwelfare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;




public class MyFragment extends Fragment {

    private static final String EXTRA_TITLE_MESSAGE = "EXTRA_TITLE_MESSAGE";
    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private static final String IMAGE_MESSAGE = "IMAGE_MESSAGE";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public static final MyFragment newInstance(String title,String message, int img){
        MyFragment fragment = new MyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TITLE_MESSAGE,title);
        bundle.putString(EXTRA_MESSAGE, message);
        bundle.putInt(IMAGE_MESSAGE, img);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String title = getArguments().getString(EXTRA_TITLE_MESSAGE);

        String message = getArguments().getString(EXTRA_MESSAGE);

        View v = inflater.inflate(R.layout.fragment_my, container, false);

        TextView titleTextView = (TextView) v.findViewById(R.id.titletextView);

        titleTextView.setText(title);

        TextView messageTextView = (TextView) v.findViewById(R.id.messagetextView);

        messageTextView.setText(message);

        int img = getArguments().getInt(IMAGE_MESSAGE);

        ImageView image = (ImageView) v.findViewById(R.id.imageView);

        image.setImageResource(img);

        return v;
    }


}
