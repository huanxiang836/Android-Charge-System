package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapter.CategoryAdapter;
import com.example.myapplication.adapter.OrderAdapter;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.databinding.DialogCashPaymentBinding;
import com.example.myapplication.databinding.DialogReceiptPreviewBinding;
import com.example.myapplication.model.OrderItem;
import com.example.myapplication.model.OrderSummary;
import com.example.myapplication.model.PaymentType;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.ProductCategory;
import com.example.myapplication.model.Receipt;
import com.example.myapplication.service.CashierService;
import com.example.myapplication.service.StoreCatalogService;
import com.example.myapplication.util.CurrencyUtils;
import com.example.myapplication.util.ReceiptFormatter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int LIST_SPAN_COUNT = 2;
    private static final int HIGH_VALUE_THRESHOLD = 100;
    private static final String SECTION_ORDER = "order";
    private static final String SECTION_MEMBER = "member";
    private static final String SECTION_MARKETING = "marketing";
    private static final String SECTION_SETTING = "setting";

    private ActivityMainBinding binding;
    private CashierService cashierService;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private OrderAdapter orderAdapter;
    private GridLayoutManager productLayoutManager;
    private String selectedCategoryId;
    private String currentSection = SECTION_ORDER;
    private AlertDialog cashPaymentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupStoreLogo();
        initializeServices();
        setupCategoryList();
        setupProductList();
        setupOrderList();
        setupTopActions();
        setupModuleCards();
        setupSearch();
        loadInitialData();
        updateTopBarClock();
        applyListLayout();
        switchSection(SECTION_ORDER);
        refreshOrderPanel();
    }

    private void setupStoreLogo() {
        binding.storeLogoImage.setBackground(null);
        binding.storeLogoImage.setImageResource(R.drawable.ic_pos_logo);
    }

    private void initializeServices() {
        cashierService = new CashierService(new StoreCatalogService());
    }

    private void setupCategoryList() {
        binding.categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(category -> {
            selectedCategoryId = category.getId();
            categoryAdapter.setSelectedCategoryId(selectedCategoryId);
            refreshProductList();
            binding.categoryTitleText.setText(category.getName());
            binding.productRecyclerView.scrollToPosition(0);
        });
        binding.categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupProductList() {
        productLayoutManager = new GridLayoutManager(this, LIST_SPAN_COUNT);
        binding.productRecyclerView.setLayoutManager(productLayoutManager);
        productAdapter = new ProductAdapter(
                product -> {
                    cashierService.addProduct(product);
                    refreshOrderPanel();
                    Toast.makeText(MainActivity.this,
                            getString(R.string.add_to_cart_feedback, product.getName()),
                            Toast.LENGTH_SHORT).show();
                },
                this::showSpecSelectionDialog
        );
        binding.productRecyclerView.setAdapter(productAdapter);
    }

    private void setupOrderList() {
        binding.orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(
                item -> updateItemQuantity(item, 1),
                item -> updateItemQuantity(item, -1),
                item -> {
                    cashierService.removeProduct(item.getProduct().getId());
                    refreshOrderPanel();
                }
        );
        binding.orderRecyclerView.setAdapter(orderAdapter);
    }

    private void setupTopActions() {
        binding.navOrderButton.setOnClickListener(view -> switchSection(SECTION_ORDER));
        binding.navMemberButton.setOnClickListener(view -> switchSection(SECTION_MEMBER));
        binding.navMarketingButton.setOnClickListener(view -> switchSection(SECTION_MARKETING));
        binding.navSettingButton.setOnClickListener(view -> switchSection(SECTION_SETTING));
        binding.clearOrderText.setOnClickListener(view -> {
            if (cashierService.getOrderItems().isEmpty()) {
                Toast.makeText(this, R.string.empty_order_toast, Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this, R.style.PosAlertDialog)
                    .setTitle(getString(R.string.clear_order_confirm_title))
                    .setMessage(getString(R.string.clear_order_confirm_message))
                    .setPositiveButton(R.string.clear_order, (dialog, which) -> {
                        cashierService.clearOrder();
                        refreshOrderPanel();
                        Toast.makeText(this, R.string.clear_success_toast, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        });
        binding.holdOrderText.setOnClickListener(view -> {
            if (cashierService.getOrderItems().isEmpty()) {
                Toast.makeText(this, R.string.empty_order_toast, Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this, R.style.PosAlertDialog)
                    .setTitle(getString(R.string.hold_order_confirm_title))
                    .setMessage(getString(R.string.hold_order_confirm_message))
                    .setPositiveButton(R.string.hold_order, (dialog, which) -> {
                        cashierService.holdCurrentOrder();
                        refreshOrderPanel();
                        updateHoldResumeButtons();
                        Toast.makeText(this, R.string.hold_success_toast, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        });
        binding.resumeOrderText.setOnClickListener(view -> {
            if (!cashierService.hasHeldOrder()) {
                Toast.makeText(this, R.string.no_held_order_toast, Toast.LENGTH_SHORT).show();
                return;
            }
            cashierService.resumeHeldOrder();
            refreshOrderPanel();
            updateHoldResumeButtons();
            Toast.makeText(this, R.string.resume_success_toast, Toast.LENGTH_SHORT).show();
        });
        binding.previewReceiptButton.setOnClickListener(view -> previewLatestReceipt());
        binding.wechatPayButton.setOnClickListener(view -> settleOrder(PaymentType.WECHAT));
        binding.alipayPayButton.setOnClickListener(view -> settleOrder(PaymentType.ALIPAY));
        binding.memberPayButton.setOnClickListener(view -> settleOrder(PaymentType.MEMBER));
        binding.morePayButton.setOnClickListener(view -> settleOrder(PaymentType.CASH));
    }

    private void setupModuleCards() {
        binding.moduleMemberCard.setOnClickListener(view ->
                Toast.makeText(this, R.string.module_coming_soon, Toast.LENGTH_SHORT).show());
        binding.moduleOperationCard.setOnClickListener(view ->
                Toast.makeText(this, R.string.module_coming_soon, Toast.LENGTH_SHORT).show());
    }

    private void setupSearch() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 不在输入前做处理，避免干扰快速检索体验。
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                refreshProductList();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 产品过滤在 onTextChanged 即时生效，保持输入反馈直接。
            }
        });
    }

    private void loadInitialData() {
        List<ProductCategory> categories = cashierService.getCategories();
        categoryAdapter.submitList(categories);
        if (!categories.isEmpty()) {
            selectedCategoryId = categories.get(0).getId();
            categoryAdapter.setSelectedCategoryId(selectedCategoryId);
            binding.categoryTitleText.setText(categories.get(0).getName());
        }
        refreshProductList();
    }

    private void updateTopBarClock() {
        binding.statusValueText.setText(new SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.CHINA
        ).format(System.currentTimeMillis()));
    }

    private void applyListLayout() {
        productLayoutManager.setSpanCount(LIST_SPAN_COUNT);
        productAdapter.setListMode(true);
    }

    private void switchSection(String section) {
        currentSection = section;
        boolean orderSelected = SECTION_ORDER.equals(section);
        binding.cashierPageContainer.setVisibility(orderSelected ? android.view.View.VISIBLE : android.view.View.GONE);
        binding.modulePageContainer.setVisibility(orderSelected ? android.view.View.GONE : android.view.View.VISIBLE);
        updateTopNavState();
        if (SECTION_MEMBER.equals(section)) {
            binding.moduleTitleText.setText(R.string.nav_member);
            binding.moduleDescText.setText(R.string.module_member_desc);
        } else if (SECTION_MARKETING.equals(section)) {
            binding.moduleTitleText.setText(R.string.nav_marketing);
            binding.moduleDescText.setText(R.string.module_card_operation_desc);
        } else if (SECTION_SETTING.equals(section)) {
            binding.moduleTitleText.setText(R.string.nav_setting);
            binding.moduleDescText.setText(R.string.module_setting_desc);
        }
        updateTopBarClock();
    }

    private void updateTopNavState() {
        updateNavButton(binding.navOrderButton, SECTION_ORDER.equals(currentSection));
        updateNavButton(binding.navMemberButton, SECTION_MEMBER.equals(currentSection));
        updateNavButton(binding.navMarketingButton, SECTION_MARKETING.equals(currentSection));
        updateNavButton(binding.navSettingButton, SECTION_SETTING.equals(currentSection));
    }

    private void updateNavButton(com.google.android.material.button.MaterialButton button, boolean selected) {
        button.setBackgroundTintList(getColorStateList(selected ? R.color.top_nav_selected : R.color.top_nav_unselected));
        button.setStrokeColorResource(selected ? R.color.top_nav_active_stroke : R.color.top_nav_stroke);
    }

    private void refreshProductList() {
        String keyword = binding.searchEditText.getText().toString().trim();
        List<Product> products = cashierService.getProducts(selectedCategoryId, keyword);
        productAdapter.submitList(products);
    }

    private void updateItemQuantity(OrderItem item, int delta) {
        cashierService.changeQuantity(item.getProduct().getId(), delta);
        refreshOrderPanel();
    }

    private void refreshOrderPanel() {
        List<OrderItem> items = cashierService.getOrderItems();
        OrderSummary summary = cashierService.getOrderSummary();
        orderAdapter.submitList(items);
        binding.emptyOrderText.setVisibility(items.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
        binding.orderCountValueText.setText(String.valueOf(summary.getItemCount()));
        binding.subtotalValueText.setText(CurrencyUtils.format(summary.getSubtotal()));
        binding.discountValueText.setText("-" + CurrencyUtils.format(summary.getDiscount()));
        binding.payableValueText.setText(CurrencyUtils.format(summary.getPayableAmount()));
        boolean canSettle = !items.isEmpty();
        binding.wechatPayButton.setEnabled(canSettle);
        binding.alipayPayButton.setEnabled(canSettle);
        binding.memberPayButton.setEnabled(canSettle);
        binding.morePayButton.setEnabled(canSettle);
        binding.previewReceiptButton.setEnabled(cashierService.getLatestReceipt() != null);
        binding.previewReceiptButton.setAlpha(cashierService.getLatestReceipt() != null ? 1F : 0.4F);
        updateHoldResumeButtons();
    }

    private void updateHoldResumeButtons() {
        boolean hasHeld = cashierService.hasHeldOrder();
        boolean hasItems = !cashierService.getOrderItems().isEmpty();
        binding.holdOrderText.setVisibility(hasItems ? View.GONE : View.GONE);
        binding.resumeOrderText.setVisibility(hasHeld ? View.GONE : View.GONE);
    }

    private void settleOrder(PaymentType paymentType) {
        if (cashierService.getOrderItems().isEmpty()) {
            Toast.makeText(this, R.string.empty_order_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        OrderSummary summary = cashierService.getOrderSummary();

        if (paymentType == PaymentType.CASH) {
            showCashPaymentDialog();
            return;
        }

        if (summary.getPayableAmount() >= HIGH_VALUE_THRESHOLD) {
            new AlertDialog.Builder(this, R.style.PosAlertDialog)
                    .setTitle(getString(R.string.settlement_confirm_title))
                    .setMessage(getString(R.string.settlement_confirm_high_value,
                            CurrencyUtils.format(summary.getPayableAmount())))
                    .setPositiveButton(R.string.settlement_confirm_positive, (d, w) ->
                            performSettlement(paymentType, summary.getPayableAmount()))
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        } else {
            performSettlement(paymentType, summary.getPayableAmount());
        }
    }

    private void performSettlement(PaymentType paymentType, int receivedAmount) {
        Receipt receipt = cashierService.checkout(paymentType, receivedAmount);
        showReceiptDialog(receipt, true);
        refreshOrderPanel();
    }

    private void showCashPaymentDialog() {
        DialogCashPaymentBinding cashBinding = DialogCashPaymentBinding.inflate(getLayoutInflater());
        OrderSummary summary = cashierService.getOrderSummary();
        int payableAmount = summary.getPayableAmount();

        cashBinding.payableAmountText.setText(CurrencyUtils.format(payableAmount));
        cashBinding.changeAmountText.setText(CurrencyUtils.format(0));

        cashBinding.receivedAmountInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int received = Integer.parseInt(s.toString().trim());
                    int change = Math.max(0, received - payableAmount);
                    cashBinding.changeAmountText.setText(CurrencyUtils.format(change));
                    cashBinding.changeAmountText.setTextColor(getColor(
                            change >= 0 ? R.color.primary_green : R.color.price_highlight));
                    cashBinding.confirmCashButton.setEnabled(received >= payableAmount);
                } catch (NumberFormatException e) {
                    cashBinding.changeAmountText.setText(CurrencyUtils.format(0));
                    cashBinding.confirmCashButton.setEnabled(false);
                }
            }
        });

        cashPaymentDialog = new AlertDialog.Builder(this)
                .setView(cashBinding.getRoot())
                .setCancelable(true)
                .create();

        cashBinding.cancelCashButton.setOnClickListener(v -> cashPaymentDialog.dismiss());

        cashBinding.confirmCashButton.setOnClickListener(v -> {
            try {
                int received = Integer.parseInt(
                        cashBinding.receivedAmountInput.getText().toString().trim());
                if (received < payableAmount) {
                    Toast.makeText(this, R.string.cash_insufficient_toast, Toast.LENGTH_SHORT).show();
                    return;
                }
                cashPaymentDialog.dismiss();
                performSettlement(PaymentType.CASH, received);
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.cash_invalid_toast, Toast.LENGTH_SHORT).show();
            }
        });

        cashBinding.receivedAmountInput.requestFocus();
        cashPaymentDialog.show();
    }

    private void showSpecSelectionDialog(Product product) {
        new AlertDialog.Builder(this, R.style.PosAlertDialog)
                .setTitle(product.getName())
                .setMessage(getString(R.string.spec_default_message, product.getSpecification()))
                .setPositiveButton(R.string.spec_confirm, (dialog, which) -> {
                    cashierService.addProduct(product);
                    refreshOrderPanel();
                    Toast.makeText(MainActivity.this,
                            getString(R.string.add_to_cart_feedback, product.getName()),
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void previewLatestReceipt() {
        Receipt latestReceipt = cashierService.getLatestReceipt();
        if (latestReceipt == null) {
            Toast.makeText(this, R.string.no_receipt_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        showReceiptDialog(latestReceipt, false);
    }

    private void showReceiptDialog(Receipt receipt, boolean justCompleted) {
        DialogReceiptPreviewBinding dialogBinding = DialogReceiptPreviewBinding.inflate(getLayoutInflater());
        dialogBinding.receiptContentText.setText(ReceiptFormatter.format(receipt));
        dialogBinding.receiptStatusText.setText(
                justCompleted ? getString(R.string.payment_complete_status) : getString(R.string.receipt_preview_status)
        );
        if (justCompleted) {
            dialogBinding.receiptStatusText.setTextColor(getColor(R.color.primary_green));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.closeReceiptButton.setOnClickListener(view -> dialog.dismiss());
        dialogBinding.printReceiptButton.setOnClickListener(view -> {
            Toast.makeText(this, R.string.print_success_toast, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        dialogBinding.continueOrderButton.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }
}
