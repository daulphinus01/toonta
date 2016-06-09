package com.toonta.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toonta.app.model.ToontaQuestion;
import com.toonta.app.utils.ToontaConstants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ToontaQuestionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ToontaQuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToontaQuestionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private ToontaQuestion toontaQuestion;

    private OnFragmentInteractionListener mListener;

    public ToontaQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ToontaQuestionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToontaQuestionFragment newInstance(ToontaQuestion toontaQuestion) {
        ToontaQuestionFragment fragment = new ToontaQuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable(ToontaConstants.TOONTA_QUESTION, toontaQuestion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            toontaQuestion = getArguments().getParcelable(ToontaConstants.TOONTA_QUESTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_toonta_question, container, false);

        container.setBackgroundColor(getResources().getColor(R.color.white_bg));

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams llpTxt = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.2f);
        LinearLayout.LayoutParams llpQstRep = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.8f);

        // Ajout de la question.
        TextView textView = new TextView(getContext());
        textView.setText(toontaQuestion.getQuestion());
        textView.setHeight(22);
        textView.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setGravity(Gravity.CENTER);

        linearLayout.addView(textView, llpTxt);

        switch (toontaQuestion.getQuestionType()) {
            case OPEN_QUESTION:
                EditText editText = new EditText(getContext());
                editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                editText.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                editText.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.addView(editText, llpQstRep);
                break;
            default:
                LinearLayout linearLayoutBoxes = new LinearLayout(getContext());
                linearLayoutBoxes.setOrientation(LinearLayout.VERTICAL);

                for (String answer : toontaQuestion.getResponses()) {
                    CheckBox checkBox = new CheckBox(getContext());
                    checkBox.setText(answer);
                    checkBox.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                    checkBox.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    checkBox.setId(answer.hashCode());
                    linearLayoutBoxes.addView(checkBox);
                }

                linearLayout.addView(linearLayoutBoxes, llpQstRep);

                break;
        }

        container.addView(linearLayout);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString()
           //         + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
