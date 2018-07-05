package com.example.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.R;

public class SheetDialogFragment extends BottomSheetDialogFragment {
	private static final String ARGUMENT_EXAMPLE = "example";
	private static final String SAVED_USERNAME = "username";
	private static final String SAVED_PASSWORD = "password";

	private String mExample;
	private View mRootView;
	private BottomSheetBehavior mBehavior;
	private String mUsername = "";
	private String mPassword = "";
	private SheetDialogListener mListener;

	public interface SheetDialogListener {
		void onSheetDialogStateChanged(DialogFragment dialog, int newState, String username, String password);
		void onSheetDialogDismiss(DialogFragment dialog, String username, String password);
	}

	public static SheetDialogFragment newInstance(String example) {
		SheetDialogFragment fragment = new SheetDialogFragment();

		// arguments
		Bundle arguments = new Bundle();
		arguments.putString(ARGUMENT_EXAMPLE, example);
		fragment.setArguments(arguments);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);

		// handle fragment arguments
		Bundle arguments = getArguments();
		if (arguments != null) {
			handleArguments(arguments);
		}

		// set callback listener
		try {
			mListener = (SheetDialogListener) getTargetFragment();
		} catch (ClassCastException e) {
			throw new ClassCastException(getTargetFragment().toString() + " must implement " + SheetDialogListener.class.getName());
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// cancelable on touch outside
		if (getDialog() != null) getDialog().setCanceledOnTouchOutside(true);

		// restore saved state
		if (savedInstanceState != null) {
			handleSavedInstanceState(savedInstanceState);
		}
	}

	@Override
	public void onDestroyView() {
		// http://code.google.com/p/android/issues/detail?id=17423
		if (getDialog() != null && getRetainInstance()) getDialog().setDismissMessage(null);
		super.onDestroyView();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// inflate layout
		LayoutInflater inflater = getActivity().getLayoutInflater();
		mRootView = inflater.inflate(R.layout.dialog_custom, null);

		// create dialog
		final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
		dialog.setContentView(mRootView);

		// set arguments
		EditText usernameEditText = mRootView.findViewById(R.id.dialog_custom_username);
		usernameEditText.setHint(mExample);

		// setup behavior
		mBehavior = BottomSheetBehavior.from((View) mRootView.getParent());
		mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				EditText usernameEditText = mRootView.findViewById(R.id.dialog_custom_username);
				EditText passwordEditText = mRootView.findViewById(R.id.dialog_custom_password);

				String username = usernameEditText.getText().toString();
				String password = passwordEditText.getText().toString();

				// TODO: data validation

				mListener.onSheetDialogStateChanged(SheetDialogFragment.this, newState, username, password);
				if (newState == BottomSheetBehavior.STATE_HIDDEN) dialog.dismiss();
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {
				// do nothing
			}
		});

		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		EditText usernameEditText = mRootView.findViewById(R.id.dialog_custom_username);
		EditText passwordEditText = mRootView.findViewById(R.id.dialog_custom_password);

		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		// TODO: data validation

		mListener.onSheetDialogDismiss(SheetDialogFragment.this, username, password);

		// destroy dialog fragment
		if (getFragmentManager() != null) {
			getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// save current instance state
		super.onSaveInstanceState(outState);

		EditText usernameEditText = mRootView.findViewById(R.id.dialog_custom_username);
		EditText passwordEditText = mRootView.findViewById(R.id.dialog_custom_password);

		outState.putString(SAVED_USERNAME, usernameEditText.getText().toString());
		outState.putString(SAVED_PASSWORD, passwordEditText.getText().toString());
	}

	private void handleArguments(Bundle arguments) {
		if (arguments.containsKey(ARGUMENT_EXAMPLE)) {
			mExample = (String) arguments.get(ARGUMENT_EXAMPLE);
		}
	}

	private void handleSavedInstanceState(Bundle savedInstanceState) {
		EditText usernameEditText = mRootView.findViewById(R.id.dialog_custom_username);
		EditText passwordEditText = mRootView.findViewById(R.id.dialog_custom_password);

		if (savedInstanceState.containsKey(SAVED_USERNAME)) {
			usernameEditText.setText((String) savedInstanceState.get(SAVED_USERNAME));
		}

		if (savedInstanceState.containsKey(SAVED_PASSWORD)) {
			passwordEditText.setText((String) savedInstanceState.get(SAVED_PASSWORD));
		}
	}
}
