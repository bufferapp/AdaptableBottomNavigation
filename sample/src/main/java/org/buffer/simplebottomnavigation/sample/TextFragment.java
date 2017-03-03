package org.buffer.simplebottomnavigation.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextFragment extends Fragment {

    private static String ARG_TEXT = "ARG_TEXT";
    private String content;

    public static TextFragment newInstance(String text) {
        TextFragment textFragment = new TextFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        textFragment.setArguments(bundle);
        return textFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = getArguments().getString(ARG_TEXT, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_text, container, false);

        TextView contentText = (TextView) fragmentView.findViewById(R.id.text_content);
        contentText.setText(content);

        return fragmentView;
    }
}