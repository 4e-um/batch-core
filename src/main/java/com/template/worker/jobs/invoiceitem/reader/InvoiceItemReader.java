package com.template.worker.jobs.invoiceitem.reader;

import com.template.worker.jobs.invoiceitem.model.InvoiceItemRaw;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

@Component
public class InvoiceItemReader implements ItemReader<InvoiceItemRaw> {
    @Override
    public InvoiceItemRaw read()
            throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return null;
    }
}