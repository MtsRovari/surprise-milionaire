package com.liveonsolutions.binance.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liveonsolutions.binance.Adapter.BoxAdapter;
import com.liveonsolutions.binance.ApiService.Api;
import com.liveonsolutions.binance.ApiService.BoxResponse;
import com.liveonsolutions.binance.ApiService.ResponseListener;
import com.liveonsolutions.binance.Dialog.WarningDialog;
import com.liveonsolutions.binance.Dialog.LoadingDialog;
import com.liveonsolutions.binance.PrefUtil;
import com.liveonsolutions.binance.R;
import com.liveonsolutions.binance.Dialog.SuccessLoadingDialog;

import java.util.List;

import static com.liveonsolutions.binance.Activity.MainActivity.ARG_RELOAD_LIST;

public class BoxActivity extends AppCompatActivity implements SuccessLoadingDialog.OnDialogInteraction,
        BoxAdapter.OnItemClickListener, WarningDialog.OnDialogInteraction {

    private FragmentManager manager;
    private Fragment fragment;
    private SuccessLoadingDialog successLoadingDialog;
    private LoadingDialog loadingDialog;
    private WarningDialog warningDialog;
    private BoxAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        initialize();
        loadData();
    }

    private void initialize() {
        mRecyclerView = findViewById(R.id.recycler);

        GridLayoutManager lnlGrid = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(lnlGrid);
    }

    private void loadData() {
        List<BoxResponse> boxResponses =
                PrefUtil.getToPrefs(this, PrefUtil.PREFS_BOX_LIST, BoxResponse[].class);

        if (boxResponses.isEmpty())
            showLoadingDialog();
        else {
            mAdapter = new BoxAdapter(BoxActivity.this, boxResponses);
            mRecyclerView.setAdapter(mAdapter);
        }

        Api.getInstance(this).getBox(new ResponseListener() {
            @Override
            public void onSuccess(Object o) {
                if (o instanceof List) {
                    mAdapter = new BoxAdapter(BoxActivity.this, (List<BoxResponse>) o);
                    mRecyclerView.setAdapter(mAdapter);

                    if (boxResponses.isEmpty())
                        loadingDialog.dismiss();

                    PrefUtil.saveToPrefs(BoxActivity.this, PrefUtil.PREFS_BOX_LIST, (List<BoxResponse>) o);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                loadingDialog.dismiss();
            }
        });
    }

    /**
     * call success loading dialog
     */
    private void showSuccessLoadingDialog() {
        manager = getSupportFragmentManager();
        // close existing dialog fragments
        fragment = manager.findFragmentByTag("request_success_loading_dialog");
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commit();
        }    // Supply num input as an argument.

        successLoadingDialog = new SuccessLoadingDialog();
        successLoadingDialog.show(manager, "request_success_loading_dialog");
    }

    /**
     * call loading dialog
     */
    private void showLoadingDialog() {
        manager = getSupportFragmentManager();
        // close existing dialog fragments
        fragment = manager.findFragmentByTag("request_loading_dialog");
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commit();
        }    // Supply num input as an argument.

        loadingDialog = new LoadingDialog();
        loadingDialog.show(manager, "request_loading_dialog");
    }

    /**
     * call warning dialog
     */
    private void showWarningDialog(String boxId) {
        manager = getSupportFragmentManager();
        // close existing dialog fragments
        fragment = manager.findFragmentByTag("request_warning_dialog");
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commit();
        }    // Supply num input as an argument.

        Bundle args = new Bundle();
        args.putString("boxId", boxId);

        warningDialog = new WarningDialog();
        warningDialog.setArguments(args);
        warningDialog.show(manager, "request_warning_dialog");
    }

    @Override
    public void OnSuccessAnimationEnd() {
        successLoadingDialog.dismiss();

        Intent intent = new Intent(BoxActivity.this, MainActivity.class);
        intent.putExtra(ARG_RELOAD_LIST, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void OnBoxInteraction(BoxResponse box) {
        showWarningDialog(box.getBoxId());
    }

    @Override
    public void onCancelInteraction() {
        warningDialog.dismiss();
    }

    @Override
    public void onConfirmInteraction(String boxId) {
        warningDialog.dismiss();

        showLoadingDialog();

        Api.getInstance(this).requestOrderToBuy(boxId, new ResponseListener() {
            @Override
            public void onSuccess(Object o) {
                loadingDialog.dismiss();
                showSuccessLoadingDialog();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(BoxActivity.this, "Error", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }
}