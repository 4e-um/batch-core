package com.template.worker.jobs.invoice.reader;

import com.template.worker.jobs.invoice.model.InvoiceItemRow;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

@Component
public class InvoiceReader implements ItemReader<InvoiceItemRow> {
    @Override
    public InvoiceItemRow read()
            throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return null;
    }
}