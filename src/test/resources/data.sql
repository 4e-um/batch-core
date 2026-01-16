-- =========================
-- invoice_item 테스트 데이터
-- =========================

-- sub_id = 1001
INSERT INTO invoice_item (
    sub_id, inv_month, name, type, value_type, "value", created_at
) VALUES
      (1001, '202508', '기본요금', 'CHARGE', 'AMOUNT', 10000, CURRENT_TIMESTAMP),
      (1001, '202508', '프로모션할인', 'DISCOUNT', 'AMOUNT', -2000, CURRENT_TIMESTAMP);

-- sub_id = 1002
INSERT INTO invoice_item (
    sub_id, inv_month, name, type, value_type, "value", created_at
) VALUES
    (1002, '202508', '기본요금', 'CHARGE', 'AMOUNT', 15000, CURRENT_TIMESTAMP);
