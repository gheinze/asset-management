package com.accounted4.assetmanager.ui.loan;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Window;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author gheinze
 */
public class PdfScheduleWindow extends Window {

    private static final AtomicInteger PDFS_GENERATED = new AtomicInteger(0);


    public PdfScheduleWindow(StreamResource.StreamSource stream) {
        super("Amortization Schedule");
        init(stream);
    }

    private void init(StreamResource.StreamSource stream) {

        String resourceName = String.format("AmortizationSchedule_%s.pdf", Integer.toString(PDFS_GENERATED.incrementAndGet()));
        StreamResource streamResource = new StreamResource(stream, resourceName);
        streamResource.setMIMEType("application/pdf");
        streamResource.setCacheTime(0);  // dynamically generated, file name may have no meaning and so we should not cache by file name

        BrowserFrame browserFrame = new BrowserFrame(resourceName, streamResource);
        browserFrame.setSizeFull();

        setResizable(true);
        setPosition(30,30);
        setHeight("90%");
        setWidth("90%");
        setContent(browserFrame);

    }


}
