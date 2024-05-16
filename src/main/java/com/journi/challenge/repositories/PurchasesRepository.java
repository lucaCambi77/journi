package com.journi.challenge.repositories;

import com.journi.challenge.models.Purchase;
import com.journi.challenge.models.PurchaseStats;

import javax.inject.Named;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@Singleton
public class PurchasesRepository {

  private final List<Purchase> allPurchases = new ArrayList<>();
  private final Map<String, PurchaseStats> map = new HashMap<>();
  private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE.withZone(ZoneId.of("UTC"));

  public List<Purchase> list() {
    return allPurchases;
  }

  /**
   * Save a purchase and add to a map where we store the day and the purchase static. We can get
   * statistics in a constant time by getting a specific day which will contain the sum of all
   * previous days
   *
   * @param purchase
   */
  public void save(Purchase purchase) {
    allPurchases.add(purchase);

    PurchaseStats purchaseStats =
        map.getOrDefault(
            formatter.format(purchase.getTimestamp().minusDays(1)), new PurchaseStats());

    map.put(
        formatter.format(purchase.getTimestamp()),
        new PurchaseStats(
            formatter.format(purchase.getTimestamp()),
            formatter.format(purchase.getTimestamp()),
            1L + purchaseStats.getCountPurchases(),
            purchase.getTotalValue() + purchaseStats.getTotalAmount(),
            null,
            Math.min(purchase.getTotalValue(), purchaseStats.getMinAmount()),
            Math.max(purchase.getTotalValue(), purchaseStats.getMaxAmount())));
  }

  /**
   * Return stats of the last 30 days. It should run in O(1) time, but we also consider the gaps for
   * missing values
   *
   * @return stats of the last 30 days
   */
  public PurchaseStats getLast30DaysStats() {

    PurchaseStats oldest = first();
    PurchaseStats oldestMinusOne =
        map.getOrDefault(
            formatter.format(LocalDate.now().atStartOfDay().minusDays(31)), new PurchaseStats());
    PurchaseStats mostRecent = last();

    Double totalAmount =
        mostRecent.getTotalAmount() - oldestMinusOne.getTotalAmount() + first().getTotalAmount();
    Long countPurchase =
        mostRecent.getCountPurchases()
            - oldestMinusOne.getCountPurchases()
            + first().getCountPurchases();
    return new PurchaseStats(
        oldest.getFrom(),
        mostRecent.getFrom(),
        countPurchase,
        totalAmount,
        totalAmount / countPurchase,
        Math.min(mostRecent.getMinAmount(), oldest.getTotalAmount()),
        Math.max(mostRecent.getMaxAmount(), oldest.getMaxAmount()));
  }

  /**
   * Get the most recent available stats. It should run in O(1) time, but we simulate the missing
   * values
   *
   * @return most recent available stats
   */
  private PurchaseStats last() {
    PurchaseStats stats = null;
    LocalDateTime today = LocalDate.now().atStartOfDay();
    LocalDateTime todayMinus30 = LocalDate.now().atStartOfDay().minusDays(30);

    while (today.isAfter(todayMinus30) && stats == null) {
      stats = map.get(formatter.format(today));
      today = today.minusDays(1);
    }

    return stats;
  }

  /**
   * Get the oldest available stats. It should run in O(1) time, but we simulate the missing values
   *
   * @return oldest available stats
   */
  private PurchaseStats first() {
    PurchaseStats stats = null;
    LocalDateTime today = LocalDate.now().atStartOfDay();
    LocalDateTime todayMinus30 = LocalDate.now().atStartOfDay().minusDays(30);

    while (todayMinus30.isBefore(today) && stats == null) {
      stats = map.get(formatter.format(todayMinus30));
      todayMinus30 = todayMinus30.plusDays(1);
    }

    return stats;
  }
}
