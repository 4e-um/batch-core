package com.template.worker.jobs.invoiceSend.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemWriter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.template.worker.jobs.invoiceSend.model.InvoiceAggregateRecord;
import com.template.worker.jobs.invoiceSend.processor.InvoiceSendProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceSendWriter implements ItemWriter<InvoiceAggregateRecord>, ItemStream {

    private static final String TOPIC = "invoice-noti";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final InvoiceSendProcessor processor;

    private InvoiceAggregateRecord lastBuffered;

    @Override
    public void write(Chunk<? extends InvoiceAggregateRecord> items) throws Exception {

        for (InvoiceAggregateRecord invoice : items) {
            send(invoice);
        }

        lastBuffered = processor.flushLast();
    }

    @Override
    public void close() {

        if (lastBuffered == null) {
            return;
        }

        try {
            send(lastBuffered);
            kafkaTemplate.flush();
        } catch (Exception e) {
            throw new RuntimeException("Failed to flush last invoice", e);
        } finally {
            lastBuffered = null;
        }
    }

    private void send(InvoiceAggregateRecord invoice) throws Exception {
        String payload = objectMapper.writeValueAsString(invoice);
        kafkaTemplate.send(TOPIC, String.valueOf(invoice.invId()), payload);
    }
}
