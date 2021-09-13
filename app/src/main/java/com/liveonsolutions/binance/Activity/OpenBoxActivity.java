package com.liveonsolutions.binance.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.liveonsolutions.binance.Adapter.BoxAdapter;
import com.liveonsolutions.binance.Adapter.MyBoxAdapter;
import com.liveonsolutions.binance.Adapter.OpenBoxAdapter;
import com.liveonsolutions.binance.Adapter.OrderDetailsAdapter;
import com.liveonsolutions.binance.ApiService.Api;
import com.liveonsolutions.binance.ApiService.BoxResponse;
import com.liveonsolutions.binance.ApiService.MyBoxResponse;
import com.liveonsolutions.binance.ApiService.OrderDetailsResponse;
import com.liveonsolutions.binance.ApiService.ResponseListener;
import com.liveonsolutions.binance.Dialog.LoadingDialog;
import com.liveonsolutions.binance.Dialog.SuccessLoadingDialog;
import com.liveonsolutions.binance.Dialog.WarningDialog;
import com.liveonsolutions.binance.PrefUtil;
import com.liveonsolutions.binance.R;

import java.util.List;

import static com.liveonsolutions.binance.Activity.MainActivity.ARG_RELOAD_LIST;

public class OpenBoxActivity extends AppCompatActivity implements SuccessLoadingDialog.OnDialogInteraction,
        OpenBoxAdapter.OnItemClickListener, WarningDialog.OnDialogInteraction {

    private FragmentManager manager;
    private Fragment fragment;
    private SuccessLoadingDialog successLoadingDialog;
    private LoadingDialog loadingDialog;
    private WarningDialog warningDialog;
    private OrderDetailsAdapter mOrderDetailsAdapter;
    private OpenBoxAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private View bottom_sheet, viewDialog;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_box);

        initialize();
        loadData();
    }

    private void initialize() {
        mRecyclerView = findViewById(R.id.recycler);

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        GridLayoutManager lnlGrid = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(lnlGrid);
    }

    private void loadData() {
        List<MyBoxResponse> boxResponses =
                PrefUtil.getToPrefs(this, PrefUtil.PREFS_OPEN__BOX_LIST, MyBoxResponse[].class);

        if (boxResponses.isEmpty())
            showLoadingDialog();
        else {
            mAdapter = new OpenBoxAdapter(OpenBoxActivity.this, boxResponses);
            mRecyclerView.setAdapter(mAdapter);
        }

        Api.getInstance(this).getOpenBox(new ResponseListener() {
            @Override
            public void onSuccess(Object o) {
                if (o instanceof List) {
                    mAdapter = new OpenBoxAdapter(OpenBoxActivity.this, (List<MyBoxResponse>) o);
                    mRecyclerView.setAdapter(mAdapter);

                    if (boxResponses.isEmpty())
                        loadingDialog.dismiss();

                    PrefUtil.saveToPrefs(OpenBoxActivity.this, PrefUtil.PREFS_OPEN__BOX_LIST, (List<BoxResponse>) o);
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

        Intent intent = new Intent(OpenBoxActivity.this, MainActivity.class);
        intent.putExtra(ARG_RELOAD_LIST, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
                Toast.makeText(OpenBoxActivity.this, "Error", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }

    @Override
    public void OnBoxInteraction(MyBoxResponse box) {
        showBottomDialog(box.getId());
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
                    mOrderDetailsAdapter = new OrderDetailsAdapter(OpenBoxActivity.this, ((OrderDetailsResponse) o).getOrders());
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

                Toast.makeText(OpenBoxActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
            }
        });
    }
}