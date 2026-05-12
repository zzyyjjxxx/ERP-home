package com.erp.controller;

import com.erp.common.response.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> result = new HashMap<>();

        // Today's sales amount
        BigDecimal todaySales = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(total_amount), 0) FROM sal_order WHERE order_date = CURDATE() AND del_flag = 0", BigDecimal.class);
        result.put("todaySales", todaySales != null ? todaySales : BigDecimal.ZERO);

        // Today's purchase amount
        BigDecimal todayPurchase = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(total_amount), 0) FROM pur_order WHERE order_date = CURDATE() AND del_flag = 0", BigDecimal.class);
        result.put("todayPurchase", todayPurchase != null ? todayPurchase : BigDecimal.ZERO);

        // This month's sales
        BigDecimal monthSales = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(total_amount), 0) FROM sal_order WHERE DATE_FORMAT(order_date, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m') AND del_flag = 0", BigDecimal.class);
        result.put("monthSales", monthSales != null ? monthSales : BigDecimal.ZERO);

        // This month's purchases
        BigDecimal monthPurchase = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(total_amount), 0) FROM pur_order WHERE DATE_FORMAT(order_date, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m') AND del_flag = 0", BigDecimal.class);
        result.put("monthPurchase", monthPurchase != null ? monthPurchase : BigDecimal.ZERO);

        // Pending orders count
        Integer pendingOrders = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM sal_order WHERE status = 1 AND del_flag = 0", Integer.class);
        result.put("pendingOrders", pendingOrders != null ? pendingOrders : 0);

        // Low stock products count
        Integer lowStock = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM inv_stock s INNER JOIN inv_product p ON s.product_id = p.id WHERE s.quantity <= p.min_stock AND p.del_flag = 0", Integer.class);
        result.put("lowStockCount", lowStock != null ? lowStock : 0);

        // Receivables total
        BigDecimal receivables = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(balance), 0) FROM fin_receivable WHERE status IN (0,1) AND del_flag = 0", BigDecimal.class);
        result.put("receivablesTotal", receivables != null ? receivables : BigDecimal.ZERO);

        // Payables total
        BigDecimal payables = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(balance), 0) FROM fin_payable WHERE status IN (0,1) AND del_flag = 0", BigDecimal.class);
        result.put("payablesTotal", payables != null ? payables : BigDecimal.ZERO);

        // Recent 30 days sales trend
        List<Map<String, Object>> salesTrend = jdbcTemplate.queryForList(
            "SELECT DATE(order_date) as date, COALESCE(SUM(total_amount), 0) as amount FROM sal_order WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) AND del_flag = 0 GROUP BY DATE(order_date) ORDER BY date");
        result.put("salesTrend", salesTrend);

        return Result.ok(result);
    }
}
