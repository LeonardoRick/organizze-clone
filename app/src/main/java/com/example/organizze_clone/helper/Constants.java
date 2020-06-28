package com.example.organizze_clone.helper;

public class Constants {
    public static final String KEY_SELECTED_FAB_ID = "KEY_SELECTED_FAB_ID";
    public static final String KEY_PROFIT_FAB_ID = "KEY_PROFIT_FAB_ID";
    public static final String KEY_SPENDING_FAB_ID = "KEY_SPENDING_FAB_ID";
    public static final String KEY_CURRENT_MONTH = "KEY_CURRENT_MONTH";

    public static class UserNode {
        public static final String KEY = "users"; // name of main user node on database
        public static final String NAME = "name";
        public static final String TOTAL_SPENDING = "totalSpending";
        public static final String TOTAL_PROFIT = "totalProfit";
    }

    public static class TransactionNode {
        public static final String KEY = "transaction"; // name of transaction main node on database
        public static final String DATE = "date";
        public static final String DESC = "desc";
        public static final String CATEGORY = "title";
        public static final String TYPE = "type";
        public static final String VALUE = "value";

        public static final String PROFIT = "profit";
        public static final String SPENDING = "spending";
    }

}
