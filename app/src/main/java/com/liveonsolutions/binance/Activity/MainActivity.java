package com.liveonsolutions.binance.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.liveonsolutions.binance.Adapter.OrderDetailsAdapter;
import com.liveonsolutions.binance.ApiService.AccountResponse;
import com.liveonsolutions.binance.ApiService.Api;
import com.liveonsolutions.binance.ApiService.BoxResponse;
import com.liveonsolutions.binance.ApiService.MyBoxResponse;
import com.liveonsolutions.binance.ApiService.OrderDetailsResponse;
import com.liveonsolutions.binance.ApiService.ResponseListener;
import com.liveonsolutions.binance.Adapter.MyBoxAdapter;
import com.liveonsolutions.binance.Dialog.LoadingDialog;
import com.liveonsolutions.binance.Dialog.OpenBoxDialog;
import com.liveonsolutions.binance.PrefUtil;
import com.liveonsolutions.binance.R;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyBoxAdapter.OnItemClickListener,
        OpenBoxDialog.OnDialogInteraction {

    private RelativeLayout lnlBox, lnlOpenBox, lnlStatement, lnlBalanceVisibility;
    private TextView tWalletBalance, tTitle;
    private View viewBalance;
    private ImageView icClosedEye, icOpenedEye;
    private MyBoxAdapter mAdapter;
    private OrderDetailsAdapter mOrderDetailsAdapter;
    private RecyclerView mRecyclerView;

    private FragmentManager manager;
    private Fragment fragment;
    private LoadingDialog loadingDialog;
    private OpenBoxDialog openBoxDialog;

    private View bottom_sheet, viewDialog;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomDialog;

    public static String ARG_RELOAD_LIST = "ARG_RELOAD_LIST";
    private boolean isToReload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        setupWidgets();
        loadData();
        loadAccount();
    }

    private void initialize() {
        lnlBox = findViewById(R.id.lnl_box);
        lnlStatement = findViewById(R.id.lnl_statement);
        lnlOpenBox = findViewById(R.id.lnl_open_box);
        tWalletBalance = findViewById(R.id.t_balance);
        lnlBalanceVisibility = findViewById(R.id.lnl_balance_visibility);
        viewBalance = findViewById(R.id.view_balance);
        icClosedEye = findViewById(R.id.ic_closed_eye);
        icOpenedEye = findViewById(R.id.ic_opened_eye);
        mRecyclerView = findViewById(R.id.recycler);
        tTitle = findViewById(R.id.t_title);

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        GridLayoutManager lnlGrid = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(lnlGrid);

        isToReload = getIntent().getBooleanExtra(ARG_RELOAD_LIST, false);
    }

    private void setupWidgets() {
        lnlBox.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BoxActivity.class));
        });

        lnlStatement.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, StatementActivity.class));
        });

        lnlOpenBox.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, OpenBoxActivity.class));
        });

        lnlBalanceVisibility.setOnClickListener(v -> {
            if (tWalletBalance.getVisibility() == View.VISIBLE) {
                tWalletBalance.setVisibility(View.GONE);
                viewBalance.setVisibility(View.VISIBLE);
                icClosedEye.setVisibility(View.GONE);
                icOpenedEye.setVisibility(View.VISIBLE);
            } else {
                tWalletBalance.setVisibility(View.VISIBLE);
                viewBalance.setVisibility(View.GONE);
                icClosedEye.setVisibility(View.VISIBLE);
                icOpenedEye.setVisibility(View.GONE);
            }
        });
    }

    private void loadAccount() {
        if (loadingDialog == null)
            showLoadingDialog();

        Api.getInstance(this).getAccount(new ResponseListener() {
            @Override
            public void onSuccess(Object o) {
                if (loadingDialog != null)
                    loadingDialog.dismiss();

                if (o instanceof AccountResponse) {
                    tWalletBalance.setText(((AccountResponse) o).getAmountTotal());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (loadingDialog != null)
                    loadingDialog.dismiss();

                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        List<MyBoxResponse> boxResponses =
                PrefUtil.getToPrefs(this, PrefUtil.PREFS_BOX_LIST, MyBoxResponse[].class);

        if (boxResponses.isEmpty() || isToReload) {
            showLoadingDialog();
            lnlBox.setVisibility(View.GONE);
            tTitle.setVisibility(View.GONE);
            lnlStatement.setVisibility(View.GONE);
            tWalletBalance.setVisibility(View.GONE);
            viewBalance.setVisibility(View.VISIBLE);
            icClosedEye.setVisibility(View.GONE);
            icOpenedEye.setVisibility(View.VISIBLE);
        } else {
            mAdapter = new MyBoxAdapter(MainActivity.this, boxResponses);
            mRecyclerView.setAdapter(mAdapter);
        }

        Api.getInstance(this).getMyBox(new ResponseListener() {
            @Override
            public void onSuccess(Object o) {
                if (o instanceof List) {

                    mAdapter = new MyBoxAdapter(MainActivity.this, (List<MyBoxResponse>) o);
                    mRecyclerView.setAdapter(mAdapter);

                    if (boxResponses.isEmpty() || isToReload) {
                        loadingDialog.dismiss();
                        lnlBox.setVisibility(View.VISIBLE);
                        tTitle.setVisibility(View.VISIBLE);
                        lnlStatement.setVisibility(View.VISIBLE);
                        tWalletBalance.setVisibility(View.VISIBLE);
                        viewBalance.setVisibility(View.GONE);
                        icClosedEye.setVisibility(View.VISIBLE);
                        icOpenedEye.setVisibility(View.GONE);
                    }

                    PrefUtil.saveToPrefs(MainActivity.this, PrefUtil.PREFS_BOX_LIST, (List<MyBoxResponse>) o);

                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (boxResponses.isEmpty() || isToReload)
                    loadingDialog.dismiss();
            }
        });
    }

    @Override
    public void OnBoxInteraction(MyBoxResponse box) {
        if (box.isUnlocked())
            showOpenBoxDialog(box.getId());
    }

    @Override
    public void OnAnimationEnd(String boxId) {
        openBoxDialog.dismiss();

        showBottomDialog(boxId);
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
     * call open box dialog
     */
    private void showOpenBoxDialog(String boxId) {
        manager = getSupportFragmentManager();
        // close existing dialog fragments
        fragment = manager.findFragmentByTag("request_open_box_dialog");
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commit();
        }    // Supply num input as an argument.

        Bundle args = new Bundle();
        args.putString("boxId", boxId);
        openBoxDialog = new OpenBoxDialog();
        openBoxDialog.setArguments(args);
        openBoxDialog.show(manager, "request_open_box_dialog");
    }

    private void showBottomDialog(String orderId) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        viewDialog = getLayoutInflater().inflate(R.layout.bottom_box_details, null);

        TextView tAmountTotal = viewDialog.findViewById(R.id.t_amount_total);
        TextView tPercentTotal = viewDialog.findViewById(R.id.t_percent_total);
        LottieAnimationView lottieAnimationView = viewDialog.findViewById(R.id.animation_lottie);
        LottieAnimationView loadingAnimation = viewDialog.findViewById(R.id.loading_animation);
        LinearLayout lnlHeader = viewDialog.findViewById(R.id.lnl_header);
        RecyclerView orderDetailsRecycler = viewDialog.findViewById(R.id.recycler);
        orderDetailsRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        loadOrderDetails(orderId, orderDetailsRecycler, tAmountTotal, tPercentTotal, lottieAnimationView,
                loadingAnimation, lnlHeader);

        bottomDialogSetup(viewDialog);
    }

    private void loadOrderDetails(String orderId, RecyclerView recyclerView, TextView tAmountTotal,
                                  TextView tPercentTotal, LottieAnimationView lottieAnimationView,
                                  LottieAnimationView loading, LinearLayout lnlHeader) {
        Api.getInstance(this).getOrderDetails(orderId, new ResponseListener() {
            @Override
            public void onSuccess(Object o) {
                loading.setVisibility(View.GONE);
                lnlHeader.setVisibility(View.VISIBLE);

                if (o instanceof OrderDetailsResponse) {
                    mOrderDetailsAdapter = new OrderDetailsAdapter(MainActivity.this, ((OrderDetailsResponse) o).getOrders());
                    recyclerView.setAdapter(mOrderDetailsAdapter);

                    if (((OrderDetailsResponse) o).getPercentTotal().contains("-")) {
                        tPercentTotal.setText(((OrderDetailsResponse) o).getPercentTotal() + "%");
                        tPercentTotal.setTextColor(getResources().getColor(R.color.graphDownColor));
                        lottieAnimationView.setAnimation(R.raw.graph_down);
                        lottieAnimationView.playAnimation();
                    } else {
                        tPercentTotal.setText(((OrderDetailsResponse) o).getPercentTotal().replace("-", "") + "%");
                        tPercentTotal.setTextColor(getResources().getColor(R.color.graphUpColor));
                        lottieAnimationView.setAnimation(R.raw.graph_up);
                        lottieAnimationView.playAnimation();
                        lottieAnimationView.loop(true);
                    }

                    tAmountTotal.setText("BTC " + ((OrderDetailsResponse) o).getAmountTotal());
                }

            }

            @Override
            public void onFailure(String errorMessage) {
                loading.setVisibility(View.GONE);
                lnlHeader.setVisibility(View.VISIBLE);

                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bottomDialogSetup(View view) {
        mBottomDialog = new BottomSheetDialog(this, R.style.DialogStyle);
        mBottomDialog.setContentView(view);
        mBottomDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mBottomDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomDialog.show();
        mBottomDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomDialog = null;

                loadAccount();
                loadData();
            }
        });
    }
}