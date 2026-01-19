package com.template.worker.jobs.invoice.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.template.worker.jobs.invoice.model.InvoiceItemRow;

@Component
public class InvoiceReader implements ItemReader<InvoiceItemRow> {
    @Override
    public InvoiceItemRow read()
            throws Exception,
                    UnexpectedInputException,
                    ParseException,
                    NonTransientResourceException {
        return null;
    }
}
