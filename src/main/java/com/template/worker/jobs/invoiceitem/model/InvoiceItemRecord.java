package com.template.worker.jobs.invoiceitem.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvoiceItemRecord {
  private Long subId;
  private String invMonth;
  private String name;
  private String type;
  private String valueType;
  private Double value;
  private String targetScope;
  private LocalDateTime createdAt;
}
