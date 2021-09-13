package com.liveonsolutions.binance.Activity;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.liveonsolutions.binance.Adapter.BoxAdapter;
import com.liveonsolutions.binance.Adapter.MyBoxAdapter;
import com.liveonsolutions.binance.Adapter.StatementAdapter;
import com.liveonsolutions.binance.ApiService.Api;
import com.liveonsolutions.binance.ApiService.BoxResponse;
import com.liveonsolutions.binance.ApiService.MyBoxResponse;
import com.liveonsolutions.binance.ApiService.ResponseListener;
import com.liveonsolutions.binance.Dialog.LoadingDialog;
import com.liveonsolutions.binance.Dialog.SuccessLoadingDialog;
import com.liveonsolutions.binance.PrefUtil;
import com.liveonsolutions.binance.R;

import java.util.List;

public class StatementActivity extends AppCompatActivity implements StatementAdapter.OnItemClickListener {

    private FragmentManager manager;
    private Fragment fragment;
    private LoadingDialog loadingDialog;
    private RecyclerView mRecyclerView;
    private StatementAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        initialize();
        loadData();
    }

    private void initialize() {
        mRecyclerView = findViewById(R.id.recycler);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void loadData() {
        List<MyBoxResponse> boxResponses =
                PrefUtil.getToPrefs(this, PrefUtil.PREFS_STATEMENT, MyBoxResponse[].class);

        if (boxResponses.isEmpty())
            showLoadingDialog();
        else {
            mAdapter = new StatementAdapter(StatementActivity.this, boxResponses);
            mRecyclerView.setAdapter(mAdapter);
        }

        Api.getInstance(this).getMyBox(new ResponseListener() {
            @Override
            public void onSuccess(Object o) {
                if (o instanceof List) {
                    mAdapter = new StatementAdapter(StatementActivity.this, (List<MyBoxResponse>) o);
                    mRecyclerView.setAdapter(mAdapter);

                    if (boxResponses.isEmpty())
                        loadingDialog.dismiss();

                    PrefUtil.saveToPrefs(StatementActivity.this, PrefUtil.PREFS_STATEMENT, (List<MyBoxResponse>) o);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                loadingDialog.dismiss();
            }
        });
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

    @Override
    public void OnBoxInteraction(MyBoxResponse box) {
        Toast.makeText(this, box.getId(), Toast.LENGTH_SHORT).show();
    }
}