package com.journi.challenge.repositories;

import com.journi.challenge.models.Purchase;
import com.journi.challenge.models.PurchaseStats;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class PurchasesRepository {

  private List<Purchase> allPurchases = new ArrayList<>();
  private Map<String, PurchaseStats> map = new HashMap<>();
  private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE.withZone(ZoneId.of("UTC"));
  private List<LocalDateTime> periods = new ArrayList<>();

  public void reset() {
    allPurchases = new ArrayList<>();
    periods = new ArrayList<>();
    map = new HashMap<>();
  }

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
    saveStatistics(purchase);
  }

  private void saveStatistics(Purchase purchase) {
    Collections.sort(periods);

    PurchaseStats purchaseStats = map.get(formatter.format(purchase.getTimestamp()));

    // if i can't find any stats, i'll add current purchase. Otherwise, i'll add the current
    // purchase to the existing stats
    if (purchaseStats == null) {
      map.put(
          formatter.format(purchase.getTimestamp()),
          new PurchaseStats(
              formatter.format(purchase.getTimestamp()),
              formatter.format(purchase.getTimestamp()),
              1L,
              purchase.getTotalValue(),
              null,
              purchase.getTotalValue(),
              purchase.getTotalValue()));

    } else {
      map.put(
          formatter.format(purchase.getTimestamp()),
          new PurchaseStats(
              formatter.format(purchase.getTimestamp()),
              purchaseStats.getTo(),
              1L + purchaseStats.getCountPurchases(),
              purchase.getTotalValue() + purchaseStats.getTotalAmount(),
              null,
              Math.min(purchase.getTotalValue(), purchaseStats.getMinAmount()),
              Math.max(purchase.getTotalValue(), purchaseStats.getMaxAmount())));
    }

    // Iterate over the next stats as this current purchase will span until today
    LocalDateTime tomorrow = LocalDate.now().atStartOfDay().plusDays(1);
    LocalDateTime p = purchase.getTimestamp().plusDays(1);

    LocalDateTime minPeriod = periods.isEmpty() ? LocalDateTime.now() : periods.get(0);
    LocalDateTime maxPeriod =
        periods.isEmpty() ? LocalDateTime.now() : periods.get(periods.size() - 1);
    while (p.isBefore(tomorrow)) {

      purchaseStats = map.get(formatter.format(p));

      // this will happen only for the first iterations of nulls
      if (purchaseStats == null) {
        map.put(
            formatter.format(p),
            new PurchaseStats(
                formatter.format(purchase.getTimestamp()),
                formatter.format(minPeriod), // set to the minimum period available
                1L,
                purchase.getTotalValue(),
                null,
                purchase.getTotalValue(),
                purchase.getTotalValue()));
      } else {
        map.put(
            formatter.format(p),
            new PurchaseStats(
                purchaseStats.getFrom(),
                formatter.format(maxPeriod), // set to the latest period available
                purchaseStats.getCountPurchases() + 1,
                purchaseStats.getTotalAmount() + purchase.getTotalValue(),
                null,
                Math.min(purchase.getTotalValue(), purchaseStats.getMinAmount()),
                Math.max(purchase.getTotalValue(), purchaseStats.getMaxAmount())));
      }

      p = p.plusDays(1);
    }

    periods.add(purchase.getTimestamp());
  }

  /**
   * Return stats of the last 30 days. It should run in O(1) time
   *
   * @return stats of the last 30 days
   */
  public PurchaseStats getLast30DaysStats() {

    PurchaseStats oldest =
        map.getOrDefault(
            formatter.format(LocalDate.now().atStartOfDay().minusDays(30)), new PurchaseStats());
    PurchaseStats mostRecent =
        map.getOrDefault(formatter.format(LocalDate.now().atStartOfDay()), new PurchaseStats());

    Double totalAmount = mostRecent.getTotalAmount() - oldest.getTotalAmount();
    Long countPurchase = mostRecent.getCountPurchases() - oldest.getCountPurchases();
    return new PurchaseStats(
        mostRecent.getFrom(),
        mostRecent.getTo(),
        countPurchase,
        totalAmount,
        totalAmount / countPurchase,
        Math.min(mostRecent.getMinAmount(), oldest.getTotalAmount()),
        Math.max(mostRecent.getMaxAmount(), oldest.getMaxAmount()));
  }
}
