package fr.sapk.twitterlike.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import fr.sapk.twitterlike.R;
import fr.sapk.twitterlike.api.Api;
import fr.sapk.twitterlike.api.message.WriteResponse;
import fr.sapk.twitterlike.session.Session;

/**
 * The type Write msg dialog.
 */
public class WriteMsgDialog extends DialogFragment {

    private EditText message;

    /**
     * Gets instance.
     *
     * @param token  the token
     * @param userId the user id
     * @return the instance
     */
    public static WriteMsgDialog getInstance(final String token, final String userId) {
        WriteMsgDialog f = new WriteMsgDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("token", token);
        args.putString("user_id", userId);
        f.setArguments(args);
        return f;
    }


    /**
     * On create dialog dialog.
     *
     * @param savedInstanceState the saved instance state
     * @return the dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_msg, null);
        message = (EditText) view.findViewById(R.id.tchat_msg);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        closeKeyboard();
                        if (!message.getText().toString().isEmpty()) {
                            //post message
                            new SendMessageAsyncTask(view.getContext()).execute(message.getText().toString());
                        } else {
                            message.setError(WriteMsgDialog.this.getActivity()
                                    .getString(R.string.error_missing_msg));
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        closeKeyboard();
                        WriteMsgDialog.this.dismiss();
                    }
                }

        );
        return builder.create();
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(message.getWindowToken(), 0);
    }

    /**
     * The type Send message async task.
     */
    protected class SendMessageAsyncTask extends AsyncTask<String, Void, Boolean> {

        Context context;
        private WriteResponse response = null;

        /**
         * Instantiates a new Send message async task.
         *
         * @param context the context
         */
        SendMessageAsyncTask(final Context context) {
            this.context = context;
        }

        /**
         * Do in background integer.
         *
         * @param params the params
         * @return the integer
         */
        @Override
        protected Boolean doInBackground(String... params) {


            if (!Api.isAvailable(context)) {
                return null;
            }
            try {
                response = Api.Write(Session.userId,params[0],Session.token);
                return response.isOk();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * On post execute.
         *
         * @param success the success status
         */
        @Override
        public void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(context, context.getString(R.string.error_send_msg), Toast.LENGTH_SHORT).show();
            }else {
                WriteMsgDialog.this.dismiss();
            }
        }
    }
}
