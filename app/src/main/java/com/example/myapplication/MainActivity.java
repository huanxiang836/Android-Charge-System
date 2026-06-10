package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapter.CategoryAdapter;
import com.example.myapplication.adapter.ManageProductAdapter;
import com.example.myapplication.adapter.MemberAdapter;
import com.example.myapplication.adapter.OrderAdapter;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.databinding.DialogCashPaymentBinding;
import com.example.myapplication.databinding.DialogManageProductBinding;
import com.example.myapplication.databinding.DialogMemberCreateBinding;
import com.example.myapplication.databinding.DialogProductSpecBinding;
import com.example.myapplication.databinding.DialogReceiptPreviewBinding;
import com.example.myapplication.databinding.ItemComboCandidateBinding;
import com.example.myapplication.model.ComboItem;
import com.example.myapplication.model.Member;
import com.example.myapplication.model.MemberOperationConfig;
import com.example.myapplication.model.MemberStatistics;
import com.example.myapplication.model.OrderItem;
import com.example.myapplication.model.OrderSummary;
import com.example.myapplication.model.PaymentType;
import com.example.myapplication.model.Product;
import com.example.myapplication.model.ProductCategory;
import com.example.myapplication.model.ProductSpecSelection;
import com.example.myapplication.model.Receipt;
import com.example.myapplication.service.CashierService;
import com.example.myapplication.service.MemberService;
import com.example.myapplication.service.StoreCatalogService;
import com.example.myapplication.util.CurrencyUtils;
import com.example.myapplication.util.DiscountUtils;
import com.example.myapplication.util.ReceiptFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int LIST_SPAN_COUNT = 2;
    private static final int HIGH_VALUE_THRESHOLD = 100;
    private static final String SECTION_ORDER = "order";
    private static final String SECTION_MEMBER = "member";
    private static final String SECTION_MARKETING = "marketing";
    private static final String SECTION_SETTING = "setting";

    private ActivityMainBinding binding;
    private CashierService cashierService;
    private MemberService memberService;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private OrderAdapter orderAdapter;
    private ManageProductAdapter manageProductAdapter;
    private MemberAdapter memberAdapter;
    private GridLayoutManager productLayoutManager;
    private String selectedCategoryId;
    private String currentSection = SECTION_ORDER;
    private AlertDialog cashPaymentDialog;
    private Member selectedMember;

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
        setupManageList();
        setupMemberList();
        setupTopActions();
        setupModuleCards();
        setupMemberActions();
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
        memberService = new MemberService(this);
        cashierService = new CashierService(new StoreCatalogService(this), memberService);
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
                this::showSpecSelectionDialog,
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
                    cashierService.removeProduct(item.getOrderItemKey());
                    refreshOrderPanel();
                }
        );
        binding.orderRecyclerView.setAdapter(orderAdapter);
    }

    private void setupManageList() {
        binding.manageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        manageProductAdapter = new ManageProductAdapter(cashierService.getCategoryNameMap(),
                new ManageProductAdapter.OnManageProductActionListener() {
                    @Override
                    public void onToggle(Product product) {
                        boolean nextActive = !product.isActive();
                        cashierService.updateManageProductActive(product.getId(), nextActive);
                        refreshCategoryData();
                        refreshProductList();
                        refreshManagementPanel();
                        refreshOrderPanel();
                        Toast.makeText(
                                MainActivity.this,
                                getString(nextActive ? R.string.manage_enable_success : R.string.manage_disable_success),
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onEdit(Product product) {
                        showManageProductDialog(product);
                    }
                });
        binding.manageRecyclerView.setAdapter(manageProductAdapter);
        binding.manageAddProductButton.setOnClickListener(view -> showManageProductDialog(null));
    }

    private void setupMemberList() {
        binding.memberRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        memberAdapter = new MemberAdapter(member -> {
            selectedMember = member;
            refreshMemberDetailPanel();
        });
        binding.memberRecyclerView.setAdapter(memberAdapter);
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
                        refreshMemberDetailPanel();
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
                        refreshMemberDetailPanel();
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
            refreshMemberDetailPanel();
            updateHoldResumeButtons();
            Toast.makeText(this, R.string.resume_success_toast, Toast.LENGTH_SHORT).show();
        });
        binding.previewReceiptButton.setOnClickListener(view -> previewLatestReceipt());
        binding.wechatPayButton.setOnClickListener(view -> settleOrder(PaymentType.WECHAT));
        binding.alipayPayButton.setOnClickListener(view -> settleOrder(PaymentType.ALIPAY));
        binding.memberPayButton.setOnClickListener(view -> {
            MemberOperationConfig operationConfig = memberService.getOperationConfig();
            if (!operationConfig.isMemberPayEnabled()) {
                Toast.makeText(this, R.string.member_payment_disabled_toast, Toast.LENGTH_SHORT).show();
                return;
            }
            if (cashierService.getCurrentMember() == null) {
                Toast.makeText(this, R.string.member_pay_select_required_toast, Toast.LENGTH_SHORT).show();
                return;
            }
            settleOrder(PaymentType.MEMBER);
        });
        binding.morePayButton.setOnClickListener(view -> settleOrder(PaymentType.CASH));
        binding.removeCurrentMemberButton.setOnClickListener(view -> clearCurrentOrderMember());
    }

    private void setupModuleCards() {
        binding.moduleMemberCard.setOnClickListener(view -> {
            if (!SECTION_MEMBER.equals(currentSection)) {
                Toast.makeText(this, R.string.module_coming_soon, Toast.LENGTH_SHORT).show();
                return;
            }
            showMemberCenter();
        });
        binding.moduleOperationCard.setOnClickListener(view -> {
            if (!SECTION_MEMBER.equals(currentSection)) {
                Toast.makeText(this, R.string.module_coming_soon, Toast.LENGTH_SHORT).show();
                return;
            }
            showMemberOperationPanel();
        });
    }

    private void setupMemberActions() {
        binding.memberCenterBackButton.setOnClickListener(view -> showMemberModuleHome());
        binding.memberOperationBackButton.setOnClickListener(view -> showMemberModuleHome());
        binding.memberCreateButton.setOnClickListener(view -> showMemberCreateDialog());
        binding.memberBindCurrentOrderButton.setOnClickListener(view -> bindSelectedMemberToCurrentOrder());
        binding.memberClearCurrentOrderButton.setOnClickListener(view -> clearCurrentOrderMember());
        binding.memberOperationSaveButton.setOnClickListener(view -> saveMemberOperationConfig());
        binding.memberSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                refreshMemberList();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setupSearch() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                refreshProductList();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void loadInitialData() {
        refreshCategoryData();
        refreshProductList();
        refreshMemberList();
        refreshMemberOperationPanel();
    }

    private void refreshCategoryData() {
        List<ProductCategory> categories = cashierService.getCategories();
        categoryAdapter.submitList(categories);
        if (!categories.isEmpty()) {
            if (selectedCategoryId == null || !containsCategory(categories, selectedCategoryId)) {
                selectedCategoryId = categories.get(0).getId();
            }
            categoryAdapter.setSelectedCategoryId(selectedCategoryId);
            for (ProductCategory category : categories) {
                if (category.getId().equals(selectedCategoryId)) {
                    binding.categoryTitleText.setText(category.getName());
                    break;
                }
            }
        }
    }

    private boolean containsCategory(List<ProductCategory> categories, String categoryId) {
        for (ProductCategory category : categories) {
            if (category.getId().equals(categoryId)) {
                return true;
            }
        }
        return false;
    }

    private void updateTopBarClock() {
        binding.statusValueText.setText(new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
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
        binding.cashierPageContainer.setVisibility(orderSelected ? View.VISIBLE : View.GONE);
        binding.modulePageContainer.setVisibility(orderSelected ? View.GONE : View.VISIBLE);
        updateTopNavState();
        hideMemberSubPanels();
        binding.manageRecyclerView.setVisibility(View.GONE);
        binding.manageEmptyText.setVisibility(View.GONE);
        binding.manageAddProductButton.setVisibility(View.GONE);
        binding.moduleCardsContainer.setVisibility(View.GONE);

        if (SECTION_MEMBER.equals(section)) {
            binding.moduleTitleText.setText(R.string.nav_member);
            binding.moduleDescText.setText(R.string.module_member_desc);
            showMemberModuleHome();
            refreshMemberList();
            refreshMemberDetailPanel();
            refreshMemberOperationPanel();
        } else if (SECTION_MARKETING.equals(section)) {
            binding.moduleTitleText.setText(R.string.nav_marketing);
            binding.moduleDescText.setText(R.string.module_manage_desc);
            binding.manageAddProductButton.setVisibility(View.VISIBLE);
            refreshManagementPanel();
        } else if (SECTION_SETTING.equals(section)) {
            binding.moduleTitleText.setText(R.string.nav_setting);
            binding.moduleDescText.setText(R.string.module_setting_desc);
            binding.moduleCardsContainer.setVisibility(View.VISIBLE);
        }
        updateTopBarClock();
    }

    private void hideMemberSubPanels() {
        binding.memberCenterContainer.setVisibility(View.GONE);
        binding.memberOperationContainer.setVisibility(View.GONE);
    }

    private void showMemberModuleHome() {
        binding.moduleTitleText.setText(R.string.nav_member);
        binding.moduleDescText.setText(R.string.module_member_desc);
        binding.moduleCardsContainer.setVisibility(View.VISIBLE);
        binding.memberCenterContainer.setVisibility(View.GONE);
        binding.memberOperationContainer.setVisibility(View.GONE);
    }

    private void showMemberCenter() {
        binding.moduleTitleText.setText(R.string.module_card_member_title);
        binding.moduleDescText.setText(R.string.module_card_member_desc);
        binding.moduleCardsContainer.setVisibility(View.GONE);
        binding.memberCenterContainer.setVisibility(View.VISIBLE);
        binding.memberOperationContainer.setVisibility(View.GONE);
        refreshMemberList();
        refreshMemberDetailPanel();
    }

    private void showMemberOperationPanel() {
        binding.moduleTitleText.setText(R.string.module_card_operation_title);
        binding.moduleDescText.setText(R.string.member_operation_desc);
        binding.moduleCardsContainer.setVisibility(View.GONE);
        binding.memberCenterContainer.setVisibility(View.GONE);
        binding.memberOperationContainer.setVisibility(View.VISIBLE);
        refreshMemberOperationPanel();
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

    private void refreshManagementPanel() {
        List<Product> products = cashierService.getManageProducts();
        manageProductAdapter.submitList(products);
        binding.manageRecyclerView.setVisibility(products.isEmpty() ? View.GONE : View.VISIBLE);
        binding.manageEmptyText.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void refreshMemberList() {
        String keyword = binding.memberSearchEditText.getText() == null
                ? ""
                : binding.memberSearchEditText.getText().toString().trim();
        List<Member> members = memberService.searchMembers(keyword);
        if (selectedMember != null) {
            Member updatedSelectedMember = memberService.getMemberByPhone(selectedMember.getPhone());
            selectedMember = updatedSelectedMember;
        }
        if (selectedMember != null && !containsMember(members, selectedMember.getMemberId())) {
            selectedMember = null;
        }
        if (selectedMember == null && !members.isEmpty()) {
            selectedMember = members.get(0);
        }
        memberAdapter.submitList(members);
        memberAdapter.setSelectedMemberId(selectedMember == null ? null : selectedMember.getMemberId());
        binding.memberEmptyText.setVisibility(members.isEmpty() ? View.VISIBLE : View.GONE);
        refreshMemberDetailPanel();
    }

    private void refreshMemberDetailPanel() {
        if (selectedMember == null) {
            binding.memberDetailNameValueText.setText("--");
            binding.memberDetailPhoneValueText.setText("--");
            binding.memberDetailDiscountValueText.setText("--");
            binding.memberDetailTimeValueText.setText("--");
            binding.memberBindCurrentOrderButton.setEnabled(false);
        } else {
            binding.memberDetailNameValueText.setText(selectedMember.getName());
            binding.memberDetailPhoneValueText.setText(selectedMember.getPhone());
            binding.memberDetailDiscountValueText.setText(DiscountUtils.formatRate(selectedMember.getDiscountRate()));
            binding.memberDetailTimeValueText.setText(selectedMember.getCreateTime());
            binding.memberBindCurrentOrderButton.setEnabled(true);
        }
        Member currentMember = cashierService.getCurrentMember();
        binding.memberCurrentOrderStatusText.setText(currentMember == null
                ? getString(R.string.member_none_bound)
                : getString(R.string.member_bound_status_format, currentMember.getName(), currentMember.getPhone()));
        binding.memberClearCurrentOrderButton.setEnabled(currentMember != null);
    }

    private void refreshMemberOperationPanel() {
        MemberOperationConfig operationConfig = memberService.getOperationConfig();
        binding.memberOperationDiscountInput.setText(String.format(Locale.CHINA, "%.1f", operationConfig.getDefaultDiscountRate() / 10.0F));
        binding.memberCreationCheckBox.setChecked(operationConfig.isMemberCreationEnabled());
        binding.memberPayCheckBox.setChecked(operationConfig.isMemberPayEnabled());

        MemberStatistics statistics = memberService.getTodayStatistics();
        binding.memberTotalCountText.setText(String.valueOf(statistics.getTotalMemberCount()));
        binding.memberNewTodayCountText.setText(String.valueOf(statistics.getTodayNewMemberCount()));
        binding.memberOrderTodayCountText.setText(String.valueOf(statistics.getTodayMemberOrderCount()));
        binding.memberDiscountTodayText.setText(CurrencyUtils.format(statistics.getTodayMemberDiscountAmount()));
        binding.memberCreateButton.setEnabled(operationConfig.isMemberCreationEnabled());
        refreshOrderPanel();
    }

    private void updateItemQuantity(OrderItem item, int delta) {
        cashierService.changeQuantity(item.getOrderItemKey(), delta);
        refreshOrderPanel();
    }

    private void refreshOrderPanel() {
        List<OrderItem> items = cashierService.getOrderItems();
        OrderSummary summary = cashierService.getOrderSummary();
        MemberOperationConfig operationConfig = memberService.getOperationConfig();
        orderAdapter.submitList(items);
        binding.emptyOrderText.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        binding.orderCountValueText.setText(String.valueOf(summary.getItemCount()));
        binding.subtotalValueText.setText(CurrencyUtils.format(summary.getSubtotal()));
        binding.promotionDiscountValueText.setText("-" + CurrencyUtils.format(summary.getPromotionDiscount()));
        binding.memberDiscountValueText.setText("-" + CurrencyUtils.format(summary.getMemberDiscount()));
        binding.discountValueText.setText("-" + CurrencyUtils.format(summary.getTotalDiscount()));
        binding.payableValueText.setText(CurrencyUtils.format(summary.getPayableAmount()));
        boolean canSettle = !items.isEmpty();
        binding.wechatPayButton.setEnabled(canSettle);
        binding.alipayPayButton.setEnabled(canSettle);
        binding.memberPayButton.setEnabled(canSettle && operationConfig.isMemberPayEnabled());
        binding.morePayButton.setEnabled(canSettle);
        binding.previewReceiptButton.setEnabled(cashierService.getLatestReceipt() != null);
        binding.previewReceiptButton.setAlpha(cashierService.getLatestReceipt() != null ? 1F : 0.4F);
        updateCurrentMemberSummary(summary);
        updateHoldResumeButtons();
    }

    private void updateCurrentMemberSummary(OrderSummary summary) {
        binding.currentMemberInfoText.setText(summary.getMemberDisplayText());
        if (summary.isMemberApplied()) {
            binding.currentMemberDiscountText.setText(getString(
                    R.string.member_current_order_discount_format,
                    DiscountUtils.formatRate(cashierService.getCurrentMember().getDiscountRate()),
                    CurrencyUtils.format(summary.getMemberDiscount())
            ));
            binding.removeCurrentMemberButton.setVisibility(View.VISIBLE);
        } else {
            binding.currentMemberDiscountText.setText(R.string.member_order_action_hint);
            binding.removeCurrentMemberButton.setVisibility(View.GONE);
        }
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
        refreshMemberDetailPanel();
        refreshMemberOperationPanel();
    }

    private void showCashPaymentDialog() {
        DialogCashPaymentBinding cashBinding = DialogCashPaymentBinding.inflate(getLayoutInflater());
        OrderSummary summary = cashierService.getOrderSummary();
        int payableAmount = summary.getPayableAmount();

        cashBinding.payableAmountText.setText(CurrencyUtils.format(payableAmount));
        cashBinding.changeAmountText.setText(CurrencyUtils.format(0));

        cashBinding.receivedAmountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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
        DialogProductSpecBinding specBinding = DialogProductSpecBinding.inflate(getLayoutInflater());
        specBinding.specProductNameText.setText(product.getName());
        specBinding.specProductPriceText.setText(CurrencyUtils.format(product.getPrice()));
        if (product.isCombo()) {
            specBinding.specComboSummaryText.setVisibility(View.VISIBLE);
            specBinding.specComboSummaryText.setText("套餐内容：" + product.getComboSummary());
            specBinding.specSizeContainer.setVisibility(View.GONE);
            specBinding.specTemperatureContainer.setVisibility(View.GONE);
            specBinding.specSweetnessContainer.setVisibility(View.GONE);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(specBinding.getRoot())
                .create();

        specBinding.specCancelButton.setOnClickListener(view -> dialog.dismiss());
        specBinding.specConfirmButton.setOnClickListener(view -> {
            ProductSpecSelection specSelection = product.isCombo()
                    ? ProductSpecSelection.createComboSelection(product.getComboSummary())
                    : new ProductSpecSelection(
                    resolveSelectedSize(specBinding),
                    resolveSelectedTemperature(specBinding),
                    resolveSelectedSweetness(specBinding)
            );
            cashierService.addProduct(product, specSelection);
            refreshOrderPanel();
            Toast.makeText(
                    MainActivity.this,
                    getString(R.string.add_to_cart_feedback, product.getName()),
                    Toast.LENGTH_SHORT
            ).show();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showManageProductDialog(Product editingProduct) {
        DialogManageProductBinding manageBinding = DialogManageProductBinding.inflate(getLayoutInflater());
        Map<String, String> categoryNameMap = cashierService.getCategoryNameMap();
        List<String> categoryLabels = new ArrayList<>(categoryNameMap.values());
        List<String> categoryIds = new ArrayList<>(categoryNameMap.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryLabels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        manageBinding.manageCategorySpinner.setAdapter(adapter);
        boolean editingCombo = editingProduct != null && editingProduct.isCombo();
        if (editingCombo) {
            manageBinding.manageDialogTitleText.setText(R.string.manage_combo_edit);
            manageBinding.manageConfirmButton.setText(R.string.manage_combo_edit);
            manageBinding.manageTypeComboButton.setChecked(true);
            manageBinding.manageTypeNormalButton.setEnabled(false);
            manageBinding.manageTypeComboButton.setEnabled(false);
            manageBinding.manageProductNameInput.setText(editingProduct.getName());
            manageBinding.manageProductPriceInput.setText(String.valueOf(editingProduct.getPrice()));
        }
        toggleManageDialogMode(manageBinding, editingCombo);

        List<Product> candidateProducts = cashierService.getComboCandidateProducts();
        Map<String, Integer> selectedQuantityMap = new java.util.LinkedHashMap<>();
        if (editingCombo) {
            for (ComboItem comboItem : editingProduct.getComboItems()) {
                selectedQuantityMap.put(comboItem.getChildProductId(), comboItem.getQuantity());
            }
        }
        buildComboCandidateViews(manageBinding, candidateProducts, selectedQuantityMap);

        manageBinding.manageTypeGroup.setOnCheckedChangeListener((group, checkedId) ->
                toggleManageDialogMode(
                        manageBinding,
                        checkedId == manageBinding.manageTypeComboButton.getId()
                ));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(manageBinding.getRoot())
                .create();

        manageBinding.manageCancelButton.setOnClickListener(view -> dialog.dismiss());
        manageBinding.manageConfirmButton.setOnClickListener(view -> {
            String name = manageBinding.manageProductNameInput.getText() == null
                    ? ""
                    : manageBinding.manageProductNameInput.getText().toString().trim();
            String priceText = manageBinding.manageProductPriceInput.getText() == null
                    ? ""
                    : manageBinding.manageProductPriceInput.getText().toString().trim();
            if (name.isEmpty() || priceText.isEmpty()) {
                Toast.makeText(this, R.string.manage_invalid_input, Toast.LENGTH_SHORT).show();
                return;
            }
            int price = Integer.parseInt(priceText);
            boolean comboMode = manageBinding.manageTypeComboButton.isChecked();
            if (comboMode) {
                List<ComboItem> comboItems = buildSelectedComboItems(candidateProducts, selectedQuantityMap);
                if (comboItems.isEmpty()) {
                    Toast.makeText(this, R.string.manage_combo_select_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editingCombo) {
                    cashierService.updateManageComboProduct(editingProduct.getId(), name, price, comboItems);
                    Toast.makeText(this, R.string.manage_combo_update_success, Toast.LENGTH_SHORT).show();
                } else {
                    cashierService.addManageComboProduct(name, price, comboItems);
                    Toast.makeText(this, R.string.manage_add_success, Toast.LENGTH_SHORT).show();
                }
            } else {
                String categoryId = categoryIds.get(manageBinding.manageCategorySpinner.getSelectedItemPosition());
                cashierService.addManageNormalProduct(categoryId, name, price);
                Toast.makeText(this, R.string.manage_add_success, Toast.LENGTH_SHORT).show();
            }
            refreshCategoryData();
            refreshProductList();
            refreshManagementPanel();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showMemberCreateDialog() {
        MemberOperationConfig operationConfig = memberService.getOperationConfig();
        if (!operationConfig.isMemberCreationEnabled()) {
            Toast.makeText(this, R.string.member_creation_disabled_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        DialogMemberCreateBinding dialogBinding = DialogMemberCreateBinding.inflate(getLayoutInflater());
        dialogBinding.memberCreateDiscountHintText.setText(getString(
                R.string.member_create_discount_hint,
                DiscountUtils.formatRate(operationConfig.getDefaultDiscountRate())
        ));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .create();

        dialogBinding.memberCreateCancelButton.setOnClickListener(view -> dialog.dismiss());
        dialogBinding.memberCreateConfirmButton.setOnClickListener(view -> {
            String phone = dialogBinding.memberCreatePhoneInput.getText() == null
                    ? ""
                    : dialogBinding.memberCreatePhoneInput.getText().toString().trim();
            String name = dialogBinding.memberCreateNameInput.getText() == null
                    ? ""
                    : dialogBinding.memberCreateNameInput.getText().toString().trim();
            if (!isPhoneValid(phone)) {
                Toast.makeText(this, R.string.member_phone_invalid, Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.isEmpty()) {
                Toast.makeText(this, R.string.member_name_invalid, Toast.LENGTH_SHORT).show();
                return;
            }
            if (memberService.getMemberByPhone(phone) != null) {
                Toast.makeText(this, R.string.member_exists_toast, Toast.LENGTH_SHORT).show();
                return;
            }
            selectedMember = memberService.createMember(phone, name);
            refreshMemberList();
            refreshMemberOperationPanel();
            Toast.makeText(this, R.string.member_create_success_toast, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void toggleManageDialogMode(DialogManageProductBinding manageBinding, boolean comboMode) {
        manageBinding.manageNormalCategoryContainer.setVisibility(comboMode ? View.GONE : View.VISIBLE);
        manageBinding.manageComboContainer.setVisibility(comboMode ? View.VISIBLE : View.GONE);
        manageBinding.manageComboSummaryText.setVisibility(comboMode ? View.VISIBLE : View.GONE);
    }

    private void buildComboCandidateViews(
            DialogManageProductBinding manageBinding,
            List<Product> candidateProducts,
            Map<String, Integer> selectedQuantityMap
    ) {
        manageBinding.manageComboItemsContainer.removeAllViews();
        for (Product candidateProduct : candidateProducts) {
            ItemComboCandidateBinding itemBinding = ItemComboCandidateBinding.inflate(getLayoutInflater(),
                    manageBinding.manageComboItemsContainer, false);
            String nameText = candidateProduct.isActive()
                    ? candidateProduct.getName()
                    : candidateProduct.getName() + getString(R.string.manage_inactive_suffix);
            itemBinding.comboCandidateCheckBox.setText(nameText);
            int initialQuantity = selectedQuantityMap.containsKey(candidateProduct.getId())
                    ? selectedQuantityMap.get(candidateProduct.getId()) : 0;
            itemBinding.comboCandidateCheckBox.setChecked(initialQuantity > 0);
            itemBinding.comboCandidateQuantityText.setText(String.valueOf(initialQuantity));
            updateCandidateButtons(itemBinding, initialQuantity > 0);
            itemBinding.comboCandidateCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int quantity = isChecked ? Math.max(1, selectedQuantityMap.getOrDefault(candidateProduct.getId(), 1)) : 0;
                selectedQuantityMap.put(candidateProduct.getId(), quantity);
                itemBinding.comboCandidateQuantityText.setText(String.valueOf(quantity));
                updateCandidateButtons(itemBinding, isChecked);
                updateComboSummaryText(manageBinding, selectedQuantityMap);
            });
            itemBinding.comboCandidatePlusButton.setOnClickListener(view -> {
                if (!itemBinding.comboCandidateCheckBox.isChecked()) {
                    itemBinding.comboCandidateCheckBox.setChecked(true);
                    return;
                }
                int quantity = selectedQuantityMap.getOrDefault(candidateProduct.getId(), 1) + 1;
                selectedQuantityMap.put(candidateProduct.getId(), quantity);
                itemBinding.comboCandidateQuantityText.setText(String.valueOf(quantity));
                updateComboSummaryText(manageBinding, selectedQuantityMap);
            });
            itemBinding.comboCandidateMinusButton.setOnClickListener(view -> {
                int currentQuantity = selectedQuantityMap.getOrDefault(candidateProduct.getId(), 0);
                int nextQuantity = Math.max(0, currentQuantity - 1);
                selectedQuantityMap.put(candidateProduct.getId(), nextQuantity);
                itemBinding.comboCandidateQuantityText.setText(String.valueOf(nextQuantity));
                if (nextQuantity == 0) {
                    itemBinding.comboCandidateCheckBox.setChecked(false);
                } else {
                    updateComboSummaryText(manageBinding, selectedQuantityMap);
                }
            });
            manageBinding.manageComboItemsContainer.addView(itemBinding.getRoot());
        }
        updateComboSummaryText(manageBinding, selectedQuantityMap);
    }

    private void updateCandidateButtons(ItemComboCandidateBinding itemBinding, boolean enabled) {
        itemBinding.comboCandidateMinusButton.setEnabled(enabled);
        itemBinding.comboCandidatePlusButton.setEnabled(enabled);
    }

    private void updateComboSummaryText(DialogManageProductBinding manageBinding, Map<String, Integer> selectedQuantityMap) {
        int selectedKinds = 0;
        int totalQuantity = 0;
        for (int quantity : selectedQuantityMap.values()) {
            if (quantity > 0) {
                selectedKinds++;
                totalQuantity += quantity;
            }
        }
        manageBinding.manageComboSummaryText.setText(getString(
                R.string.manage_combo_summary_format,
                selectedKinds,
                totalQuantity
        ));
    }

    private List<ComboItem> buildSelectedComboItems(List<Product> candidateProducts, Map<String, Integer> selectedQuantityMap) {
        List<ComboItem> comboItems = new ArrayList<>();
        for (Product candidateProduct : candidateProducts) {
            int quantity = selectedQuantityMap.getOrDefault(candidateProduct.getId(), 0);
            if (quantity > 0) {
                comboItems.add(new ComboItem(
                        candidateProduct.getId(),
                        candidateProduct.getName(),
                        quantity
                ));
            }
        }
        return comboItems;
    }

    private String resolveSelectedSize(DialogProductSpecBinding specBinding) {
        if (specBinding.sizeLargeButton.isChecked()) {
            return getString(R.string.spec_size_large);
        }
        return getString(R.string.spec_size_medium);
    }

    private String resolveSelectedTemperature(DialogProductSpecBinding specBinding) {
        if (specBinding.temperatureHotButton.isChecked()) {
            return getString(R.string.spec_temperature_hot);
        }
        if (specBinding.temperatureNormalButton.isChecked()) {
            return getString(R.string.spec_temperature_normal);
        }
        return getString(R.string.spec_temperature_iced);
    }

    private String resolveSelectedSweetness(DialogProductSpecBinding specBinding) {
        if (specBinding.sweetnessLessButton.isChecked()) {
            return getString(R.string.spec_sweetness_less);
        }
        if (specBinding.sweetnessLightButton.isChecked()) {
            return getString(R.string.spec_sweetness_light);
        }
        return getString(R.string.spec_sweetness_regular);
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

    private void bindSelectedMemberToCurrentOrder() {
        if (selectedMember == null) {
            Toast.makeText(this, R.string.member_select_required_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        cashierService.bindCurrentMember(selectedMember);
        refreshOrderPanel();
        refreshMemberDetailPanel();
        Toast.makeText(this, R.string.member_bound_success_toast, Toast.LENGTH_SHORT).show();
    }

    private void clearCurrentOrderMember() {
        cashierService.clearCurrentMember();
        refreshOrderPanel();
        refreshMemberDetailPanel();
        Toast.makeText(this, R.string.member_cleared_success_toast, Toast.LENGTH_SHORT).show();
    }

    private void saveMemberOperationConfig() {
        String discountText = binding.memberOperationDiscountInput.getText() == null
                ? ""
                : binding.memberOperationDiscountInput.getText().toString().trim();
        int discountRate = parseDiscountRate(discountText);
        if (discountRate < MemberService.MIN_DISCOUNT_RATE || discountRate > MemberService.MAX_DISCOUNT_RATE) {
            Toast.makeText(this, R.string.member_operation_discount_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        memberService.updateOperationConfig(
                discountRate,
                binding.memberCreationCheckBox.isChecked(),
                binding.memberPayCheckBox.isChecked()
        );
        refreshMemberOperationPanel();
        Toast.makeText(this, R.string.member_operation_save_success, Toast.LENGTH_SHORT).show();
    }

    private int parseDiscountRate(String discountText) {
        try {
            float discountValue = Float.parseFloat(discountText);
            return Math.round(discountValue * 10);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean isPhoneValid(String phone) {
        return phone != null && phone.matches("^1\\d{10}$");
    }

    private boolean containsMember(List<Member> members, String memberId) {
        for (Member member : members) {
            if (member.getMemberId().equals(memberId)) {
                return true;
            }
        }
        return false;
    }
}
